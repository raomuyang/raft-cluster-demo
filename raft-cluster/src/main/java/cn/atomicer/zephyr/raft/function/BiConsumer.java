package cn.atomicer.zephyr.raft.function;

/**
 * @author Rao Mengnan
 *         on 2018/5/14.
 */
public interface BiConsumer<T1, T2> {
    void accept(T1 t1, T2 t2);
}
