package chatIM.Handler;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.RandomAccess;

public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final String wsUri;
    private static File INDEX = null;

    static {
        URL location = HttpRequestHandler.class.getProtectionDomain().getCodeSource().getLocation();
        try {
            String path = location.toURI()+"index.html";
            path = !path.contains("file:")?path:path.substring(5);
            INDEX = new File(path);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public HttpRequestHandler(String wsUri){
        this.wsUri = wsUri;
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        if(wsUri.equalsIgnoreCase(msg.getUri())){
            ctx.fireChannelRead(msg.retain());//将其传递 如果是websocket则传递给下一个handler 如果不是ze

        }else {
            if(HttpHeaders.is100ContinueExpected(msg)){
                send100Continue(ctx);//处理100Continue
            }
            RandomAccessFile file = new RandomAccessFile(INDEX,"r");
            HttpResponse response = new DefaultFullHttpResponse(
                    msg.getProtocolVersion(), HttpResponseStatus.OK
            );
            response.headers().set(
                    HttpHeaders.Names.CONTENT_TYPE,
                    "text/plain;charset=UTF-8"
            );
            boolean keepAlive = HttpHeaders.isKeepAlive(msg);
            if(keepAlive){
                response.headers().set(
                        HttpHeaders.Names.CONTENT_LENGTH,file.length()
                );
                response.headers().set(HttpHeaders.Names.CONNECTION,HttpHeaders.Values.KEEP_ALIVE);


            }
            ctx.write(response);//http响应写到客户端
            if(ctx.pipeline().get(SslHandler.class) == null){
                ctx.write(new DefaultFileRegion(file.getChannel(),0,file.length()));//index.html写到客户端
            }else {
                ctx.write(new ChunkedNioFile(file.getChannel()));
            }

            ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            if(!keepAlive){
                future.addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    private static void send100Continue(ChannelHandlerContext ctx){
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.CONTINUE);
        ctx.writeAndFlush(response);
    }


    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
        cause.printStackTrace();
        ctx.close();
    }

}
