package cn.atomicer.zephyr.zk;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author Rao Mengnan
 *         on 2018/5/11.
 */
public class ZkConnection {

    public static ZooKeeper connect(String hosts, int timeout) throws IOException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        ZooKeeper zooKeeper = new ZooKeeper(hosts, timeout, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    latch.countDown();
                }
            }
        });
        latch.await();
        return zooKeeper;
    }
}
