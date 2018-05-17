package cn.atomicer.zephyr.raft.socket;

import cn.atomicer.zephyr.raft.Configuration;
import cn.atomicer.zephyr.raft.Constants;
import cn.atomicer.zephyr.raft.function.BiConsumer;
import cn.atomicer.zephyr.raft.model.Machine;
import cn.atomicer.zephyr.raft.model.Message;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @author Rao Mengnan
 *         on 2018/5/14.
 */
public class MessageSender {

    private Log log = LogFactory.getLog(getClass());

    private SocketClient socketClient;

    private Map<String, Message> responseMap;
    private Map<String, Throwable> requestErrorMap;
    private Map<String, Semaphore> requestSyncMap;

    private Configuration configuration;
    private BiConsumer<ChannelHandlerContext, Message> onMessageRecv;
    private BiConsumer<Message, Throwable> onError;


    public MessageSender(Configuration configuration) {
        this.initAction();
        this.configuration = configuration;
        this.responseMap = new ConcurrentHashMap<>();
        this.requestErrorMap = new ConcurrentHashMap<>();
        this.requestSyncMap = new ConcurrentHashMap<>();

        List<Machine> peers = configuration.getConfig().getPeers();
        int workerCount = Constants.MAX_CONNECT_WORKERS > peers.size() ? peers.size() : Constants.MAX_CONNECT_WORKERS;
        this.socketClient = new SocketClient(workerCount);
    }

    /**
     * 批量发送消息，无返回值
     *
     * @param messageMap target machine: message
     */
    public void putMessage(Map<String, Message> messageMap) {
        for (Map.Entry<String, Message> entry : messageMap.entrySet()) {
            String mid = entry.getKey();
            connectAcquire(mid).addListener(new ClientChannelFutureListener(entry.getKey(), entry.getValue()));
        }
    }

    /**
     * 发送消息，并返回future
     *
     * @param targetName 目标的机器id
     * @param message    消息
     * @return 可绑定监听器的future
     */
    public Future<Channel> sendMessage(String targetName, Message message) {
        return connectAcquire(targetName)
                .addListener(new ClientChannelFutureListener(targetName, message));
    }

    public boolean tryAcquire(String requestId, long millSeconds) throws InterruptedException {
        Semaphore semaphore = this.requestSyncMap.get(requestId);
        if (semaphore == null) {
            log.debug("SENDER: No such request id found (acquire): " + requestId);
            return false;
        }
        try {
            return semaphore.tryAcquire(millSeconds, TimeUnit.MILLISECONDS);
        } finally {
            release(requestId);
        }
    }

    public void release(String requestId) {
        Semaphore semaphore = this.requestSyncMap.get(requestId);
        if (semaphore == null) {
            log.debug("SENDER: No such request id found (release): " + requestId);
            return;
        }
        semaphore.release();
    }

    public Message popResponse(String requestId) {
        return responseMap.remove(requestId);
    }

    public Throwable getCause(String requestId) {
        return requestErrorMap.get(requestId);
    }

    public Throwable popCause(String requestId) {
        return requestErrorMap.remove(requestId);
    }

    private void initAction() {
        this.onMessageRecv = (channelHandlerContext, message) -> {
            String requestId = message.getRequestId().toString();
            log.debug(String.format("SENDER: channel %s responded: %s, type: %s, requestId: %s",
                    channelHandlerContext, message.getMid(), message.getType(), requestId));
            responseMap.put(requestId, message);
            Semaphore semaphore = requestSyncMap.get(requestId);
            if (semaphore != null) semaphore.release();
        };
        this.onError = ((message, throwable) -> {
            log.debug("SENDER: message process error: " + throwable.getMessage());
            String requestId = message.getRequestId().toString();
            requestErrorMap.put(requestId, throwable);
            Semaphore semaphore = requestSyncMap.remove(requestId);
            if (semaphore != null) semaphore.release();
        });
    }

    private Future<Channel> connectAcquire(String mid) {
        Machine machine = configuration.getMachines().get(mid);
        InetSocketAddress address = new InetSocketAddress(machine.getHost(), machine.getElectionPort());
        return socketClient.poolMap.get(address).acquire();
    }

    private void connectRelease(Channel channel, String mid) {
        Machine machine = configuration.getMachines().get(mid);
        InetSocketAddress address = new InetSocketAddress(machine.getHost(), machine.getElectionPort());
        socketClient.poolMap.get(address).release(channel);
    }

    class ClientChannelFutureListener implements GenericFutureListener<Future<? super Channel>> {
        String mid;
        Message message;

        ClientChannelFutureListener(String mid, Message message) {
            this.mid = mid;
            this.message = message;
        }

        @Override
        public void operationComplete(Future<? super Channel> future) throws Exception {
            if (future.isDone() && !future.isSuccess()) {
                log.warn(String.format("SENDER: connection failed: %s, requestId: %s", mid, message.getRequestId()));
                if (future.cause() != null) log.debug(future.cause().getMessage());
                onError.accept(message, future.cause());
                return;
            }
            requestSyncMap.put(message.getRequestId().toString(), new Semaphore(0));
            Channel channel = (Channel) future.get();
            ChannelFuture writeFuture = channel.writeAndFlush(message);

            log.debug(String.format("SENDER: channel written : %s, requestId: %s", mid, message.getRequestId()));
            if (writeFuture != null) {
                Throwable throwable = null;
                if (writeFuture.isDone() && !writeFuture.isSuccess()) {
                    log.debug(String.format("SENDER: connection completed: %s", writeFuture));
                    throwable = writeFuture.cause();
                } else {
                    try {
                        writeFuture.sync();
                    } catch (Throwable e) {
                        log.debug(String.format("SENDER: channel write failed: %s, cause: %s",
                                message.getRequestId(), e));
                        throwable = e;
                    }
                }
                if (throwable != null) {
                    log.debug(String.format("SENDER: connection exception: %s", writeFuture));
                    onError.accept(message, throwable);
                }
            }
            connectRelease(channel, mid);
        }

    }

    class SocketClient {
        Bootstrap bootstrap;
        EventLoopGroup worker;
        ChannelPoolMap<InetSocketAddress, FixedChannelPool> poolMap;

        SocketClient(int threadsCount) {
            threadsCount = threadsCount > 0 ? threadsCount : 1;
            this.worker = new NioEventLoopGroup(threadsCount);
            this.bootstrap = new Bootstrap().group(worker).channel(NioSocketChannel.class);
            this.poolMap = ZephyrConnPools.getPoolMap(bootstrap, onMessageRecv,
                    configuration.getConfig().getMaxConnections());
        }

        void shutdown() {
            if (this.worker != null) {
                worker.shutdownGracefully();
            }
        }

    }

}
