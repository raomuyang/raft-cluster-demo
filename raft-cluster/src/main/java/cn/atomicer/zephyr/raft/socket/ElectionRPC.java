package cn.atomicer.zephyr.raft.socket;

import cn.atomicer.zephyr.raft.model.Heartbeat;
import cn.atomicer.zephyr.raft.model.Message;
import cn.atomicer.zephyr.raft.model.Vote;

/**
 * @author Rao Mengnan
 *         on 2018/5/15.
 */
public interface ElectionRPC {
    Message appendEntries(String machineName, Heartbeat heartbeat) throws Throwable;

    Message requestVote(String machineName, Vote vote) throws Throwable;
}
