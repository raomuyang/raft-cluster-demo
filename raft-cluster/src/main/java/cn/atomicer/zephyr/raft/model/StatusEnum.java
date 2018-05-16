package cn.atomicer.zephyr.raft.model;

/**
 * @author Rao Mengnan
 *         on 2018/5/13.
 */
public enum  StatusEnum {
    LOOKING,
    FOLLOWER,
    OBSERVING,
    CANDIDATE,
    LEADER
}
