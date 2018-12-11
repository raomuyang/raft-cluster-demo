package cn.atomicer.raft.rpc;

import cn.atomicer.raft.Node;
import cn.atomicer.raft.model.NodeInfo;
import cn.atomicer.raft.proto.AppendEntriesRequest;
import cn.atomicer.raft.proto.Response;
import cn.atomicer.raft.proto.Vote;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Rao Mengnan
 * on 2018/11/18.
 */
public class NodeProxy implements Node {
    private Log logger = LogFactory.getLog(getClass());

    private ManagedChannel channel;
    private NodeInfo info;
    private NodeProxyGrpc.NodeProxyBlockingStub blockingStub;

    public NodeProxy(NodeInfo nodeInfo) {
        logger.info(String.format("Create node proxy, node id: %s (%s:%s)",
                nodeInfo.getName(), nodeInfo.getHost(), nodeInfo.getPort()));
        this.info = nodeInfo;
        channel = ManagedChannelBuilder
                .forAddress(info.getHost(), info.getPort())
                .usePlaintext()
                .build();
        this.blockingStub = NodeProxyGrpc.newBlockingStub(channel);
        Runtime.getRuntime().removeShutdownHook(new Thread(this::shutdown));
    }

    public NodeProxy(ManagedChannel channel, NodeInfo nodeInfo) {
        logger.info(String.format("Create node proxy by channel, node id: %s (%s:%s)",
                nodeInfo.getName(), nodeInfo.getHost(), nodeInfo.getPort()));
        this.info = nodeInfo;
        this.channel = channel;
        this.blockingStub = NodeProxyGrpc.newBlockingStub(channel);
        logger.debug(channel);
    }

    public void shutdown() {
        if (this.channel != null && !this.channel.isShutdown()) {
            this.channel.shutdown();
        }
    }

    @Override
    public String getId() {
        return info.getName();
    }

    @Override
    public Response appendEntries(AppendEntriesRequest request) {
        logger.debug(String.format("Request node %s to append entries (proxy) - %s:%s",
                info.getName(), info.getHost(), info.getPort()));
        return blockingStub.appendEntries(request);
    }

    @Override
    public Response requestVote(Vote vote) {
        logger.debug(String.format("Request node %s to vote (proxy) - %s:%s",
                info.getName(), info.getHost(), info.getPort()));
        return blockingStub.requestVote(vote);
    }

}
