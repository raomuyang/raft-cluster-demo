package cn.atomicer.zephyr.raft.model;

import java.io.Serializable;

/**
 * @author Rao Mengnan
 *         on 2018/5/14.
 */
public class Vote implements Serializable {
    public static final String ASK = "ask";
    public static final String REPLY = "reply";

    private String mid;
    private int term;
    private long logIndex;

    public Vote(String mid, int term, long logIndex) {
        this.mid = mid;
        this.term = term;
        this.logIndex = logIndex;
    }

    public Vote() {
    }

    public long getLogIndex() {
        return logIndex;
    }

    public void setLogIndex(long logIndex) {
        this.logIndex = logIndex;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    @Override
    public String toString() {
        return "Vote{" +
                "mid='" + mid + '\'' +
                ", term=" + term +
                ", logIndex=" + logIndex +
                '}';
    }
}
