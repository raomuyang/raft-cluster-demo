package cn.atomicer.zephyr.raft.serialize;

import cn.atomicer.zephyr.raft.exception.MessageDecodeException;
import cn.atomicer.zephyr.raft.model.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Rao Mengnan
 *         on 2018/5/13.
 */
public class MessageDecoder implements Decoder<Message> {

    private ConcurrentLinkedQueue<Message> messages;

    private int nextLength;
    private byte[] remainData;

    public MessageDecoder() {
        messages = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void write(byte[] bytes, int offset) {
        bytes = Arrays.copyOfRange(bytes, 0, offset);
        byte[] data = bytes;

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int remains = remainData == null ? 0 : remainData.length;

        if (nextLength == 0) {
            // 从上一次剩余的bytes中和参数传入的bytes中取出足够的字节转换为数据长度length
            byte[] array = new byte[4];
            int cur = remainData == null ? 0 : remainData.length;
            int headNeed = 4 >  cur ? 4 - cur : 0;

            if (headNeed > bytes.length) {
                if (remainData != null) {
                    outputStream.write(remainData, 0, remainData.length);
                }
                outputStream.write(bytes, 0, bytes.length);
                remainData = outputStream.toByteArray();
                return;
            }

            if (cur > 0) {
                System.arraycopy(remainData, 0, array, 0, 4 - headNeed);

                // 将上次剩余的字节数据写入outputStream中
                outputStream.write(remainData, 4 - headNeed, remainData.length - cur);
                remains -= cur;
            }

            int move = 4 - headNeed;
            System.arraycopy(bytes, 0, array, move, headNeed);
            nextLength = SerializeTool.bytes2Int(array);

            // 去掉本次参数bytes中已经使用掉的字节
            data = Arrays.copyOfRange(bytes, headNeed, bytes.length);
            remainData = null;
        }

        int need = nextLength - remains;
        if (remainData != null) outputStream.write(remainData, 0, remainData.length);

        if (data.length < need) {
            // 字节长度不够无法反序列化，保存状态并返回
            outputStream.write(data, 0, data.length);
            remainData = outputStream.toByteArray();
            return;
        } else {
            outputStream.write(data, 0, need);
            remainData = Arrays.copyOfRange(data, need, data.length);
            nextLength = 0;
        }

        try {
            byte[] finalData = outputStream.toByteArray();
            Message message = Message.fromByteBuffer(ByteBuffer.wrap(finalData));
            messages.add(message);
        } catch (IOException e) {
            throw new MessageDecodeException("illegal byte data", e);
        }
    }

    @Override
    public Message poolMessage() {
        return messages.poll();
    }

}
