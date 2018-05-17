package cn.atomicer.zephyr.raft;

/**
 * @author Rao Mengnan
 *         on 2018/5/13.
 */
public interface Constants {
    int VOTE_EXPIRE_TIMEOUT = 100; // ms
    int WAIT_VOTE_TIMEOUT = 100; // ms, 选举冲突的规避时间
    int MAX_CONNECT_WORKERS = 20;
    int READ_TIMEOUT = 30; // seconds
    int DEFAULT_ELECTION_TIMEOUT = 3000; // ms
    int DEFAULT_HEARTBEAT_TIMEOUT = 1000; // ms
    int DEFAULT_HEARTBEAT_CYCLE = 800; // ms
    int DEFAULT_SOCKET_TIMEOUT = 100;
}
