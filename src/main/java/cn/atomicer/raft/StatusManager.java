package cn.atomicer.raft;

import cn.atomicer.raft.annotation.Status;
import java.util.function.Consumer;

/**
 * @author Rao Mengnan
 * on 2018/11/21.
 */
public interface StatusManager extends Consumer<Status> {
    Status getStatus();
}