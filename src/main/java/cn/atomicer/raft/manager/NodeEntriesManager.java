package cn.atomicer.raft.manager;

import cn.atomicer.raft.EntryManager;
import cn.atomicer.raft.exception.IndexConflictException;
import cn.atomicer.raft.proto.EntryInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Rao Mengnan
 * on 2018/11/24.
 */
public class NodeEntriesManager implements EntryManager {
    private Map<Long, EntryInfo> cachedEntries;
    private AtomicLong committedIndex;

    public NodeEntriesManager() {
        this.cachedEntries = new ConcurrentHashMap<>();
        this.committedIndex = new AtomicLong(-1);
    }

    @Override
    public void appendEntries(List<EntryInfo> entries, long leaderCommitIndex) {
        if (committedIndex.get() > leaderCommitIndex) {
            throw new IndexConflictException(String.format("Index conflict with local: %s, request: %s",
                    committedIndex, leaderCommitIndex));
        }
        if (entries.size() > 0) {
            entries.forEach(e -> cachedEntries.put(e.getLogIndex(), e));
        }

        this.updateCommittedIndex(leaderCommitIndex);

    }

    @Override
    public EntryInfo getEntry(long index) {
        EntryInfo info = cachedEntries.get(index);
        return info == null ? EntryManager.NULL_ENTRY_INFO : info;
    }

    @Override
    public EntryInfo getLatestLog() {
        return cachedEntries.get((long) cachedEntries.size() - 1);
    }

    @Override
    public long getCommittedIndex() {
        return committedIndex.get();
    }

    @Override
    public void updateCommittedIndex(long index) {
        this.committedIndex.accumulateAndGet(index,
                (oldIdx, newIdx) -> oldIdx > newIdx ? oldIdx: newIdx);
    }

}
