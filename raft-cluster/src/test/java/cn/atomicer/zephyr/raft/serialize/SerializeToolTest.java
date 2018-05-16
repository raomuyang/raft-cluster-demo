package cn.atomicer.zephyr.raft.serialize;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

/**
 * @author Rao Mengnan
 *         on 2018/5/13.
 */
public class SerializeToolTest {

    @Test
    public void  testSerializeInt() {
        int num = new Random().nextInt(100000);
        byte[] array = SerializeTool.int2bytes(num);
        assertNotNull(array);
        assertEquals(num, SerializeTool.bytes2Int(array));
    }

}