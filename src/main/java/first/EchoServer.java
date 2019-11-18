package first;

import first.Handler.EchoServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class EchoServer {

    private static int port = 0;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws InterruptedException {
        if(args.length!=1){
            System.out.println(
                    "Usage:"+EchoServer.class.getSimpleName()+"<port>"
            );

            int port = Integer.parseInt(args[0]);
            new EchoServer(port).start();
        }
    }

    public static void start() throws InterruptedException {
        final EchoServerHandler serverHandler = new EchoServerHandler();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup)
                    .channel(NioServerSocketChannel.class)//use Nio to transport 这个channel用来管理连接
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() { //每连接一个新的连接创建新的channel
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(serverHandler);
                        }
                    });
            ChannelFuture f = b.bind().sync(); //异步绑定服务器 调用sync阻塞直到绑定完成
            f.channel().closeFuture().sync();//获取Channel的closefuture 阻塞当前线程直到ta完成
        }finally {
            bossGroup.shutdownGracefully().sync();
        }
    }
}
