package NetworkedPhysics.Network;

import NetworkedPhysics.Common.NetworkedPhysics;
import NetworkedPhysics.Common.Protocol.PhysicsMessage;
import NetworkedPhysics.Common.Protocol.Protocol;
import NetworkedPhysics.Network.Messages.UdpClient;
import NetworkedPhysics.Server.NetworkedPhysicsServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.BlockingDeque;


public class IncomingPacketHandlerServer extends IncommingPacketHandler {


    private final NetworkedPhysicsServer networkedPhysicsServer;
    private final Map<InetSocketAddress, UdpClient> clients;
    private static final Map<Byte, Class<? extends PhysicsMessage>> commandMap = Protocol.protocol;


    public IncomingPacketHandlerServer(NetworkedPhysicsServer networkedPhysicsServer) {
        this.networkedPhysicsServer = networkedPhysicsServer;
        clients = networkedPhysicsServer.getClients();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket packet) {

        InetSocketAddress inetSocketAddress = packet.sender();
        UdpClient udpClient = clients.get(inetSocketAddress);
        if (udpClient == null) {
            udpClient = new UdpClient(inetSocketAddress);
            clients.put(udpClient.inetSocketAddress, udpClient);
            networkedPhysicsServer.newUDPClient(udpClient);
        }

        udpClient.updateTimeout(System.currentTimeMillis());


        final byte[] rcvPktBuf = readByteBuffer(packet);
        byte[] packetObject = new byte[rcvPktBuf.length - 1];
        byte commandCode = rcvPktBuf[0];

        try {
            System.arraycopy(rcvPktBuf, 0, packetObject, 0, packetObject.length);

            PhysicsMessage instance = (((commandMap.get(commandCode))
                    .getConstructor(InetSocketAddress.class, NetworkedPhysics.class)
                    .newInstance(packet.sender(), networkedPhysicsServer)))
                    .fromBlob(packetObject);
            instance.processMessage();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        System.out.println("Inside incomming packet handler");

        //rcvPktProcessing(rcvPktBuf, rcvPktLength, srcAddr);
    }


}
