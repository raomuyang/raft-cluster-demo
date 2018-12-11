package cn.atomicer.raft;

import cn.atomicer.raft.annotation.Status;
import cn.atomicer.raft.exception.StatusChangeException;
import cn.atomicer.raft.mock.MockManagers;
import cn.atomicer.raft.proto.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Rao Mengnan
 * on 2018/11/22.
 */
public class NodeSynchronizeTest {
    private MockManagers.NodeRPC nodeRPC;
    private MockManagers.LocalEntryManager entryManager;
    private MockManagers.StatusManager statusManager;
    private MockManagers.MockTermManager termManager;
    private MockManagers.OnLogAppend onLogAppend;

    @Before
    public void before() {
        nodeRPC = new MockManagers.NodeRPC();
        entryManager = new MockManagers.LocalEntryManager();
        statusManager = new MockManagers.StatusManager();
        termManager = new MockManagers.MockTermManager();
        onLogAppend = new MockManagers.OnLogAppend(entryManager);
    }

    @Test
    public void testMatchLogIndexWithNormal() {

        // Follower本地无entry条目
        // leader:   0, 1, 2, 3, 4, 5, 6, 7, 8, 9
        // follower:
        entryManager.entryList = mockEntryList(10);
        EntriesSynchronize synchronize = new EntriesSynchronize(
                nodeRPC, entryManager, statusManager, termManager, onLogAppend);
        long matchedIndex = synchronize.matchLogIndex();
        assertEquals(-1, matchedIndex);
        assertEquals(0, synchronize.getNextIndex());

        // Follower本地有5条条目
        // leader:   0, 1, 2, 3, 4, 5, 6, 7, 8, 9
        // follower: 0, 1, 2, 3, 4
        synchronize = new EntriesSynchronize(
                nodeRPC, entryManager, statusManager, termManager, onLogAppend);
        nodeRPC.entryList = mockEntryList(5);
        matchedIndex = synchronize.matchLogIndex();
        assertEquals(4, matchedIndex);
        assertEquals(5, synchronize.getNextIndex());
    }

    @Test
    public void testMatchLogIndexWithOverwriteResult() {

        // Follower本地有5条条目, 但有两条是冲突的
        // leader:   0, 1, 2, 3, 4, 5, 6, 7, 8, 9
        // follower: 0, 1, 2, 3(t2), 4(t2)

        entryManager.entryList = mockEntryList(10);
        nodeRPC.entryList = mockEntryList(3);
        nodeRPC.entryList.add(EntryInfo.newBuilder().setLogIndex(3).setCreateTerm(2).build());
        nodeRPC.entryList.add(EntryInfo.newBuilder().setLogIndex(4).setCreateTerm(2).build());

        EntriesSynchronize synchronize = new EntriesSynchronize(
                nodeRPC, entryManager, statusManager, termManager, onLogAppend);
        long matchedIndex = synchronize.matchLogIndex();
        assertEquals(2, matchedIndex);
        assertEquals(3, synchronize.getNextIndex());
    }

    @Test(expected = StatusChangeException.class)
    public void testMatchLogFailedCauseTermLessThanResponse() {
        entryManager.entryList = mockEntryList(10);
        nodeRPC.entryList = new ArrayList<>();
        termManager.term = 1;
        nodeRPC.term = 2;

        EntriesSynchronize synchronize = new EntriesSynchronize(
                nodeRPC, entryManager, statusManager, termManager, onLogAppend);
        synchronize.matchLogIndex();
    }

    @Test
    public void testMatchLogFailedCauseTermLessThanResponseAndStatusChange() {
        entryManager.entryList = mockEntryList(10);
        nodeRPC.entryList = new ArrayList<>();
        termManager.term = 1;
        nodeRPC.term = 2;

        try {
            EntriesSynchronize synchronize = new EntriesSynchronize(
                    nodeRPC, entryManager, statusManager, termManager, onLogAppend);
            synchronize.matchLogIndex();
        } catch (StatusChangeException e) {
            assertEquals(Status.FOLLOWER, statusManager.status);
        }
    }

    @Test
    public void testAppendEntriesToAnNotEmptyNode() {
        // Follower本地无entry条目
        // leader:   0, 1, 2, 3, 4, 5, 6, 7, 8, 9
        // follower: 0, 1, 2, 3, 4
        entryManager.entryList = mockEntryList(10);
        nodeRPC.entryList = mockEntryList(5);
        EntriesSynchronize synchronize = new EntriesSynchronize(
                nodeRPC, entryManager, statusManager, termManager, onLogAppend);
        boolean res = synchronize.appendNext();
        assertFalse(res);

        long matched = synchronize.matchLogIndex();
        assertEquals(4, matched);
        res = synchronize.appendNext();
        assertTrue(res);
        assertEquals(6, nodeRPC.entryList.size());
        assertEquals(entryManager.getEntry(5), nodeRPC.entryList.get(5));
    }

    @Test
    public void testAppendEntriesToAnEmptyNode() {
        // Follower本地无entry条目
        // leader:   0, 1, 2, 3, 4, 5, 6, 7, 8, 9
        // follower:
        entryManager.entryList = mockEntryList(10);
        nodeRPC.entryList = new ArrayList<>();
        EntriesSynchronize synchronize = new EntriesSynchronize(
                nodeRPC, entryManager, statusManager, termManager, onLogAppend);
        boolean res = synchronize.appendNext();
        assertFalse(res);

        long matched = synchronize.matchLogIndex();
        assertEquals(-1, matched);
        res = synchronize.appendNext();
        assertTrue(res);
        assertEquals(1, nodeRPC.entryList.size());
        assertEquals(entryManager.getEntry(0), nodeRPC.entryList.get(0));
    }

    @Test
    public void testAppendEntriesWithConflict() {
        // Follower本地有5条条目, 但有两条是冲突的
        // leader:   0, 1, 2, 3, 4, 5, 6, 7, 8, 9
        // follower: 0, 1, 2, 3(t2), 4(t2)

        entryManager.entryList = mockEntryList(10);
        nodeRPC.entryList = mockEntryList(3);
        nodeRPC.entryList.add(EntryInfo.newBuilder().setLogIndex(3).setCreateTerm(2).build());
        nodeRPC.entryList.add(EntryInfo.newBuilder().setLogIndex(4).setCreateTerm(2).build());
        EntriesSynchronize synchronize = new EntriesSynchronize(
                nodeRPC, entryManager, statusManager, termManager, onLogAppend);

        boolean res = synchronize.appendNext();
        assertFalse(res);

        long matched = synchronize.matchLogIndex();
        assertEquals(2, matched);
        res = synchronize.appendNext();
        assertTrue(res);

        assertEquals(entryManager.getEntry(3), nodeRPC.entryList.get(3));
        assertNotEquals(entryManager.getEntry(4), nodeRPC.entryList.get(4));
    }

    @Test
    public void testSyncAllEntries() {
        // Follower本地有5条条目, 但有两条是冲突的
        // leader:   0, 1, 2, 3, 4, 5, 6, 7, 8, 9
        // follower: 0, 1, 2, 3(t2), 4(t2)

        entryManager.entryList = mockEntryList(10);
        nodeRPC.entryList = mockEntryList(3);
        nodeRPC.entryList.add(EntryInfo.newBuilder().setLogIndex(3).setCreateTerm(2).build());
        nodeRPC.entryList.add(EntryInfo.newBuilder().setLogIndex(4).setCreateTerm(2).build());
        EntriesSynchronize synchronize = new EntriesSynchronize(
                nodeRPC, entryManager, statusManager, termManager, onLogAppend);
        synchronize.doSync();
        assertEquals(entryManager.entryList, nodeRPC.entryList);
        assertEquals(entryManager.entryList.size() - 1, entryManager.committed);
    }

    @Test
    public void testSyncWithEmptyEntries() {
        EntriesSynchronize synchronize = new EntriesSynchronize(
                nodeRPC, entryManager, statusManager, termManager, onLogAppend);
        synchronize.doSync();
        assertEquals(entryManager.entryList, nodeRPC.entryList);
    }

    @Test(expected = StatusChangeException.class)
    public void testMatchLogIndexFailedCauseStatusNotLeader() {
        EntriesSynchronize synchronize = new EntriesSynchronize(
                nodeRPC, entryManager, statusManager, termManager, onLogAppend);
        statusManager.status = Status.FOLLOWER;
        synchronize.matchLogIndex();
    }


    @Test(expected = StatusChangeException.class)
    public void testMatchLogIndexFailedCauseCommittedIndexOutOfDate() {
        EntriesSynchronize synchronize = new EntriesSynchronize(
                nodeRPC, entryManager, statusManager, termManager, onLogAppend);
        statusManager.status = Status.LEADER;

        entryManager.entryList = mockEntryList(10);
        nodeRPC.entryList = mockEntryList(3);
        nodeRPC.committedIndex = 1;
        synchronize.matchLogIndex();
    }

    private List<EntryInfo> mockEntryList(int size) {
        List<EntryInfo> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(EntryInfo.newBuilder().setLogIndex(i).build());
        }
        return list;
    }


}