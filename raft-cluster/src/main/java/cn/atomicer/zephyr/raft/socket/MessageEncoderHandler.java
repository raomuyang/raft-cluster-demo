package cn.atomicer.zephyr.raft.socket;

import cn.atomicer.zephyr.raft.serialize.Encoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author Rao Mengnan
 *         on 2018/5/17.
 */
public class MessageEncoderHandler<T> extends MessageToByteEncoder  {

    private Encoder<T> encoder;

    public MessageEncoderHandler(Encoder<T> encoder) {
        this.encoder = encoder;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object message, ByteBuf out) throws Exception {
        byte[] bytes = encoder.encode((T) message);
        out.writeBytes(bytes);
    }
}
