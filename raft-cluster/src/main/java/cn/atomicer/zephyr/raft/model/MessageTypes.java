package cn.atomicer.zephyr.raft.model;

/**
 * @author Rao Mengnan
 *         on 2018/5/13.
 */
public interface MessageTypes {
    String VOTE = "vote";
    String PING = "ping";
    String PONG = "pong";
    String ERRO = "err";
    String ACK = "ack";
    String REJECT = "rej";
}
