package cn.atomicer.zephyr.raft.serialize;

/**
 * @author Rao Mengnan
 *         on 2018/5/13.
 */
public class SerializeTool {
    public static byte[] int2bytes(int num) {
        byte[] data = new byte[4];
        data[0] = (byte) ((num >> 24) & 0xff);
        data[1] = (byte) ((num >> 16) & 0xff);
        data[2] = (byte) ((num >> 8) & 0xff);
        data[3] = (byte) (num & 0xff);
        return data;
    }

    public static int bytes2Int(byte[] data) {
        return (data[0] & 0xff) << 24 |
                (data[1] & 0xff) << 16 |
                (data[2] & 0xff) << 8 |
                data[3] & 0xff;
    }

    public static byte[] long2Bytes(long num) {
        byte[] data = new byte[8];
        int factory = 8;
        for (int i = 0; i < 8; i++) {
            data[i] = (byte) ((num >> (8 * --factory)) & 0xff);
        }
        return data;
    }

    public static long bytes2long(byte[] bytes) {
        long data = 0;
        int factory = 8;
        for (int i = 0; i < 8; i++) {
            data |= ((bytes[i] & 0xff) << (8 * --factory));
        }
        return data;
    }

}
