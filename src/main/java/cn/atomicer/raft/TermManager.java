package cn.atomicer.raft;

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
public interface TermManager {

    void resetVote();

    boolean checkAndDetermineVote(Vote vote);

    long getTerm();

    void updateTerm(long term);

    void increaseTerm();

    String getLeaderId();

    void setLeaderId(String leaderId);
}
