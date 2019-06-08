package NetworkedPhysics.Network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

public abstract class IncommingPacketHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    static byte[] readByteBuffer(DatagramPacket packet) {
        final ByteBuf buf = packet.content();
        final int rcvPktLength = buf.readableBytes();
        final byte[] rcvPktBuf = new byte[rcvPktLength];
        buf.readBytes(rcvPktBuf);
        return rcvPktBuf;
    }
}
