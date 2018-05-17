package cn.atomicer.zephyr.raft.serialize;

/**
 * The message encoder encodes the message as a byte array
 *
 * @author Rao-Mengnan
 *         on 2018/2/12.
 */
public interface Encoder<T> {
    /**
     * Message to byte array
     *
     * @param t The message object instance
     * @return The result of coding
     */
    byte[] encode(T t);
}
