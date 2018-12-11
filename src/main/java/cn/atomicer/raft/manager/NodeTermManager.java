package cn.atomicer.raft.manager;

import cn.atomicer.raft.TermManager;
import cn.atomicer.raft.annotation.Status;
import cn.atomicer.raft.proto.EntryInfo;
import cn.atomicer.raft.proto.Vote;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author Rao Mengnan
 * on 2018/11/18.
 */
public class NodeTermManager implements TermManager {

    private AtomicReference<String> votedFor;
    private AtomicLong currentTerm;
    private Supplier<EntryInfo> latestLogSupplier;
    private Consumer<Status> onStatusChange;
    private String leaderId;

    public NodeTermManager(Supplier<EntryInfo> latestLogSupplier, Consumer<Status> onStatusChange) {
        this.latestLogSupplier = latestLogSupplier;
        this.votedFor = new AtomicReference<>();
        this.currentTerm = new AtomicLong(0);
        this.onStatusChange = onStatusChange != null ? onStatusChange : (s) -> {
        };
    }

    @Override
    public void resetVote() {
        this.votedFor.set(null);
    }

    @Override
    public boolean checkAndDetermineVote(Vote vote) {

        // 如果 term < currentTerm返回 false
        if (vote.getTerm() < this.currentTerm.get()) {
            return false;
        } else if (vote.getTerm() > this.currentTerm.get()) {
            // 选票中的任期大于当前任期，更新自己的任期信息，转化为Follower状态
            this.updateTerm(vote.getTerm());
            this.resetVote();
            onStatusChange.accept(Status.FOLLOWER);
        }

        // 如果votedFor为空或者与candidateId相同，并且候选人的日志和自己的日志一样新，则给该候选人投票
        if (compareWithLocal(vote.getLastEntryInfo(), latestLogSupplier.get()) < 0) {
            return false;
        }

        // voteGranted
        return this.votedFor.compareAndSet(null, vote.getNodeId())
                || this.votedFor.get().equals(vote.getNodeId());
    }

    @Override
    public long getTerm() {
        return this.currentTerm.get();
    }

    @Override
    public void updateTerm(long term) {
        this.currentTerm.accumulateAndGet(term, (o, l) -> o > l ? o : l);
    }

    @Override
    public void increaseTerm() {
        this.currentTerm.addAndGet(1);
        this.resetVote();
    }

    @Override
    public String getLeaderId() {
        return leaderId;
    }

    @Override
    public void setLeaderId(String leaderId) {
        this.leaderId = leaderId;
    }

    AtomicReference<String> getVotedFor() {
        return votedFor;
    }

    private int compareWithLocal(EntryInfo entriesInfo, EntryInfo lastLogInfo) {
        // 如果两个日志的任期号不同，任期号大的为新；如果任期号相同，更长的日志为新。
        if (lastLogInfo == null) {
            return 1;
        }

        int compareTerm = Long.compare(entriesInfo.getCreateTerm(),
                lastLogInfo.getCreateTerm());
        int compareIndex = Long.compare(entriesInfo.getLogIndex(),
                lastLogInfo.getLogIndex());

        if (compareTerm > 0 || (compareTerm == 0 && compareIndex > 0)) {
            return 1;
        } else if (compareTerm < 0 || compareIndex < 0) {
            return -1;
        }
        return 0;
    }
}
