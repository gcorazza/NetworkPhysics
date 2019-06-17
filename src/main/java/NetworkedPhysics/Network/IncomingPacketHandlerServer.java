package NetworkedPhysics.Network;

import NetworkedPhysics.Server.NetworkedPhysicsServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;
import java.util.Map;


public class IncomingPacketHandlerServer extends IncomingPacketHandler {


    private final Map<InetSocketAddress, UdpConnection> clients;



    public IncomingPacketHandlerServer(NetworkedPhysicsServer networkedPhysicsServer) {
        super(networkedPhysicsServer);
        clients = networkedPhysicsServer.getClients();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket packet) {

        InetSocketAddress inetSocketAddress = packet.sender();
        UdpConnection udpConnection = clients.get(inetSocketAddress);

        if (udpConnection == null) {
            udpConnection = new UdpConnection(inetSocketAddress);
            clients.put(udpConnection.inetSocketAddress, udpConnection);
            ((NetworkedPhysicsServer) rewindablePhysicsWorld).newUDPClient(udpConnection);
        }

        udpConnection.updateTimeout(System.currentTimeMillis());

        processIncomingMessage(udpConnection, packet);
        System.out.println("Inside incomming packet handler");

        //rcvPktProcessing(rcvPktBuf, rcvPktLength, srcAddr);
    }

}
