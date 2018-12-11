package cn.atomicer.raft.rpc;

import cn.atomicer.raft.Node;
import cn.atomicer.raft.proto.AppendEntriesRequest;
import cn.atomicer.raft.proto.Response;
import cn.atomicer.raft.proto.Vote;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

/**
 * @author Rao Mengnan
 * on 2018/11/18.
 */
public class NodeProxyGrpcService extends NodeProxyGrpc.NodeProxyImplBase {
    private Node nodeManager;
    private Server server;
    private int port;

    public NodeProxyGrpcService(Node nodeManager, int port) {
        this.nodeManager = nodeManager;
        this.port = port;
    }

    public void start() throws IOException {
        this.server = ServerBuilder
                .forPort(port)
                .addService(this)
                .build();
        this.server.start();
        Runtime.getRuntime()
                .addShutdownHook(new Thread(NodeProxyGrpcService.this::stop));
    }

    public void stop() {
        if (this.server != null) {
            this.server.shutdown();
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            this.server.awaitTermination();
        }
    }

    @Override
    public void appendEntries(AppendEntriesRequest request, StreamObserver<Response> responseObserver) {
        try {
            Response response = nodeManager.appendEntries(request);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }

    }

    @Override
    public void requestVote(Vote vote, StreamObserver<Response> responseObserver) {
        try {
            Response response = nodeManager.requestVote(vote);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

}
