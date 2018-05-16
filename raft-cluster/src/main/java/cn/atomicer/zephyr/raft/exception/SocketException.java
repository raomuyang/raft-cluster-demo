package cn.atomicer.zephyr.raft.exception;

/**
 * @author Rao Mengnan
 *         on 2018/5/14.
 */
public class SocketException extends ZephyrRaftException {
    public SocketException() {
    }

    public SocketException(String message) {
        super(message);
    }

    public SocketException(String message, Throwable cause) {
        super(message, cause);
    }

    public SocketException(Throwable cause) {
        super(cause);
    }

    public SocketException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
