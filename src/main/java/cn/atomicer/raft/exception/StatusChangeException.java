package cn.atomicer.raft.exception;

/**
 * @author Rao Mengnan
 * on 2018/11/21.
 */
public class StatusChangeException extends RuntimeException {
    public StatusChangeException(String message) {
        super(message);
    }
}
