package com.austin.netty.http.helloworld;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.handler.ssl.SslContext;

/**
 * @author yangxiaochen
 * @since 2019/11/20 16:18   Wed
 */
public class HttpHelloWorldServerInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslContext;

    public HttpHelloWorldServerInitializer(SslContext sslContext) {
        this.sslContext = sslContext;
    }

    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        if (sslContext != null) {
            pipeline.addLast(sslContext.newHandler(socketChannel.alloc()));
        }

        pipeline.addLast(new HttpServerCodec())
                .addLast(new HttpServerExpectContinueHandler())
                .addLast(new HttpHelloWorldServerHandler());
    }
}
