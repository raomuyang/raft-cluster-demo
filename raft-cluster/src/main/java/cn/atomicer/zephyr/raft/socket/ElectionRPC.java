package cn.atomicer.zephyr.raft.socket;

import cn.atomicer.zephyr.raft.model.Message;
import cn.atomicer.zephyr.raft.model.Vote;

/**
 * @author Rao Mengnan
 *         on 2018/5/15.
 */
public interface ElectionRPC {
    void appendEntries(String machineName) throws Throwable;

    Message requestVote(String machineName, Vote vote) throws Throwable;
}
