package NetworkedPhysics.Network;

import NetworkedPhysics.Network.Messages.UdpClient;
import NetworkedPhysics.Server.NetworkedPhysicsServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;
import java.util.Map;


public class IncomingPacketHandlerServer extends IncommingPacketHandler {


    private final Map<InetSocketAddress, UdpClient> clients;



    public IncomingPacketHandlerServer(NetworkedPhysicsServer networkedPhysicsServer) {
        super(networkedPhysicsServer);
        clients = networkedPhysicsServer.getClients();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket packet) {

        InetSocketAddress inetSocketAddress = packet.sender();
        UdpClient udpClient = clients.get(inetSocketAddress);
        if (udpClient == null) {
            udpClient = new UdpClient(inetSocketAddress);
            clients.put(udpClient.inetSocketAddress, udpClient);
            ((NetworkedPhysicsServer) networkedPhysics).newUDPClient(udpClient);
        }

        udpClient.updateTimeout(System.currentTimeMillis());


        processIncomingMessage(packet);
        System.out.println("Inside incomming packet handler");

        //rcvPktProcessing(rcvPktBuf, rcvPktLength, srcAddr);
    }



}
