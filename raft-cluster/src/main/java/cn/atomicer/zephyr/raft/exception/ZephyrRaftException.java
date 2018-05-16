package cn.atomicer.zephyr.raft.exception;

/**
 * @author Rao Mengnan
 *         on 2018/5/12.
 */
public class ZephyrRaftException extends RuntimeException {
    public ZephyrRaftException() {
    }

    public ZephyrRaftException(String message) {
        super(message);
    }

    public ZephyrRaftException(String message, Throwable cause) {
        super(message, cause);
    }

    public ZephyrRaftException(Throwable cause) {
        super(cause);
    }

    public ZephyrRaftException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
