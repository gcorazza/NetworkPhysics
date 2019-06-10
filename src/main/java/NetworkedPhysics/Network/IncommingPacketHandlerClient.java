package NetworkedPhysics.Network;

import NetworkedPhysics.Client.NetworkedPhysicsClient;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;


public class IncommingPacketHandlerClient extends IncomingPacketHandler {


    private final UdpConnection serverConnection;

    public IncommingPacketHandlerClient(NetworkedPhysicsClient networkedPhysicsClient) {
        super(networkedPhysicsClient);
        serverConnection = networkedPhysicsClient.getServerConnection();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket packet) throws Exception {
        this.processIncomingMessage(serverConnection, packet);
    }
}
