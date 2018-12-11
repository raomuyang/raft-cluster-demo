package cn.atomicer.raft.manager;

import cn.atomicer.raft.EntryManager;
import cn.atomicer.raft.Node;
import cn.atomicer.raft.StatusManager;
import cn.atomicer.raft.TermManager;
import cn.atomicer.raft.annotation.Status;
import cn.atomicer.raft.model.Configuration;
import cn.atomicer.raft.model.NodeInfo;
import cn.atomicer.raft.proto.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Rao Mengnan
 * on 2018/11/18.
 */
public class NodeManager implements Node {

    private static Log logger = LogFactory.getLog(NodeManager.class);

    private TermManager termManager;
    private EntryManager entryManager;
    private StatusManager statusManager;

    private NodeInfo localhost;

    public NodeManager(Configuration config, TermManager termManager,
                       EntryManager entryManager, StatusManager statusManager) {
        this.termManager = termManager;
        this.entryManager = entryManager;
        this.statusManager = statusManager;
        this.localhost = config.getNodeInfo();
    }

    @Override
    public String getId() {
        return localhost.getName();
    }

    @Override
    public Response appendEntries(AppendEntriesRequest request) {
        String logMessage = String.format("Node %s request append entries, term %s, prev log index: %s, prev log term: %s",
                request.getLeaderId(), request.getTerm(), request.getPrevLogIndex(), request.getTerm());
        logger.debug(logMessage);
        MessageType type;
        boolean termPass = checkAndUpdateTerm(request.getTerm());
        if (!termPass) {
            // leader.term >= follower.term
            type = MessageType.DENY;
        } else if (!checkTermOfLogIndex(request.getPrevLogIndex(), request.getPrevLogTerm())) {
            // last term of log must be equal
            type = MessageType.DENY;
        } else if (request.getLeaderCommit() < entryManager.getCommittedIndex()) {
            type = MessageType.DENY;
        } else {
            logger.debug("(Access) " + logMessage);
            // 更新leader信息，更新beat timestamp，清空票箱
            termManager.setLeaderId(request.getLeaderId());
            statusManager.accept(Status.FOLLOWER);
            type = MessageType.OK;
            // 提交日志
            entryManager.appendEntries(request.getEntriesList(), request.getLeaderCommit());
        }

        return Response
                .newBuilder()
                .setType(type)
                .setTerm(termManager.getTerm())
                .build();
    }

    @Override
    public Response requestVote(Vote vote) {
        boolean voteGranted = termManager.checkAndDetermineVote(vote);
        return Response
                .newBuilder()
                .setType(voteGranted ? MessageType.OK : MessageType.DENY)
                .setTerm(termManager.getTerm())
                .build();
    }

    private boolean checkTermOfLogIndex(long logIndex, long term) {
        // logIndex < 0时，entryManager返回的entry info中的index始终为-1：NULL_ENTRY_INFO
        EntryInfo localEntry = entryManager.getEntry(logIndex);
        return localEntry != null && localEntry.getCreateTerm() == term;
    }

    private boolean checkAndUpdateTerm(long term) {
        if (term < termManager.getTerm()) {
            return false;
        }

        if (term > termManager.getTerm()) {
            termManager.updateTerm(term);
            statusManager.accept(Status.FOLLOWER);
        }

        return true;
    }


}
