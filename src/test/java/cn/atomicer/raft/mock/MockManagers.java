package cn.atomicer.raft.mock;

import cn.atomicer.raft.EntryManager;
import cn.atomicer.raft.Node;
import cn.atomicer.raft.TermManager;
import cn.atomicer.raft.annotation.Status;
import cn.atomicer.raft.proto.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * @author Rao Mengnan
 * on 2018-12-10.
 */
public class MockManagers {
    public static class OnLogAppend implements BiConsumer<String, Long> {
        public EntryManager entryManager;

        public OnLogAppend(EntryManager entryManager) {
            this.entryManager = entryManager;
        }

        @Override
        public void accept(String aString, Long index) {
            entryManager.updateCommittedIndex(index);
        }
    }

    public static class NodeRPC implements Node {
        public long committedIndex = -1;
        public long term;
        public List<EntryInfo> entryList = new ArrayList<>();

        @Override
        public String getId() {
            return "test node";
        }

        /**
         * 模拟EntryManager追加日志（只做最基本的检查，不关注commit等）
         */
        @Override
        public Response appendEntries(AppendEntriesRequest request) {
            Response ok = Response.newBuilder().setTerm(term).setType(MessageType.OK).build();
            Response deny = Response.newBuilder().setTerm(term).setType(MessageType.DENY).build();

            Response response;

            if (request.getPrevLogIndex() >= entryList.size()) {
                // 本地不存在，直接deny
                response = deny;
            } else if (committedIndex > request.getLeaderCommit()) {
                // 本地提交大于request，deny
                response = deny;
            } else if (request.getPrevLogIndex() == -1 && entryList.size() == 0) {
                // 本地没有任何记录，通过
                response = ok;
            } else {
                // 相同index下的log的create term必须相同
                EntryInfo local = entryList.get((int) request.getPrevLogIndex());
                if (local == null || local.getCreateTerm() != request.getPrevLogTerm()) {
                    response = deny;
                } else {
                    response = ok;
                }
            }

            // request中日志存在时，append到日志列表
            if (request.getEntriesCount() == 1 && response == ok) {
                EntryInfo newEntry = request.getEntries(0);
                if (newEntry.getLogIndex() < entryList.size()) {
                    entryList.set((int) newEntry.getLogIndex(), newEntry);
                } else if (newEntry.getLogIndex() == entryList.size()) {
                    entryList.add(newEntry);
                } else {
                    throw new IndexOutOfBoundsException();
                }
            }

            return response;
        }

        @Override
        public Response requestVote(Vote vote) {
            return null;
        }
    }

    public static class LocalEntryManager implements EntryManager {
        public List<EntryInfo> entryList = new ArrayList<>();
        public long committed = -1;

        @Override
        public void appendEntries(List<EntryInfo> entries, long leaderCommitIndex) {
            // pass
        }

        @Override
        public EntryInfo getEntry(long index) {
            if (index < 0) return EntryManager.NULL_ENTRY_INFO;
            else if (index >= entryList.size()) return null;
            return entryList.get((int) index);
        }

        @Override
        public EntryInfo getLatestLog() {
            if (entryList.size() > 0) return entryList.get(entryList.size() - 1);
            else return null;
        }

        @Override
        public long getCommittedIndex() {
            return committed;
        }

        @Override
        public void updateCommittedIndex(long index) {
            this.committed = index > this.committed ? index : this.committed;
        }
    }

    public static class StatusManager implements cn.atomicer.raft.StatusManager {
        public Status status = Status.LEADER;

        @Override
        public Status getStatus() {
            return status;
        }

        @Override
        public void accept(Status status) {
            this.status = status;
        }
    }

    public static class MockTermManager implements TermManager {
        public long term;

        @Override
        public void resetVote() {

        }

        @Override
        public boolean checkAndDetermineVote(Vote vote) {
            return false;
        }

        @Override
        public long getTerm() {
            return term;
        }

        @Override
        public void updateTerm(long term) {
            this.term = term;
        }

        @Override
        public void increaseTerm() {

        }

        @Override
        public String getLeaderId() {
            return null;
        }

        @Override
        public void setLeaderId(String leaderId) {

        }

    }
}
