package cn.atomicer.raft.demo;

import cn.atomicer.raft.NodeInitializer;
import cn.atomicer.raft.manager.NodeEntriesManager;

import java.io.IOException;

/**
 * @author Rao Mengnan
 * on 2018/11/28.
 */
public class App1 {
    private static String path = App1.class.getResource("/config-1.yml").getPath();
    public static void main(String[] args) throws IOException, InterruptedException {
        NodeInitializer initializer = new NodeInitializer(path, new NodeEntriesManager());
        initializer.getService().start();
        initializer.getService().blockUntilShutdown();
    }
}
