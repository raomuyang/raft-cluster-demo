package cn.atomicer.zephyr.raft;

import cn.atomicer.zephyr.raft.model.*;
import cn.atomicer.zephyr.raft.serialize.MessageDecoder;
import cn.atomicer.zephyr.raft.serialize.MessageEncoder;
import cn.atomicer.zephyr.raft.socket.MessageDecodeHandler;
import cn.atomicer.zephyr.raft.socket.MessageEncoderHandler;
import com.google.gson.Gson;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author Rao Mengnan
 *         on 2018/5/12.
 */
public class ElectionServer {
    private Log log = LogFactory.getLog(getClass());
    private static final Gson gson = new Gson();

    private Machine localhost;
    private Configuration configuration;
    private ElectionManager electionManager;

    private ServerBootstrap bootstrap;
    private EventLoopGroup worker;
    private EventLoopGroup boss;

    public ElectionServer(Configuration configuration) {
        this.configuration = configuration;
        this.localhost = configuration.getConfig().getMachineInfo();
        this.electionManager = new ElectionManager(configuration);
        initServer();
    }

    private void initServer() {
        int workerThread = this.configuration.getMachines().size();
        this.worker = new NioEventLoopGroup(workerThread);
        this.boss = new NioEventLoopGroup();
        this.bootstrap = new ServerBootstrap()
                .group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ReadTimeoutHandler(Constants.READ_TIMEOUT, TimeUnit.SECONDS));
                        ch.pipeline().addLast(new MessageDecodeHandler<>(new MessageDecoder()));
                        ch.pipeline().addLast(new MessageEncoderHandler<>(new MessageEncoder()));
                        ch.pipeline().addLast(new MessageHandler());
                    }
                });
    }

    public void startServer() throws InterruptedException {
        int electionPort = localhost.getElectionPort();
        log.debug(String.format("EL-SERVER: start election server, machines in cluster: %s, election port: %s",
                configuration.getMachines().size(), electionPort));
        try {
            ChannelFuture channelFuture = bootstrap.bind(electionPort);
            electionManager.start();
            channelFuture.sync().channel().closeFuture().sync();
        } finally {
            worker.shutdownGracefully();
            boss.shutdownGracefully();
        }
    }

    class MessageHandler extends SimpleChannelInboundHandler<Message> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {
            String type = message.getType().toString().toLowerCase();
            String requestId = message.getRequestId().toString();
            log.debug("EL-SERVER: got election message, type: " + type);
            switch (type) {
                case MessageTypes.PING:
                    Message resp = electionManager.processAddEntitiesRequest(message);
                    ctx.writeAndFlush(resp);
                    log.debug(String.format("EL-SERVER: write message response (ping) success: %s", resp));
                    break;
                case MessageTypes.VOTE:
                    Vote voteInfo = gson.fromJson(new String(message.getData().array()), Vote.class);
                    Vote myVote = electionManager.processVoteRequest(message.getMid().toString(), voteInfo);
                    log.debug("EL-SERVER: channel response: " + myVote);
                    Message voteResp = electionManager.buildMessage(MessageTypes.VOTE, myVote, requestId);
                    ctx.writeAndFlush(voteResp);
                    log.debug(String.format("EL-SERVER: write message response (vote) success: %s", voteResp));
            }

        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            log.warn("EL-SERVER: Unknown message process exception", cause);
        }
    }

}
