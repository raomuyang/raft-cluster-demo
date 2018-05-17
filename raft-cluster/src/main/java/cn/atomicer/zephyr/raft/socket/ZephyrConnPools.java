package cn.atomicer.zephyr.raft.socket;

import cn.atomicer.zephyr.raft.function.BiConsumer;
import cn.atomicer.zephyr.raft.model.Message;
import cn.atomicer.zephyr.raft.serialize.MessageDecoder;
import cn.atomicer.zephyr.raft.serialize.MessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.ChannelPoolMap;
import io.netty.channel.pool.FixedChannelPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.InetSocketAddress;

/**
 * @author Rao Mengnan
 *         on 2018/5/14.
 */
class ZephyrConnPools {
    public static final int DEFAULT_MAX_CONNECTIONS = 20;

    static class ElectionConnectionPoolHandler implements ChannelPoolHandler {
        private Log log = LogFactory.getLog(getClass());
        private BiConsumer<ChannelHandlerContext, Message> onMessageRecv;

        ElectionConnectionPoolHandler(BiConsumer<ChannelHandlerContext, Message> onMessageRecv) {
            this.onMessageRecv = onMessageRecv;
        }

        @Override
        public void channelReleased(Channel ch) throws Exception {
            log.debug(String.format("%s released", ch));
        }

        @Override
        public void channelAcquired(Channel ch) throws Exception {
            log.debug(String.format("%s acquired", ch));
        }

        @Override
        public void channelCreated(Channel ch) throws Exception {
            log.debug(String.format("%s created", ch));
            ch.pipeline().addLast("VoteMessageDecoder", new MessageDecodeHandler<>(new MessageDecoder()));
            ch.pipeline().addLast("VoteMessageEncoder", new MessageEncoderHandler<>(new MessageEncoder()));
            ch.pipeline().addLast(new SimpleChannelInboundHandler<Message>() {
                @Override
                protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
                    if (onMessageRecv != null) onMessageRecv.accept(ctx, msg);
                }
            });
        }
    }


    static ChannelPoolMap<InetSocketAddress, FixedChannelPool> getPoolMap(Bootstrap bootstrap,
                                                                          BiConsumer<ChannelHandlerContext, Message> onMessageRecv,
                                                                          int maxConnection) {
        final int finalMaxConnection = maxConnection > 0 ? maxConnection : DEFAULT_MAX_CONNECTIONS;

        ChannelPoolHandler handler = new ElectionConnectionPoolHandler(onMessageRecv);

        return new AbstractChannelPoolMap<InetSocketAddress, FixedChannelPool>() {
            @Override
            protected FixedChannelPool newPool(InetSocketAddress key) {
                return new FixedChannelPool(bootstrap.remoteAddress(key),
                        handler, finalMaxConnection);
            }
        };
    }

    static ChannelPoolMap<InetSocketAddress, FixedChannelPool> getPoolMap(Bootstrap bootstrap,
                                                                                 BiConsumer<ChannelHandlerContext, Message> onMessageRecv) {
        return getPoolMap(bootstrap, onMessageRecv, DEFAULT_MAX_CONNECTIONS);

    }

}
