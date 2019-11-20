package com.austin.netty.http.helloworld;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;

/**
 * @author yangxiaochen
 * @since 2019/11/20 16:22   Wed
 */
public class HttpHelloWorldServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    private static String JSON = "{\n"
            + "    \"referQname\": \"/DW/UDW/WISE/udw_event#event_action=wiseps_query_click\",               \n"
            + "    \"viewOwner\": \"yangxiaochen\",                \n"
            + "    \"permCols\": [                          \n"
            + "        \"event_time\",\n"
            + "        \"event_ip\",\n"
            + "    \t\"event_city\",\n"
            + "    \t\"event_userid\"\n"
            + "    ],\n"
            + "    \"rowPermStr\": \"where event_city = 'beijing'\"  \n"
            + "}";

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpObject httpObject) throws Exception {
        if (httpObject instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) httpObject;

            boolean keepAlive = HttpUtil.isKeepAlive(request);
            FullHttpResponse response = new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(JSON.getBytes()));

            response.headers()
                    .set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                    .setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());

            if (keepAlive) {
                if (!request.protocolVersion().isKeepAliveDefault()) {
                    response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                }
            } else {
                response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
            }

            ChannelFuture future = channelHandlerContext.write(response);

            if (!keepAlive) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
