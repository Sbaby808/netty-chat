package com.corwin.netty;

import com.corwin.config.NettyConfig;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 通道初始化对象
 */
@Component
public class WebSocketChannelInit extends ChannelInitializer {

    @Autowired
    NettyConfig nettyConfig;

    @Autowired
    WebSocketHandler webSocketHandler;

    @Override
    protected void initChannel(Channel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        // 对Http协议的支持
        pipeline.addLast(new HttpServerCodec());
        // 对大数据流的支持
        pipeline.addLast(new ChunkedWriteHandler());
        // post请求分三个部分。request line / request header / request body
        // HttpObjectAggregator将多个信息转换成单一的request或者response对象
        pipeline.addLast(new HttpObjectAggregator(8000));
        // 将http协议升级为ws协议。也就是对websocket的支持
        pipeline.addLast(new WebSocketServerProtocolHandler(nettyConfig.getPath()));
        // 自定义处理handler
        pipeline.addLast(webSocketHandler);
    }
}
