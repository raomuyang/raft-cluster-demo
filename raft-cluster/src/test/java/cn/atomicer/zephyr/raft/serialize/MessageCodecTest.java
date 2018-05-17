package cn.atomicer.zephyr.raft.serialize;

import cn.atomicer.zephyr.raft.model.Message;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * @author Rao Mengnan
 *         on 2018/5/13.
 */
public class MessageCodecTest {

    @Test
    public void testEncodeAndDecode() {
        MessageEncoder encoder = new MessageEncoder();
        Message message1 = Message.newBuilder().setData(null)
                .setMid("a")
                .setTerm(1)
                .setRequestId("")
                .setData(null)
                .setType("xxx1")
                .build();

        byte[] bytes1 = encoder.encode(message1);

        Message message2 = Message.newBuilder().setData(null)
                .setMid("b")
                .setTerm(2)
                .setRequestId("")
                .setData(null)
                .setType("xxx2")
                .build();
        byte[] bytes2 = encoder.encode(message2);

        MessageDecoder decoder = new MessageDecoder();
        assertNull(decoder.poolMessage());
        decoder.write(bytes1, bytes1.length);
        Message message = decoder.poolMessage();
        assertEquals(message1.getMid().toString(), message.getMid().toString());
        assertEquals(message1.getTerm(), message.getTerm());

        decoder.write(bytes2, 3);
        assertNull(decoder.poolMessage());

        bytes2 = Arrays.copyOfRange(bytes2, 3, bytes2.length);
        decoder.write(bytes2, bytes2.length);
        message = decoder.poolMessage();
        assertEquals(message2.getMid().toString(), message.getMid().toString());
        assertEquals(message2.getTerm(), message.getTerm());
    }

}