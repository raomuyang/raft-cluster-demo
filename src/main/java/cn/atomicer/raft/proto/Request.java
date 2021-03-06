// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: messages.proto

package cn.atomicer.raft.proto;

/**
 * Protobuf type {@code proto.Request}
 */
public  final class Request extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:proto.Request)
    RequestOrBuilder {
  // Use Request.newBuilder() to construct.
  private Request(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private Request() {
    type_ = 0;
    nodeId_ = "";
    term_ = 0L;
    data_ = com.google.protobuf.ByteString.EMPTY;
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
  }
  private Request(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    int mutable_bitField0_ = 0;
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          default: {
            if (!input.skipField(tag)) {
              done = true;
            }
            break;
          }
          case 8: {
            int rawValue = input.readEnum();

            type_ = rawValue;
            break;
          }
          case 18: {
            java.lang.String s = input.readStringRequireUtf8();

            nodeId_ = s;
            break;
          }
          case 24: {

            term_ = input.readInt64();
            break;
          }
          case 34: {
            cn.atomicer.raft.proto.EntryInfo.Builder subBuilder = null;
            if (lastEntriesInfo_ != null) {
              subBuilder = lastEntriesInfo_.toBuilder();
            }
            lastEntriesInfo_ = input.readMessage(cn.atomicer.raft.proto.EntryInfo.parser(), extensionRegistry);
            if (subBuilder != null) {
              subBuilder.mergeFrom(lastEntriesInfo_);
              lastEntriesInfo_ = subBuilder.buildPartial();
            }

            break;
          }
          case 42: {

            data_ = input.readBytes();
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return cn.atomicer.raft.proto.Messages.internal_static_proto_Request_descriptor;
  }

  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return cn.atomicer.raft.proto.Messages.internal_static_proto_Request_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            cn.atomicer.raft.proto.Request.class, cn.atomicer.raft.proto.Request.Builder.class);
  }

  public static final int TYPE_FIELD_NUMBER = 1;
  private int type_;
  /**
   * <code>optional .proto.MessageType type = 1;</code>
   */
  public int getTypeValue() {
    return type_;
  }
  /**
   * <code>optional .proto.MessageType type = 1;</code>
   */
  public cn.atomicer.raft.proto.MessageType getType() {
    cn.atomicer.raft.proto.MessageType result = cn.atomicer.raft.proto.MessageType.valueOf(type_);
    return result == null ? cn.atomicer.raft.proto.MessageType.UNRECOGNIZED : result;
  }

  public static final int NODEID_FIELD_NUMBER = 2;
  private volatile java.lang.Object nodeId_;
  /**
   * <code>optional string nodeId = 2;</code>
   */
  public java.lang.String getNodeId() {
    java.lang.Object ref = nodeId_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      nodeId_ = s;
      return s;
    }
  }
  /**
   * <code>optional string nodeId = 2;</code>
   */
  public com.google.protobuf.ByteString
      getNodeIdBytes() {
    java.lang.Object ref = nodeId_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      nodeId_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int TERM_FIELD_NUMBER = 3;
  private long term_;
  /**
   * <code>optional int64 term = 3;</code>
   */
  public long getTerm() {
    return term_;
  }

  public static final int LASTENTRIESINFO_FIELD_NUMBER = 4;
  private cn.atomicer.raft.proto.EntryInfo lastEntriesInfo_;
  /**
   * <code>optional .proto.EntryInfo lastEntriesInfo = 4;</code>
   */
  public boolean hasLastEntriesInfo() {
    return lastEntriesInfo_ != null;
  }
  /**
   * <code>optional .proto.EntryInfo lastEntriesInfo = 4;</code>
   */
  public cn.atomicer.raft.proto.EntryInfo getLastEntriesInfo() {
    return lastEntriesInfo_ == null ? cn.atomicer.raft.proto.EntryInfo.getDefaultInstance() : lastEntriesInfo_;
  }
  /**
   * <code>optional .proto.EntryInfo lastEntriesInfo = 4;</code>
   */
  public cn.atomicer.raft.proto.EntryInfoOrBuilder getLastEntriesInfoOrBuilder() {
    return getLastEntriesInfo();
  }

  public static final int DATA_FIELD_NUMBER = 5;
  private com.google.protobuf.ByteString data_;
  /**
   * <code>optional bytes data = 5;</code>
   */
  public com.google.protobuf.ByteString getData() {
    return data_;
  }

  private byte memoizedIsInitialized = -1;
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (type_ != cn.atomicer.raft.proto.MessageType.DENY.getNumber()) {
      output.writeEnum(1, type_);
    }
    if (!getNodeIdBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 2, nodeId_);
    }
    if (term_ != 0L) {
      output.writeInt64(3, term_);
    }
    if (lastEntriesInfo_ != null) {
      output.writeMessage(4, getLastEntriesInfo());
    }
    if (!data_.isEmpty()) {
      output.writeBytes(5, data_);
    }
  }

  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (type_ != cn.atomicer.raft.proto.MessageType.DENY.getNumber()) {
      size += com.google.protobuf.CodedOutputStream
        .computeEnumSize(1, type_);
    }
    if (!getNodeIdBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, nodeId_);
    }
    if (term_ != 0L) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt64Size(3, term_);
    }
    if (lastEntriesInfo_ != null) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(4, getLastEntriesInfo());
    }
    if (!data_.isEmpty()) {
      size += com.google.protobuf.CodedOutputStream
        .computeBytesSize(5, data_);
    }
    memoizedSize = size;
    return size;
  }

  private static final long serialVersionUID = 0L;
  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof cn.atomicer.raft.proto.Request)) {
      return super.equals(obj);
    }
    cn.atomicer.raft.proto.Request other = (cn.atomicer.raft.proto.Request) obj;

    boolean result = true;
    result = result && type_ == other.type_;
    result = result && getNodeId()
        .equals(other.getNodeId());
    result = result && (getTerm()
        == other.getTerm());
    result = result && (hasLastEntriesInfo() == other.hasLastEntriesInfo());
    if (hasLastEntriesInfo()) {
      result = result && getLastEntriesInfo()
          .equals(other.getLastEntriesInfo());
    }
    result = result && getData()
        .equals(other.getData());
    return result;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptorForType().hashCode();
    hash = (37 * hash) + TYPE_FIELD_NUMBER;
    hash = (53 * hash) + type_;
    hash = (37 * hash) + NODEID_FIELD_NUMBER;
    hash = (53 * hash) + getNodeId().hashCode();
    hash = (37 * hash) + TERM_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
        getTerm());
    if (hasLastEntriesInfo()) {
      hash = (37 * hash) + LASTENTRIESINFO_FIELD_NUMBER;
      hash = (53 * hash) + getLastEntriesInfo().hashCode();
    }
    hash = (37 * hash) + DATA_FIELD_NUMBER;
    hash = (53 * hash) + getData().hashCode();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static cn.atomicer.raft.proto.Request parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static cn.atomicer.raft.proto.Request parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static cn.atomicer.raft.proto.Request parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static cn.atomicer.raft.proto.Request parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static cn.atomicer.raft.proto.Request parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static cn.atomicer.raft.proto.Request parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static cn.atomicer.raft.proto.Request parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static cn.atomicer.raft.proto.Request parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static cn.atomicer.raft.proto.Request parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static cn.atomicer.raft.proto.Request parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(cn.atomicer.raft.proto.Request prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code proto.Request}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:proto.Request)
      cn.atomicer.raft.proto.RequestOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return cn.atomicer.raft.proto.Messages.internal_static_proto_Request_descriptor;
    }

    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return cn.atomicer.raft.proto.Messages.internal_static_proto_Request_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              cn.atomicer.raft.proto.Request.class, cn.atomicer.raft.proto.Request.Builder.class);
    }

    // Construct using cn.atomicer.raft.proto.Request.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    public Builder clear() {
      super.clear();
      type_ = 0;

      nodeId_ = "";

      term_ = 0L;

      if (lastEntriesInfoBuilder_ == null) {
        lastEntriesInfo_ = null;
      } else {
        lastEntriesInfo_ = null;
        lastEntriesInfoBuilder_ = null;
      }
      data_ = com.google.protobuf.ByteString.EMPTY;

      return this;
    }

    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return cn.atomicer.raft.proto.Messages.internal_static_proto_Request_descriptor;
    }

    public cn.atomicer.raft.proto.Request getDefaultInstanceForType() {
      return cn.atomicer.raft.proto.Request.getDefaultInstance();
    }

    public cn.atomicer.raft.proto.Request build() {
      cn.atomicer.raft.proto.Request result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    public cn.atomicer.raft.proto.Request buildPartial() {
      cn.atomicer.raft.proto.Request result = new cn.atomicer.raft.proto.Request(this);
      result.type_ = type_;
      result.nodeId_ = nodeId_;
      result.term_ = term_;
      if (lastEntriesInfoBuilder_ == null) {
        result.lastEntriesInfo_ = lastEntriesInfo_;
      } else {
        result.lastEntriesInfo_ = lastEntriesInfoBuilder_.build();
      }
      result.data_ = data_;
      onBuilt();
      return result;
    }

    public Builder clone() {
      return (Builder) super.clone();
    }
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        Object value) {
      return (Builder) super.setField(field, value);
    }
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return (Builder) super.clearField(field);
    }
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return (Builder) super.clearOneof(oneof);
    }
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, Object value) {
      return (Builder) super.setRepeatedField(field, index, value);
    }
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        Object value) {
      return (Builder) super.addRepeatedField(field, value);
    }
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof cn.atomicer.raft.proto.Request) {
        return mergeFrom((cn.atomicer.raft.proto.Request)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(cn.atomicer.raft.proto.Request other) {
      if (other == cn.atomicer.raft.proto.Request.getDefaultInstance()) return this;
      if (other.type_ != 0) {
        setTypeValue(other.getTypeValue());
      }
      if (!other.getNodeId().isEmpty()) {
        nodeId_ = other.nodeId_;
        onChanged();
      }
      if (other.getTerm() != 0L) {
        setTerm(other.getTerm());
      }
      if (other.hasLastEntriesInfo()) {
        mergeLastEntriesInfo(other.getLastEntriesInfo());
      }
      if (other.getData() != com.google.protobuf.ByteString.EMPTY) {
        setData(other.getData());
      }
      onChanged();
      return this;
    }

    public final boolean isInitialized() {
      return true;
    }

    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      cn.atomicer.raft.proto.Request parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (cn.atomicer.raft.proto.Request) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private int type_ = 0;
    /**
     * <code>optional .proto.MessageType type = 1;</code>
     */
    public int getTypeValue() {
      return type_;
    }
    /**
     * <code>optional .proto.MessageType type = 1;</code>
     */
    public Builder setTypeValue(int value) {
      type_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional .proto.MessageType type = 1;</code>
     */
    public cn.atomicer.raft.proto.MessageType getType() {
      cn.atomicer.raft.proto.MessageType result = cn.atomicer.raft.proto.MessageType.valueOf(type_);
      return result == null ? cn.atomicer.raft.proto.MessageType.UNRECOGNIZED : result;
    }
    /**
     * <code>optional .proto.MessageType type = 1;</code>
     */
    public Builder setType(cn.atomicer.raft.proto.MessageType value) {
      if (value == null) {
        throw new NullPointerException();
      }
      
      type_ = value.getNumber();
      onChanged();
      return this;
    }
    /**
     * <code>optional .proto.MessageType type = 1;</code>
     */
    public Builder clearType() {
      
      type_ = 0;
      onChanged();
      return this;
    }

    private java.lang.Object nodeId_ = "";
    /**
     * <code>optional string nodeId = 2;</code>
     */
    public java.lang.String getNodeId() {
      java.lang.Object ref = nodeId_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        nodeId_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>optional string nodeId = 2;</code>
     */
    public com.google.protobuf.ByteString
        getNodeIdBytes() {
      java.lang.Object ref = nodeId_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        nodeId_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>optional string nodeId = 2;</code>
     */
    public Builder setNodeId(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      nodeId_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional string nodeId = 2;</code>
     */
    public Builder clearNodeId() {
      
      nodeId_ = getDefaultInstance().getNodeId();
      onChanged();
      return this;
    }
    /**
     * <code>optional string nodeId = 2;</code>
     */
    public Builder setNodeIdBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      nodeId_ = value;
      onChanged();
      return this;
    }

    private long term_ ;
    /**
     * <code>optional int64 term = 3;</code>
     */
    public long getTerm() {
      return term_;
    }
    /**
     * <code>optional int64 term = 3;</code>
     */
    public Builder setTerm(long value) {
      
      term_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional int64 term = 3;</code>
     */
    public Builder clearTerm() {
      
      term_ = 0L;
      onChanged();
      return this;
    }

    private cn.atomicer.raft.proto.EntryInfo lastEntriesInfo_ = null;
    private com.google.protobuf.SingleFieldBuilderV3<
        cn.atomicer.raft.proto.EntryInfo, cn.atomicer.raft.proto.EntryInfo.Builder, cn.atomicer.raft.proto.EntryInfoOrBuilder> lastEntriesInfoBuilder_;
    /**
     * <code>optional .proto.EntryInfo lastEntriesInfo = 4;</code>
     */
    public boolean hasLastEntriesInfo() {
      return lastEntriesInfoBuilder_ != null || lastEntriesInfo_ != null;
    }
    /**
     * <code>optional .proto.EntryInfo lastEntriesInfo = 4;</code>
     */
    public cn.atomicer.raft.proto.EntryInfo getLastEntriesInfo() {
      if (lastEntriesInfoBuilder_ == null) {
        return lastEntriesInfo_ == null ? cn.atomicer.raft.proto.EntryInfo.getDefaultInstance() : lastEntriesInfo_;
      } else {
        return lastEntriesInfoBuilder_.getMessage();
      }
    }
    /**
     * <code>optional .proto.EntryInfo lastEntriesInfo = 4;</code>
     */
    public Builder setLastEntriesInfo(cn.atomicer.raft.proto.EntryInfo value) {
      if (lastEntriesInfoBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        lastEntriesInfo_ = value;
        onChanged();
      } else {
        lastEntriesInfoBuilder_.setMessage(value);
      }

      return this;
    }
    /**
     * <code>optional .proto.EntryInfo lastEntriesInfo = 4;</code>
     */
    public Builder setLastEntriesInfo(
        cn.atomicer.raft.proto.EntryInfo.Builder builderForValue) {
      if (lastEntriesInfoBuilder_ == null) {
        lastEntriesInfo_ = builderForValue.build();
        onChanged();
      } else {
        lastEntriesInfoBuilder_.setMessage(builderForValue.build());
      }

      return this;
    }
    /**
     * <code>optional .proto.EntryInfo lastEntriesInfo = 4;</code>
     */
    public Builder mergeLastEntriesInfo(cn.atomicer.raft.proto.EntryInfo value) {
      if (lastEntriesInfoBuilder_ == null) {
        if (lastEntriesInfo_ != null) {
          lastEntriesInfo_ =
            cn.atomicer.raft.proto.EntryInfo.newBuilder(lastEntriesInfo_).mergeFrom(value).buildPartial();
        } else {
          lastEntriesInfo_ = value;
        }
        onChanged();
      } else {
        lastEntriesInfoBuilder_.mergeFrom(value);
      }

      return this;
    }
    /**
     * <code>optional .proto.EntryInfo lastEntriesInfo = 4;</code>
     */
    public Builder clearLastEntriesInfo() {
      if (lastEntriesInfoBuilder_ == null) {
        lastEntriesInfo_ = null;
        onChanged();
      } else {
        lastEntriesInfo_ = null;
        lastEntriesInfoBuilder_ = null;
      }

      return this;
    }
    /**
     * <code>optional .proto.EntryInfo lastEntriesInfo = 4;</code>
     */
    public cn.atomicer.raft.proto.EntryInfo.Builder getLastEntriesInfoBuilder() {
      
      onChanged();
      return getLastEntriesInfoFieldBuilder().getBuilder();
    }
    /**
     * <code>optional .proto.EntryInfo lastEntriesInfo = 4;</code>
     */
    public cn.atomicer.raft.proto.EntryInfoOrBuilder getLastEntriesInfoOrBuilder() {
      if (lastEntriesInfoBuilder_ != null) {
        return lastEntriesInfoBuilder_.getMessageOrBuilder();
      } else {
        return lastEntriesInfo_ == null ?
            cn.atomicer.raft.proto.EntryInfo.getDefaultInstance() : lastEntriesInfo_;
      }
    }
    /**
     * <code>optional .proto.EntryInfo lastEntriesInfo = 4;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        cn.atomicer.raft.proto.EntryInfo, cn.atomicer.raft.proto.EntryInfo.Builder, cn.atomicer.raft.proto.EntryInfoOrBuilder> 
        getLastEntriesInfoFieldBuilder() {
      if (lastEntriesInfoBuilder_ == null) {
        lastEntriesInfoBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            cn.atomicer.raft.proto.EntryInfo, cn.atomicer.raft.proto.EntryInfo.Builder, cn.atomicer.raft.proto.EntryInfoOrBuilder>(
                getLastEntriesInfo(),
                getParentForChildren(),
                isClean());
        lastEntriesInfo_ = null;
      }
      return lastEntriesInfoBuilder_;
    }

    private com.google.protobuf.ByteString data_ = com.google.protobuf.ByteString.EMPTY;
    /**
     * <code>optional bytes data = 5;</code>
     */
    public com.google.protobuf.ByteString getData() {
      return data_;
    }
    /**
     * <code>optional bytes data = 5;</code>
     */
    public Builder setData(com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      data_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional bytes data = 5;</code>
     */
    public Builder clearData() {
      
      data_ = getDefaultInstance().getData();
      onChanged();
      return this;
    }
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return this;
    }

    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return this;
    }


    // @@protoc_insertion_point(builder_scope:proto.Request)
  }

  // @@protoc_insertion_point(class_scope:proto.Request)
  private static final cn.atomicer.raft.proto.Request DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new cn.atomicer.raft.proto.Request();
  }

  public static cn.atomicer.raft.proto.Request getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<Request>
      PARSER = new com.google.protobuf.AbstractParser<Request>() {
    public Request parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
        return new Request(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<Request> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<Request> getParserForType() {
    return PARSER;
  }

  public cn.atomicer.raft.proto.Request getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

