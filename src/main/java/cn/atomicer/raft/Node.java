package cn.atomicer.raft;

import cn.atomicer.raft.proto.AppendEntriesRequest;
import cn.atomicer.raft.proto.Response;
import cn.atomicer.raft.proto.Vote;

/**
 * @author Rao Mengnan
 * on 2018/11/18.
 */
public interface Node {
    String getId();
    Response appendEntries(AppendEntriesRequest request);
    Response requestVote(Vote vote);
}
