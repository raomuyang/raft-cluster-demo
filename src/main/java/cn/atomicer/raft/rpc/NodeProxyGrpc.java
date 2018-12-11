package cn.atomicer.raft.rpc;

import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.0.0)",
    comments = "Source: node_rpc.proto")
public class NodeProxyGrpc {

  private NodeProxyGrpc() {}

  public static final String SERVICE_NAME = "rpc.NodeProxy";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<cn.atomicer.raft.proto.AppendEntriesRequest,
      cn.atomicer.raft.proto.Response> METHOD_APPEND_ENTRIES =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "rpc.NodeProxy", "AppendEntries"),
          io.grpc.protobuf.ProtoUtils.marshaller(cn.atomicer.raft.proto.AppendEntriesRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(cn.atomicer.raft.proto.Response.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<cn.atomicer.raft.proto.Vote,
      cn.atomicer.raft.proto.Response> METHOD_REQUEST_VOTE =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "rpc.NodeProxy", "RequestVote"),
          io.grpc.protobuf.ProtoUtils.marshaller(cn.atomicer.raft.proto.Vote.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(cn.atomicer.raft.proto.Response.getDefaultInstance()));

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static NodeProxyStub newStub(io.grpc.Channel channel) {
    return new NodeProxyStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static NodeProxyBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new NodeProxyBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary and streaming output calls on the service
   */
  public static NodeProxyFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new NodeProxyFutureStub(channel);
  }

  /**
   */
  public static abstract class NodeProxyImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     * leader to follower, request append log or heartbeat
     * </pre>
     */
    public void appendEntries(cn.atomicer.raft.proto.AppendEntriesRequest request,
        io.grpc.stub.StreamObserver<cn.atomicer.raft.proto.Response> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_APPEND_ENTRIES, responseObserver);
    }

    /**
     * <pre>
     * candidate to peers, request vote
     * </pre>
     */
    public void requestVote(cn.atomicer.raft.proto.Vote request,
        io.grpc.stub.StreamObserver<cn.atomicer.raft.proto.Response> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_REQUEST_VOTE, responseObserver);
    }

    @java.lang.Override public io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_APPEND_ENTRIES,
            asyncUnaryCall(
              new MethodHandlers<
                cn.atomicer.raft.proto.AppendEntriesRequest,
                cn.atomicer.raft.proto.Response>(
                  this, METHODID_APPEND_ENTRIES)))
          .addMethod(
            METHOD_REQUEST_VOTE,
            asyncUnaryCall(
              new MethodHandlers<
                cn.atomicer.raft.proto.Vote,
                cn.atomicer.raft.proto.Response>(
                  this, METHODID_REQUEST_VOTE)))
          .build();
    }
  }

  /**
   */
  public static final class NodeProxyStub extends io.grpc.stub.AbstractStub<NodeProxyStub> {
    private NodeProxyStub(io.grpc.Channel channel) {
      super(channel);
    }

    private NodeProxyStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected NodeProxyStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new NodeProxyStub(channel, callOptions);
    }

    /**
     * <pre>
     * leader to follower, request append log or heartbeat
     * </pre>
     */
    public void appendEntries(cn.atomicer.raft.proto.AppendEntriesRequest request,
        io.grpc.stub.StreamObserver<cn.atomicer.raft.proto.Response> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_APPEND_ENTRIES, getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * candidate to peers, request vote
     * </pre>
     */
    public void requestVote(cn.atomicer.raft.proto.Vote request,
        io.grpc.stub.StreamObserver<cn.atomicer.raft.proto.Response> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_REQUEST_VOTE, getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class NodeProxyBlockingStub extends io.grpc.stub.AbstractStub<NodeProxyBlockingStub> {
    private NodeProxyBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private NodeProxyBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected NodeProxyBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new NodeProxyBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * leader to follower, request append log or heartbeat
     * </pre>
     */
    public cn.atomicer.raft.proto.Response appendEntries(cn.atomicer.raft.proto.AppendEntriesRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_APPEND_ENTRIES, getCallOptions(), request);
    }

    /**
     * <pre>
     * candidate to peers, request vote
     * </pre>
     */
    public cn.atomicer.raft.proto.Response requestVote(cn.atomicer.raft.proto.Vote request) {
      return blockingUnaryCall(
          getChannel(), METHOD_REQUEST_VOTE, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class NodeProxyFutureStub extends io.grpc.stub.AbstractStub<NodeProxyFutureStub> {
    private NodeProxyFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private NodeProxyFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected NodeProxyFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new NodeProxyFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * leader to follower, request append log or heartbeat
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<cn.atomicer.raft.proto.Response> appendEntries(
        cn.atomicer.raft.proto.AppendEntriesRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_APPEND_ENTRIES, getCallOptions()), request);
    }

    /**
     * <pre>
     * candidate to peers, request vote
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<cn.atomicer.raft.proto.Response> requestVote(
        cn.atomicer.raft.proto.Vote request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_REQUEST_VOTE, getCallOptions()), request);
    }
  }

  private static final int METHODID_APPEND_ENTRIES = 0;
  private static final int METHODID_REQUEST_VOTE = 1;

  private static class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final NodeProxyImplBase serviceImpl;
    private final int methodId;

    public MethodHandlers(NodeProxyImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_APPEND_ENTRIES:
          serviceImpl.appendEntries((cn.atomicer.raft.proto.AppendEntriesRequest) request,
              (io.grpc.stub.StreamObserver<cn.atomicer.raft.proto.Response>) responseObserver);
          break;
        case METHODID_REQUEST_VOTE:
          serviceImpl.requestVote((cn.atomicer.raft.proto.Vote) request,
              (io.grpc.stub.StreamObserver<cn.atomicer.raft.proto.Response>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    return new io.grpc.ServiceDescriptor(SERVICE_NAME,
        METHOD_APPEND_ENTRIES,
        METHOD_REQUEST_VOTE);
  }

}
