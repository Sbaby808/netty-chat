package com.corwin.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义处理类
 * TextWebSocketFrame:websocket数据是以帧的形式处理
 */
@Component
@ChannelHandler.Sharable    // 设置通道共享
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    public static List<Channel> channelList = new ArrayList<>();
    /**
     * 读就绪事件
     * @param channelHandlerContext
     * @param textWebSocketFrame
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        String text = textWebSocketFrame.text();
        System.out.println("msg:" + text);
        Channel channel = channelHandlerContext.channel();
        for (Channel channel1 : channelList) {
            if(channel1 != channel) {
                channel1.writeAndFlush(new TextWebSocketFrame(text));
            }
        }
    }

    /**
     * 通道就绪
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelList.add(channel);
        System.out.println("有新的连接.");
    }

    /**
     * 通道未就绪
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelList.remove(channel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        Channel channel = ctx.channel();
        channelList.remove(channel);
    }
}
