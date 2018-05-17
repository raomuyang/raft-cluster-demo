package cn.atomicer.zephyr.raft.demo;

import cn.atomicer.zephyr.raft.Configuration;
import cn.atomicer.zephyr.raft.ElectionServer;

import java.io.IOException;

/**
 * @author Rao Mengnan
 *         on 2018/5/17.
 */
public class ElectionServerDemo {
    public static void main(String[] args) throws IOException, InterruptedException {
        Configuration configuration = new Configuration();
        configuration.loadConfig(ElectionServer.class.getResource("/config-2.yml").getPath());
        ElectionServer server = new ElectionServer(configuration);
        server.startServer();
    }
}
