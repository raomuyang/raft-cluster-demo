package cn.atomicer.raft;

import cn.atomicer.raft.proto.EntryInfo;

import java.util.List;

/**
 * @author Rao Mengnan
 * on 2018/11/19.
 */
public interface EntryManager {

    EntryInfo NULL_ENTRY_INFO = EntryInfo.newBuilder().setCreateTerm(-1).setLogIndex(-1).build();

    void appendEntries(List<EntryInfo> entries, long leaderCommitIndex);

    EntryInfo getEntry(long index);

    EntryInfo getLatestLog();

    long getCommittedIndex();

    void updateCommittedIndex(long index);
}
