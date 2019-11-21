package IMdemo;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public class IMInitializer extends ChannelInitializer {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast(new ServerHandler());

    }
}
