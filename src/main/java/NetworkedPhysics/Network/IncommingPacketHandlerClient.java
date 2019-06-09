package NetworkedPhysics.Network;

import NetworkedPhysics.Client.NetworkedPhysicsClient;
import NetworkedPhysics.Common.NetworkedPhysics;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetAddress;


public class IncommingPacketHandlerClient extends  IncommingPacketHandler {


    public IncommingPacketHandlerClient(NetworkedPhysicsClient networkedPhysicsClient) {
        super(networkedPhysicsClient);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket packet) throws Exception {
        final InetAddress srcAddr = packet.sender().getAddress();
        final int port = packet.sender().getPort();

        this.processIncomingMessage(packet);
    }
}
