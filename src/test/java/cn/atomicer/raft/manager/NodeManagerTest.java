package cn.atomicer.raft.manager;

import cn.atomicer.raft.EntryManager;
import cn.atomicer.raft.TermManager;
import cn.atomicer.raft.mock.MockManagers;
import cn.atomicer.raft.model.Configuration;
import cn.atomicer.raft.model.NodeInfo;
import cn.atomicer.raft.proto.*;
import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.*;

/**
 * @author Rao Mengnan
 * on 2018/11/21.
 */
public class NodeManagerTest {
    private MockManagers.LocalEntryManager entryManager;
    private MockManagers.StatusManager statusManager;

    private TermManager termManager;
    private Configuration config;
    private NodeManager nodeManager;

    @Before
    public void before() {
        entryManager = new MockManagers.LocalEntryManager();
        statusManager = new MockManagers.StatusManager();
        termManager = new NodeTermManager(()->entryManager.getLatestLog(), statusManager);

        config = new Configuration();
        config.setNodeInfo(new NodeInfo());
        config.getNodeInfo().setName("test-node-id");

        nodeManager = new NodeManager(config, termManager, entryManager, statusManager);
    }

    @Test
    public void testAcceptFirstAppendEntriesRequest() {
        assertEquals(0, termManager.getTerm());
        entryManager.committed =  -1;
        AppendEntriesRequest request = AppendEntriesRequest
                .newBuilder()
                .setLeaderId("test-leader")
                .setPrevLogIndex(-1)
                .setPrevLogTerm(-1)
                .setLeaderCommit(-1)
                .setTerm(1)
                .build();
        Response response = nodeManager.appendEntries(request);
        assertEquals(MessageType.OK, response.getType());
        assertEquals(-1, entryManager.committed);
        assertEquals(1, termManager.getTerm());
    }

    @Test
    public void testDenyAppendEntriesRequestCauseTermLessThanCurrent() {
        assertEquals(0, termManager.getTerm());
        termManager.increaseTerm();
        AppendEntriesRequest request = AppendEntriesRequest
                .newBuilder()
                .setLeaderId("test-leader")
                .setPrevLogIndex(-1)
                .setPrevLogTerm(-1)
                .setLeaderCommit(-1)
                .setTerm(0)
                .build();
        Response response = nodeManager.appendEntries(request);
        assertEquals(MessageType.DENY, response.getType());
        assertEquals(-1, entryManager.committed);
    }

    @Test
    public void testDenyAppendEntriesRequestCauseCommittedIndexLessThanCurrent() {
        assertEquals(0, termManager.getTerm());

        entryManager.updateCommittedIndex(1);
        AppendEntriesRequest request = AppendEntriesRequest
                .newBuilder()
                .setLeaderId("test-leader")
                .setPrevLogIndex(-1)
                .setPrevLogTerm(-1)
                .setLeaderCommit(-1)
                .setTerm(0)
                .build();
        Response response = nodeManager.appendEntries(request);
        assertEquals(MessageType.DENY, response.getType());
        assertEquals(1, entryManager.committed);
    }

    @Test
    public void requestVote() {
        EntryInfo last = EntryManager.NULL_ENTRY_INFO;
        Vote vote = Vote.newBuilder()
                .setLastEntryInfo(last)
                .setTerm(1)
                .setNodeId("test-leader")
                .build();

        Response response = nodeManager.requestVote(vote);
        assertEquals(MessageType.OK, response.getType());
        assertEquals(1, termManager.getTerm());
    }

}