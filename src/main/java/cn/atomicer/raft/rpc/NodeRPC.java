// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: node_rpc.proto

package cn.atomicer.raft.rpc;

public final class NodeRPC {
  private NodeRPC() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\016node_rpc.proto\022\003rpc\032\016messages.proto2w\n" +
      "\tNodeProxy\022=\n\rAppendEntries\022\033.proto.Appe" +
      "ndEntriesRequest\032\017.proto.Response\022+\n\013Req" +
      "uestVote\022\013.proto.Vote\032\017.proto.ResponseB!" +
      "\n\024cn.atomicer.raft.rpcB\007NodeRPCP\001b\006proto" +
      "3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          cn.atomicer.raft.proto.Messages.getDescriptor(),
        }, assigner);
    cn.atomicer.raft.proto.Messages.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}