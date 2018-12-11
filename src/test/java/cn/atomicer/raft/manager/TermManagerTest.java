package cn.atomicer.raft.manager;

import cn.atomicer.raft.annotation.Status;
import cn.atomicer.raft.proto.EntryInfo;
import cn.atomicer.raft.proto.Vote;
import org.junit.Test;

import java.util.function.Consumer;

import static org.junit.Assert.*;

/**
 * @author Rao Mengnan
 * on 2018/11/21.
 */
public class TermManagerTest {

    @Test
    public void testIncreaseTerm() {
        NodeTermManager manager = new NodeTermManager(EntryInfo::getDefaultInstance, status -> {
        });
        assertEquals(0, manager.getTerm());
        manager.increaseTerm();
        assertEquals(1, manager.getTerm());
    }
    @Test
    public void resetVote() {
        NodeTermManager manager = new NodeTermManager(EntryInfo::getDefaultInstance, status -> {
        });
        manager.getVotedFor().set("test");
        manager.resetVote();
        assertNull(manager.getVotedFor().get());
    }

    @Test
    public void checkAndDetermineVoteOkWithNewerEntry() {
        EntryInfo paramEntry, fixed;
        NodeTermManager manager;
        Vote vote;
        StatusConsumer consumer = new StatusConsumer();
        fixed = EntryInfo.newBuilder().setCreateTerm(5).setLogIndex(4).build();
        paramEntry = EntryInfo.newBuilder().setCreateTerm(5).setLogIndex(5).build();

        // accept
        manager = new NodeTermManager(() -> fixed, consumer);
        vote = Vote.newBuilder()
                .setNodeId("test")
                .setTerm(10)
                .setLastEntryInfo(paramEntry)
                .build();
        boolean res = manager.checkAndDetermineVote(vote);
        assertTrue(res);
        assertEquals(Status.FOLLOWER, consumer.status);
        assertEquals(vote.getTerm(), manager.getTerm());
        assertEquals("test", manager.getVotedFor().get());
    }

    @Test
    public void checkAndDetermineVoteOkWithSelfInfo() {
        EntryInfo fixed;
        NodeTermManager manager;
        Vote vote;
        StatusConsumer consumer = new StatusConsumer();
        fixed = EntryInfo.newBuilder().setCreateTerm(5).setLogIndex(4).build();

        // accept
        manager = new NodeTermManager(() -> fixed, consumer);
        vote = Vote.newBuilder()
                .setNodeId("test")
                .setTerm(0)
                .setLastEntryInfo(fixed)
                .build();
        boolean res = manager.checkAndDetermineVote(vote);
        assertTrue(res);
        assertEquals(vote.getTerm(), manager.getTerm());
        assertEquals("test", manager.getVotedFor().get());
    }

    @Test
    public void checkAndDetermineVoteOkWithTwoTimes() {
        EntryInfo paramEntry, fixed;
        NodeTermManager manager;
        Vote vote;
        StatusConsumer consumer = new StatusConsumer();
        fixed = EntryInfo.newBuilder().setCreateTerm(5).setLogIndex(4).build();
        paramEntry = EntryInfo.newBuilder().setCreateTerm(5).setLogIndex(5).build();

        // accept
        manager = new NodeTermManager(() -> fixed, consumer);
        vote = Vote.newBuilder()
                .setNodeId("test")
                .setTerm(10)
                .setLastEntryInfo(paramEntry)
                .build();
        boolean res = manager.checkAndDetermineVote(vote);
        assertTrue(res);
        assertEquals(Status.FOLLOWER, consumer.status);
        assertEquals(vote.getTerm(), manager.getTerm());
        assertEquals("test", manager.getVotedFor().get());

        // accept again
        consumer.status = null;
        vote = Vote.newBuilder()
                .setNodeId("test")
                .setTerm(10)
                .setLastEntryInfo(paramEntry)
                .build();
        res = manager.checkAndDetermineVote(vote);
        assertTrue(res);
        assertNull(consumer.status);
        assertEquals(vote.getTerm(), manager.getTerm());

    }

    @Test
    public void checkAndDetermineVoteDenyButChangeTermWithOlderEntry() {
        EntryInfo paramEntry, fixed;
        NodeTermManager manager;
        Vote vote;
        StatusConsumer consumer = new StatusConsumer();
        fixed = EntryInfo.newBuilder().setCreateTerm(5).setLogIndex(4).build();

        manager = new NodeTermManager(() -> fixed, consumer);

        // deny logIndex < local log index
        paramEntry = EntryInfo.newBuilder().setCreateTerm(5).setLogIndex(3).build();
        consumer.status = null;
        vote = Vote.newBuilder()
                .setNodeId("test2")
                .setTerm(10)
                .setLastEntryInfo(paramEntry)
                .build();
        boolean res = manager.checkAndDetermineVote(vote);
        assertFalse(res);
        assertEquals(Status.FOLLOWER, consumer.status);
        assertEquals(10, manager.getTerm());
    }

    @Test
    public void checkAndDetermineVoteDenyWithSmallTerm() {
        EntryInfo paramEntry, fixed;
        NodeTermManager manager;
        Vote vote;
        StatusConsumer consumer = new StatusConsumer();
        fixed = EntryInfo.newBuilder().setCreateTerm(5).setLogIndex(4).build();
        paramEntry = EntryInfo.newBuilder().setCreateTerm(5).setLogIndex(5).build();

        // accept
        manager = new NodeTermManager(() -> fixed, consumer);
        vote = Vote.newBuilder()
                .setNodeId("test")
                .setTerm(10)
                .setLastEntryInfo(paramEntry)
                .build();
        boolean res = manager.checkAndDetermineVote(vote);
        assertTrue(res);
        assertEquals(Status.FOLLOWER, consumer.status);
        assertEquals(vote.getTerm(), manager.getTerm());
        assertEquals("test", manager.getVotedFor().get());


        // deny term < current term
        paramEntry = EntryInfo.newBuilder().setCreateTerm(5).setLogIndex(9).build();
        consumer.status = null;
        vote = Vote.newBuilder()
                .setNodeId("test2")
                .setTerm(9)
                .setLastEntryInfo(paramEntry)
                .build();
        res = manager.checkAndDetermineVote(vote);
        assertFalse(res);
        assertNull(consumer.status);
        assertEquals(10, manager.getTerm());
    }

    @Test
    public void checkAndDetermineVoteDenyCauseVoted() {
        EntryInfo paramEntry, fixed;
        NodeTermManager manager;
        Vote vote;
        StatusConsumer consumer = new StatusConsumer();
        fixed = EntryInfo.newBuilder().setCreateTerm(5).setLogIndex(4).build();
        paramEntry = EntryInfo.newBuilder().setCreateTerm(5).setLogIndex(5).build();

        // accept
        manager = new NodeTermManager(() -> fixed, consumer);
        vote = Vote.newBuilder()
                .setNodeId("test")
                .setTerm(10)
                .setLastEntryInfo(paramEntry)
                .build();
        boolean res = manager.checkAndDetermineVote(vote);
        assertTrue(res);
        assertEquals(Status.FOLLOWER, consumer.status);
        assertEquals(vote.getTerm(), manager.getTerm());
        assertEquals("test", manager.getVotedFor().get());

        // accept again
        consumer.status = null;
        vote = Vote.newBuilder()
                .setNodeId("test2")
                .setTerm(11)
                .setLastEntryInfo(paramEntry)
                .build();
        res = manager.checkAndDetermineVote(vote);
        assertTrue(res);
        assertEquals(Status.FOLLOWER, consumer.status);
        assertEquals(vote.getTerm(), manager.getTerm());

    }


    @Test
    public void compareWithLocal() {
    }

    class StatusConsumer implements Consumer<Status> {
        private Status status;

        @Override
        public void accept(Status status) {
            this.status = status;
        }
    }
}