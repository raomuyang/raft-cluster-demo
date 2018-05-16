package cn.atomicer.zephyr.zk;

/**
 * @author Rao Mengnan
 *         on 2018/5/11.
 */
public class ZephyrZkException extends RuntimeException {
    public ZephyrZkException() {
    }

    public ZephyrZkException(String message) {
        super(message);
    }

    public ZephyrZkException(String message, Throwable cause) {
        super(message, cause);
    }

    public ZephyrZkException(Throwable cause) {
        super(cause);
    }

    public ZephyrZkException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
