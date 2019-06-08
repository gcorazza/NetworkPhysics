package NetworkedPhysics.Network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetAddress;


public class IncommingPacketHandlerClient extends  IncommingPacketHandler {


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket packet) throws Exception {
        final InetAddress srcAddr = packet.sender().getAddress();
        final int port = packet.sender().getPort();

        System.out.println(srcAddr);
        System.out.println(port);
        System.out.println(channelHandlerContext.channel().localAddress());

        final byte[] rcvPktBuf = readByteBuffer(packet);

        System.out.println(new String(rcvPktBuf));
        System.out.println("Inside incomming packet handler");

        //rcvPktProcessing(rcvPktBuf, rcvPktLength, srcAddr);
    }
}
