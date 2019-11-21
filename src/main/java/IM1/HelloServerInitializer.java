package IM1;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

public class HelloServerInitializer extends ChannelInitializer<ServerSocketChannel> {

    @Override
    protected void initChannel(ServerSocketChannel ch) throws Exception {
//获取管道
        //一般来讲添加到last即可，添加了一个handler并且取名为HttpServerCodec
        //当请求到达服务端，要做解码，响应到客户端做编码
        ch.pipeline().addLast("HttpServerCodec", new HttpServerCodec());
        //添加自定义的CustomHandler这个handler，返回Hello Netty
        ch.pipeline().addLast("customHandler", new CustomHandler());
    }
}
