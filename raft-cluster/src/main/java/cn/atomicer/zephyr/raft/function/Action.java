package cn.atomicer.zephyr.raft.function;

/**
 * @author Rao Mengnan
 *         on 2018/5/16.
 */
public interface Action<R> {
    R run() throws Exception;
}
