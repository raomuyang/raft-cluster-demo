package cn.atomicer.zephyr.raft.exception;

/**
 * @author Rao Mengnan
 *         on 2018/5/13.
 */
public class MessageDecodeException extends ZephyrRaftException {
    public MessageDecodeException() {
    }

    public MessageDecodeException(String message) {
        super(message);
    }

    public MessageDecodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageDecodeException(Throwable cause) {
        super(cause);
    }

    public MessageDecodeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
