package cn.atomicer.zephyr.raft.exception;

/**
 * @author Rao Mengnan
 *         on 2018/5/13.
 */
public class MessageEncodeError extends ZephyrRaftException {
    public MessageEncodeError() {
    }

    public MessageEncodeError(String message) {
        super(message);
    }

    public MessageEncodeError(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageEncodeError(Throwable cause) {
        super(cause);
    }

    public MessageEncodeError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
