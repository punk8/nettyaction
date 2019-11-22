package IMdemo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.time.LocalDate;


@ChannelHandler.Sharable

public class ServerHandler extends SimpleChannelInboundHandler {
    //用于记录和管理所有客户端的channel，可以把相应的channel保存到一整个组中
    //DefaultChannelGroup：用于对应ChannelGroup，进行初始化
    private static ChannelGroup channelClient =  new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception{
        ByteBuf in = (ByteBuf) msg;
        System.out.println("server received : "+in.toString(CharsetUtil.UTF_8));
//        ctx.write(msg);//将接收到的信息写给发送者 不冲刷

        for (Channel channel : channelClient) {
            //循环对每一个channel对应输出即可（往缓冲区中写，写完之后再刷到客户端）
            //注：writeAndFlush不可以使用String，因为传输的载体是一个TextWebSocketFrame，需要把消息通过载体再刷到客户端
            channel.writeAndFlush(((ByteBuf) msg).retain());
//            channel.writeAndFlush(Unpooled.EMPTY_BUFFER);


        }

//        ctx.write(msg);
    }



//    public void channelReadComplete(ChannelHandlerContext ctx){
//        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);//冲刷 并且关闭channel
//    }

    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
        cause.printStackTrace();
        ctx.close();//关闭channel
    }

    public void channelActive(ChannelHandlerContext ctx){
        System.out.println("客户端连接");
        channelClient.add(ctx.channel());
        System.out.println("current channel:"+channelClient.size());

    }

    public void channelInactive(ChannelHandlerContext ctx){
        System.out.println("客户端断开");
        channelClient.remove(ctx.channel());
        System.out.println("current channel:"+channelClient.size());

    }
}
