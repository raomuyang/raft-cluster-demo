package cn.atomicer.zephyr.raft;

/**
 * @author Rao Mengnan
 *         on 2018/5/13.
 */
public interface Constants {
    int VOTE_EXPIRE_TIMEOUT = 100; // ms
    int ELECTION_TIMEOUT = 3000; // ms
    int HEARTBEAT_TIMEOUT = 20000; // ms
    int HEARTBEAT_RECYCLE = 1000; // ms
    int WAIT_VOTE_TIMEOUT = 20000; // ms
    int SOCKET_TIMEOUT = 100;
    int MAX_CONNECT_WORKERS = 20;
    int READ_TIMEOUT = 30; // seconds
}
