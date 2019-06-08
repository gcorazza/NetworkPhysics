package NetworkedPhysics.Network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.awt.*;

public class PointHandler extends SimpleChannelInboundHandler<Point> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Point msg) throws Exception {
        System.out.println("ITZ A POOOINT");
    }
}
