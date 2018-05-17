package cn.atomicer.zephyr.raft.socket;

import cn.atomicer.zephyr.raft.serialize.Decoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author Rao Mengnan
 *         on 2018/5/17.
 */
public class MessageDecodeHandler<T> extends ByteToMessageDecoder {
    private Decoder<T> decoder;

    public MessageDecodeHandler(Decoder<T> decoder) {

        this.decoder = decoder;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> out) throws Exception {
        if (byteBuf.readableBytes() < 8) {
            return;
        }
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        decoder.write(bytes, bytes.length);

        while (true) {
            T message = decoder.poolMessage();
            if (message == null) break;
            out.add(message);
        }
    }
}