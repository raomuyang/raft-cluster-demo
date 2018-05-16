package cn.atomicer.zephyr.zk;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import cn.atomicer.chopsticks.annotation.NotNull;
import cn.atomicer.chopsticks.stream.Pipeline;

import java.util.Date;
import java.util.concurrent.Semaphore;

/**
 * 创建Watcher，监视指定Node的变化：
 *  1. 创建 / 更新： 调用处理管道处理配置文件
 *  2. 删除节点： 抛出异常
 *
 * @author Rao Mengnan
 *         on 2018/5/11.
 */
public class ConfigSync {
    private Log log = LogFactory.getLog(getClass());

    private ZooKeeper zooKeeper;
    private String path;
    private Pipeline<DataContent> processPipeline;

    private ConfigWatcher watcher;
    private Semaphore semaphore;
    private boolean interrupted;


    public ConfigSync(@NotNull ZooKeeper zooKeeper, @NotNull String path) {
        this.zooKeeper = zooKeeper;
        this.path = path;
        this.watcher = new ConfigWatcher();
        this.semaphore = new Semaphore(1);
        this.processPipeline = new Pipeline<>();
    }

    public ConfigSync(@NotNull ZooKeeper zooKeeper, @NotNull String path, @NotNull Pipeline<DataContent> processPipeline) {
        this(zooKeeper, path);
        this.processPipeline = processPipeline;
    }

    /**
     *
     * @throws Throwable 当与zk通信异常或zk删除了节点时
     */
    public void start() throws Throwable {
        if (!semaphore.tryAcquire()) {
            throw new ZephyrZkException("synchronize work already started");
        }
        try {
            Stat stat = zooKeeper.exists( path, watcher);
            log.info(String.format("node %s exists: %s, stat: %s", path, stat != null, stat));
            if (stat != null) {
                stat = new Stat();
                byte[] data = zooKeeper.getData(path, null, stat);
                log.debug(String.format("get node(%s) data: ", path) + stat);
                DataContent content = createDataContent(data, stat);
                processPipeline.process(content);
            }
        } catch (Throwable throwable) {
            log.debug("error occurred in start", throwable);
            stop();
            throw new ZephyrZkException(throwable);
        }

        semaphore.acquire();
        if (watcher.throwable != null) {
            throw watcher.throwable;
        }
    }

    public void stop() {
        interrupted = true;
        semaphore.release();
        log.info("config sync worker stopped");
    }

    class ConfigWatcher implements Watcher {
        private Throwable throwable;

        @Override
        public void process(WatchedEvent event) {
            log.debug("watch node event: " + event);
            switch (event.getType()) {
                case NodeCreated:
                case NodeDataChanged:
                    if (interrupted) {
                        return;
                    }
                    try {
                        Stat stat = new Stat();
                        byte[] data = zooKeeper.getData(path, this, stat);
                        DataContent content = createDataContent(data, stat);
                        log.debug(String.format("get node(%s) data: ", path) + stat);
                        processPipeline.process(content);
                    } catch (Throwable throwable) {
                        log.debug(throwable);
                        this.throwable = new ZephyrZkException("error occurred in watcher", throwable);
                        stop();
                    }
                    break;
                case NodeDeleted:
                    watcher.throwable = new NodeDeletedException(String.format("config node `%s` deleted", path));
                    stop();
            }
        }
    }

    private DataContent createDataContent(byte[] data, Stat stat) {

        DataContent content = new DataContent();
        if (data != null) {
            content.setData(data);
            content.setStat(stat);
            content.setCreateTime(new Date(stat.getCtime()));
            content.setUpdateTime(new Date(stat.getMtime()));
            content.setCzxid(stat.getCzxid());
            content.setMzxid(stat.getMzxid());
        }
        return content;
    }

}
