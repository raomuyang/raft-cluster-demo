package cn.atomicer.zephyr.raft.serialize;

import cn.atomicer.zephyr.io.coding.Encoder;
import cn.atomicer.zephyr.raft.exception.MessageDecodeException;
import cn.atomicer.zephyr.raft.model.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author Rao Mengnan
 *         on 2018/5/13.
 */
public class MessageEncoder implements Encoder<Message> {

    /**
     *
     * encode message to byte array
     * @param message {@link Message}
     * @return 由数据长度和数据组成的byte数组
     */
    @Override
    public byte[] encode(Message message) {
        try {
            ByteBuffer buffer = message.toByteBuffer();

            int size = buffer.limit();
            byte[] dataLength = SerializeTool.int2bytes(size);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            outputStream.write(dataLength, 0, dataLength.length);
            byte[] array = buffer.array();
            outputStream.write(array, 0, array.length);
            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new MessageDecodeException(String.format("serialize error: %s", message), e);
        }
    }

}
