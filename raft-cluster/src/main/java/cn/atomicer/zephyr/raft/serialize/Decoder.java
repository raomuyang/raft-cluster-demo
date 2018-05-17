package cn.atomicer.zephyr.raft.serialize;

/**
 * The Decoder interface defines a binary message decoder behavior:
 * write byte and get messages
 *
 * @author Rao-Mengnan
 *         on 2018/2/12.
 */
public interface Decoder<T> {
    /**
     * By constantly writing byte arrays,
     * the decoder interprets these bytes as a message object
     *
     * @param bytes  Bytes fragments
     * @param offset Byte array offset of end
     */
    void write(byte[] bytes, int offset);

    /**
     * A stream of bytes may decode multiple message entities,
     * so the decoder needs to support the sequential removal
     * of the decoded result
     *
     * @return The order of the message entity
     */
    T poolMessage();
}
