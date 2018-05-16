package cn.atomicer.zephyr.zk;

/**
 * @author Rao Mengnan
 *         on 2018/5/11.
 */
public class NodeDeletedException extends ZephyrZkException {
    public NodeDeletedException(String message) {
        super(message);
    }
}
