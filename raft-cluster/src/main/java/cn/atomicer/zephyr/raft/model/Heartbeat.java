package cn.atomicer.zephyr.raft.model;

import java.io.Serializable;

/**
 * @author Rao Mengnan
 *         on 2018/5/17.
 */
public class Heartbeat implements Serializable {
    private long prevLogIndex;
    private long commitIndex;
    private int term;

    public Heartbeat(long prevLogIndex, long commitIndex, int term) {
        this.prevLogIndex = prevLogIndex;
        this.commitIndex = commitIndex;
        this.term = term;
    }

    public long getPrevLogIndex() {
        return prevLogIndex;
    }

    public void setPrevLogIndex(long prevLogIndex) {
        this.prevLogIndex = prevLogIndex;
    }

    public long getCommitIndex() {
        return commitIndex;
    }

    public void setCommitIndex(long commitIndex) {
        this.commitIndex = commitIndex;
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }


}
