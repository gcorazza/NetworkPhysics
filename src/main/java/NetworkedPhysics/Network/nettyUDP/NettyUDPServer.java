package NetworkedPhysics.Network.nettyUDP;

import NetworkedPhysics.Network.Message;
import NetworkedPhysics.Network.UDPServer;
import NetworkedPhysics.Network.UDPServerListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

public class NettyUDPServer implements UDPServer {

    private UDPServerListener listener;
    private UdpSocket serverSocket;

    private ConnectionMapper connectionMapper;

    public NettyUDPServer(UDPServerListener listener) {
        this.listener = listener;
    }

    @Override
    public void setListener(UDPServerListener listener) {
        this.listener = listener;
    }

    @Override
    public void startOn(int port) {
        serverSocket = new UdpSocket(port, new SimpleChannelInboundHandler<DatagramPacket>() {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) {
                UdpConnection connection = connectionMapper.get(msg.sender());

                if (connection == null) {
                    connection = connectionMapper.newConnection(msg.sender());
                    if (listener != null)
                        listener.newClient(connection.id);
                }

                connection.receiveMessage(msg, listener);
            }
        });
        connectionMapper = new ConnectionMapper(serverSocket);
    }



    @Override
    public void stop() {
        serverSocket.shutdown().awaitUninterruptibly();
    }

    @Override
    public void send(int id, Message message) {
        UdpConnection udpConnection = connectionMapper.get(id);
        if (udpConnection != null) {
            udpConnection.send(message);
        }
    }

    @Override
    public void sendToAll(Message message) {
        connectionMapper.connections().forEach(udpConnection -> serverSocket.send(message, udpConnection.nextStamp(), udpConnection.inetSocketAddress));
    }

    public ConnectionStatistics getStatistics(int id) {
        UdpConnection udpConnection = connectionMapper.get(id);
        if (udpConnection != null) {
            return udpConnection.getStats();
        }
        return null;
    }
}
