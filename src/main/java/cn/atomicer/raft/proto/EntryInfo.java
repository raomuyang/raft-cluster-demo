// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: messages.proto

package cn.atomicer.raft.proto;

/**
 * Protobuf type {@code proto.EntryInfo}
 */
public  final class EntryInfo extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:proto.EntryInfo)
    EntryInfoOrBuilder {
  // Use EntryInfo.newBuilder() to construct.
  private EntryInfo(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private EntryInfo() {
    logIndex_ = 0L;
    createTerm_ = 0L;
    data_ = com.google.protobuf.ByteString.EMPTY;
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
  }
  private EntryInfo(
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

            logIndex_ = input.readInt64();
            break;
          }
          case 16: {

            createTerm_ = input.readInt64();
            break;
          }
          case 26: {

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
    return cn.atomicer.raft.proto.Messages.internal_static_proto_EntryInfo_descriptor;
  }

  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return cn.atomicer.raft.proto.Messages.internal_static_proto_EntryInfo_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            cn.atomicer.raft.proto.EntryInfo.class, cn.atomicer.raft.proto.EntryInfo.Builder.class);
  }

  public static final int LOGINDEX_FIELD_NUMBER = 1;
  private long logIndex_;
  /**
   * <code>optional int64 logIndex = 1;</code>
   */
  public long getLogIndex() {
    return logIndex_;
  }

  public static final int CREATETERM_FIELD_NUMBER = 2;
  private long createTerm_;
  /**
   * <code>optional int64 createTerm = 2;</code>
   */
  public long getCreateTerm() {
    return createTerm_;
  }

  public static final int DATA_FIELD_NUMBER = 3;
  private com.google.protobuf.ByteString data_;
  /**
   * <code>optional bytes data = 3;</code>
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
    if (logIndex_ != 0L) {
      output.writeInt64(1, logIndex_);
    }
    if (createTerm_ != 0L) {
      output.writeInt64(2, createTerm_);
    }
    if (!data_.isEmpty()) {
      output.writeBytes(3, data_);
    }
  }

  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (logIndex_ != 0L) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt64Size(1, logIndex_);
    }
    if (createTerm_ != 0L) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt64Size(2, createTerm_);
    }
    if (!data_.isEmpty()) {
      size += com.google.protobuf.CodedOutputStream
        .computeBytesSize(3, data_);
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
    if (!(obj instanceof cn.atomicer.raft.proto.EntryInfo)) {
      return super.equals(obj);
    }
    cn.atomicer.raft.proto.EntryInfo other = (cn.atomicer.raft.proto.EntryInfo) obj;

    boolean result = true;
    result = result && (getLogIndex()
        == other.getLogIndex());
    result = result && (getCreateTerm()
        == other.getCreateTerm());
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
    hash = (37 * hash) + LOGINDEX_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
        getLogIndex());
    hash = (37 * hash) + CREATETERM_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
        getCreateTerm());
    hash = (37 * hash) + DATA_FIELD_NUMBER;
    hash = (53 * hash) + getData().hashCode();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static cn.atomicer.raft.proto.EntryInfo parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static cn.atomicer.raft.proto.EntryInfo parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static cn.atomicer.raft.proto.EntryInfo parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static cn.atomicer.raft.proto.EntryInfo parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static cn.atomicer.raft.proto.EntryInfo parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static cn.atomicer.raft.proto.EntryInfo parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static cn.atomicer.raft.proto.EntryInfo parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static cn.atomicer.raft.proto.EntryInfo parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static cn.atomicer.raft.proto.EntryInfo parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static cn.atomicer.raft.proto.EntryInfo parseFrom(
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
  public static Builder newBuilder(cn.atomicer.raft.proto.EntryInfo prototype) {
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
   * Protobuf type {@code proto.EntryInfo}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:proto.EntryInfo)
      cn.atomicer.raft.proto.EntryInfoOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return cn.atomicer.raft.proto.Messages.internal_static_proto_EntryInfo_descriptor;
    }

    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return cn.atomicer.raft.proto.Messages.internal_static_proto_EntryInfo_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              cn.atomicer.raft.proto.EntryInfo.class, cn.atomicer.raft.proto.EntryInfo.Builder.class);
    }

    // Construct using cn.atomicer.raft.proto.EntryInfo.newBuilder()
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
      logIndex_ = 0L;

      createTerm_ = 0L;

      data_ = com.google.protobuf.ByteString.EMPTY;

      return this;
    }

    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return cn.atomicer.raft.proto.Messages.internal_static_proto_EntryInfo_descriptor;
    }

    public cn.atomicer.raft.proto.EntryInfo getDefaultInstanceForType() {
      return cn.atomicer.raft.proto.EntryInfo.getDefaultInstance();
    }

    public cn.atomicer.raft.proto.EntryInfo build() {
      cn.atomicer.raft.proto.EntryInfo result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    public cn.atomicer.raft.proto.EntryInfo buildPartial() {
      cn.atomicer.raft.proto.EntryInfo result = new cn.atomicer.raft.proto.EntryInfo(this);
      result.logIndex_ = logIndex_;
      result.createTerm_ = createTerm_;
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
      if (other instanceof cn.atomicer.raft.proto.EntryInfo) {
        return mergeFrom((cn.atomicer.raft.proto.EntryInfo)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(cn.atomicer.raft.proto.EntryInfo other) {
      if (other == cn.atomicer.raft.proto.EntryInfo.getDefaultInstance()) return this;
      if (other.getLogIndex() != 0L) {
        setLogIndex(other.getLogIndex());
      }
      if (other.getCreateTerm() != 0L) {
        setCreateTerm(other.getCreateTerm());
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
      cn.atomicer.raft.proto.EntryInfo parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (cn.atomicer.raft.proto.EntryInfo) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private long logIndex_ ;
    /**
     * <code>optional int64 logIndex = 1;</code>
     */
    public long getLogIndex() {
      return logIndex_;
    }
    /**
     * <code>optional int64 logIndex = 1;</code>
     */
    public Builder setLogIndex(long value) {
      
      logIndex_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional int64 logIndex = 1;</code>
     */
    public Builder clearLogIndex() {
      
      logIndex_ = 0L;
      onChanged();
      return this;
    }

    private long createTerm_ ;
    /**
     * <code>optional int64 createTerm = 2;</code>
     */
    public long getCreateTerm() {
      return createTerm_;
    }
    /**
     * <code>optional int64 createTerm = 2;</code>
     */
    public Builder setCreateTerm(long value) {
      
      createTerm_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional int64 createTerm = 2;</code>
     */
    public Builder clearCreateTerm() {
      
      createTerm_ = 0L;
      onChanged();
      return this;
    }

    private com.google.protobuf.ByteString data_ = com.google.protobuf.ByteString.EMPTY;
    /**
     * <code>optional bytes data = 3;</code>
     */
    public com.google.protobuf.ByteString getData() {
      return data_;
    }
    /**
     * <code>optional bytes data = 3;</code>
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
     * <code>optional bytes data = 3;</code>
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


    // @@protoc_insertion_point(builder_scope:proto.EntryInfo)
  }

  // @@protoc_insertion_point(class_scope:proto.EntryInfo)
  private static final cn.atomicer.raft.proto.EntryInfo DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new cn.atomicer.raft.proto.EntryInfo();
  }

  public static cn.atomicer.raft.proto.EntryInfo getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<EntryInfo>
      PARSER = new com.google.protobuf.AbstractParser<EntryInfo>() {
    public EntryInfo parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
        return new EntryInfo(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<EntryInfo> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<EntryInfo> getParserForType() {
    return PARSER;
  }

  public cn.atomicer.raft.proto.EntryInfo getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

