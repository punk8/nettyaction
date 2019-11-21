package IM1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

//实现客户端发送请求，服务器端会返回Hello Netty
public class HelloServer {

    public static void main(String[] args) throws InterruptedException {
        //定义两个线程组
        //主线程组 处理连接
        EventLoopGroup boss = new NioEventLoopGroup();
        //从线程组 处理I/O
        EventLoopGroup worker = new NioEventLoopGroup();

        try{
            //服务启动类
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss,worker)
                    .channel(NioServerSocketChannel.class) //设置NIO
                    .handler(new HelloServerInitializer())
                    .childHandler(new HelloServerforClientInitializer());

            //阻塞知道绑定
            ChannelFuture future = bootstrap.bind(8888).sync();


            //监听某个channel的关闭事件 阻塞
            future.channel().closeFuture().sync();

        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }

    }
}

