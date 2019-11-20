package com.austin.netty.http.helloworld;

import java.security.cert.CertificateException;

import javax.net.ssl.SSLException;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * An HTTP server that sends back the content of the received HTTP request
 *
 * @author yangxiaochen
 * @since 2019/11/20 16:15   Wed
 */
public class HttpHelloWorldServer {

    static final boolean SSL = System.getProperty("ssl") != null;
    static final int PORT = Integer.parseInt(System.getProperty("port", SSL ? "8443" : "8080"));

    public static void main(String[] args) throws CertificateException, SSLException, InterruptedException {
        // Configure SSL.
        final SslContext sslContext;
        if (SSL) {
            SelfSignedCertificate selfSignedCertificate = new SelfSignedCertificate();
            sslContext = SslContextBuilder.forServer(selfSignedCertificate.certificate(),
                    selfSignedCertificate.privateKey()).build();
        } else {
            sslContext = null;
        }

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        // Configure the server.
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {
            serverBootstrap
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new HttpHelloWorldServerInitializer(sslContext));

            Channel channel = serverBootstrap.bind(PORT).addListener(new GenericFutureListener<Future<? super Void>>() {
                public void operationComplete(Future<? super Void> future) throws Exception {
                    if (future.isSuccess()) {
                        System.out.println("Open your web browser and navigate to " +
                                (SSL ? "https" : "http") + "://127.0.0.1:" + PORT + '/');
                    } else {
                        System.err.println("启动失败!");
                    }
                }
            }).sync().channel();

            channel.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
