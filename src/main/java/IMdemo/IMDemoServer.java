package IMdemo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class IMDemoServer {
    public static void main(String[] args){

        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

        try{
            ServerBootstrap b= new ServerBootstrap();
            b.group(boss,worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new IMInitializer());

            ChannelFuture future = b.bind(8888).sync();

            future.channel().closeFuture().sync(); //这个是server的


        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {

        }
    }
}
