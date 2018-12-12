package cn.atomicer.raft;

/**
 * @author Rao Mengnan
 * on 2018-12-12.
 */
public interface State {
    void onEntry();
    void apply();
    void onExit();
}
