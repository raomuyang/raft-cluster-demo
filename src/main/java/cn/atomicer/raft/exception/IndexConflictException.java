package cn.atomicer.raft.exception;

/**
 * @author Rao Mengnan
 * on 2018/11/28.
 */
public class IndexConflictException extends RuntimeException {
    public IndexConflictException(String message) {
        super(message);
    }
}
