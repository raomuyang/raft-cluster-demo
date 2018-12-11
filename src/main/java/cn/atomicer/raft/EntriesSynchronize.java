package cn.atomicer.raft;

import cn.atomicer.raft.annotation.Status;
import cn.atomicer.raft.exception.StatusChangeException;
import cn.atomicer.raft.proto.AppendEntriesRequest;
import cn.atomicer.raft.proto.EntryInfo;
import cn.atomicer.raft.proto.MessageType;
import cn.atomicer.raft.proto.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.function.BiConsumer;

/**
 * @author Rao Mengnan
 * on 2018/11/21.
 */
public class EntriesSynchronize {
    private static Log logger = LogFactory.getLog(EntriesSynchronize.class);

    private Node node;
    private EntryManager entryManager;
    private StatusManager statusManager;
    private TermManager termManager;

    private BiConsumer<String, Long> onLogAppendSuccess;

    private long nextIndex;
    private long matchedIndex;

    public EntriesSynchronize(Node node, EntryManager entryManager, StatusManager statusManager,
                              TermManager termManager, BiConsumer<String, Long> onLogAppendSuccess) {
        this.node = node;
        this.entryManager = entryManager;
        this.statusManager = statusManager;
        this.termManager = termManager;
        this.onLogAppendSuccess = onLogAppendSuccess;
        this.initIndex();
    }

    public void doSync() {
        try {
            matchLogIndex();
            logger.info(String.format("Match index with node (%s) successfully, matched: %s",
                    node.getId(), matchedIndex));

            EntryInfo newer = entryManager.getLatestLog();
            if (newer == null) {
                logger.debug("None entries yet, wait for next cycle");
                return;
            }
            while (nextIndex < newer.getLogIndex() + 1) {
                boolean access = appendNext();
                if (access) {
                    onLogAppendSuccess.accept(node.getId(), ++matchedIndex);
                    nextIndex++;
                } else {
                    // 无限重试
                    logger.warn(String.format("Append failed (node: %s), retry again", node.getId()));
                }
            }
        } catch (StatusChangeException e) {
            logger.info(String.format("%s - (node: %s)", e.getMessage(), node.getId()));
        }
    }

    boolean appendNext() {
        EntryInfo prevEntry = entryManager.getEntry(matchedIndex);
        if (prevEntry == null) prevEntry = EntryManager.NULL_ENTRY_INFO;

        EntryInfo nextEntry = entryManager.getEntry(nextIndex);

        AppendEntriesRequest.Builder requestBuilder = AppendEntriesRequest.newBuilder()
                .setPrevLogIndex(prevEntry.getLogIndex())
                .setPrevLogTerm(prevEntry.getCreateTerm())
                .setLeaderCommit(entryManager.getCommittedIndex())
                .setTerm(termManager.getTerm());
        if (nextEntry != null) {
            requestBuilder.addEntries(0, nextEntry);
        }

        Response response;
        try {
            response = node.appendEntries(requestBuilder.build());
        } catch (Throwable e) {
            logger.warn("AppendEntries RPC exception", e);
            return false;
        }

        if (response.getTerm() > termManager.getTerm()) {
            // throw
            changeStatusAndThrow(response);
        }
        logger.info(String.format("AppendEntries RPC, result: %s", response));
        return response.getType() == MessageType.OK;
    }

    long matchLogIndex() {
        // 每次match时entries为空，相当于一次heartbeat
        // log index: [0, n)
        while (true) {
            if (statusManager.getStatus() != Status.LEADER) {
                throw new StatusChangeException(
                        String.format("Status changed, stop append entries: %s", statusManager.getStatus()));
            }

            if (nextIndex > 0) --nextIndex;
            long tryMatchIndex = nextIndex - 1;
            EntryInfo tryMatchEntry = entryManager.getEntry(tryMatchIndex);
            AppendEntriesRequest request = AppendEntriesRequest.newBuilder()
                    .setPrevLogIndex(tryMatchEntry.getLogIndex())
                    .setPrevLogTerm(tryMatchEntry.getCreateTerm())
                    .setLeaderCommit(entryManager.getCommittedIndex())
                    .setTerm(termManager.getTerm())
                    .build();

            Response response = node.appendEntries(request);
            if (response.getType() == MessageType.OK) {
                // matched, matchIndex == nextIndex - 1 == node.logIndex
                // nextIndex >= 0
                break;
            } else if (response.getTerm() > termManager.getTerm()) {
                changeStatusAndThrow(response);
            } else if (nextIndex == 0) {
                statusManager.accept(Status.FOLLOWER);
                throw new StatusChangeException("Index [-1] matched failed, it means the newest committed index was out of date");
            }
        }

        this.matchedIndex = nextIndex - 1;  //  matchedIndex >= -1
        return this.matchedIndex;
    }

    private void changeStatusAndThrow(Response response) {
        termManager.updateTerm(response.getTerm());
        statusManager.accept(Status.FOLLOWER);
        throw new StatusChangeException(
                String.format("Status changed, stop append entries: %s", statusManager.getStatus()));
    }

    long getNextIndex() {
        return nextIndex;
    }

    void initIndex() {
        // 将nextIndex初始化为它的最新的日志条目索引数+1
        EntryInfo last = entryManager.getLatestLog();
        if (last == null) {
            this.nextIndex = 0;
            this.matchedIndex = -1;
            return;
        }
        this.nextIndex = last.getLogIndex() + 1;
        this.matchedIndex = last.getLogIndex();
    }

    public String getId() {
        return node.getId();
    }
}
