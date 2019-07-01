package NetworkedPhysics.Network.nettyUDP;

import NetworkedPhysics.Network.Message;
import NetworkedPhysics.Network.UDPClient;
import NetworkedPhysics.Network.UDPConnectionListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;

public class NettyUDPClient implements UDPClient {

    private UDPConnectionListener listener;
    private UdpSocket udpSocket;
    private UdpConnection connection;

    public NettyUDPClient(UDPConnectionListener listener) {
        this.listener = listener;
    }

    private static Message disconnect = new Message() {
        @Override
        public byte getCommandCode() {
            return Byte.MIN_VALUE;
        }

        @Override
        public byte[] getPacket() {
            return new byte[0];
        }
    };

    @Override
    public void connect(InetSocketAddress socketAddress) {
        udpSocket = new UdpSocket(0, new SimpleChannelInboundHandler<DatagramPacket>() {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) {
                connection.receiveMessage(msg, listener);
            }
        });
        udpSocket.connect(socketAddress);
        connection = new UdpConnection(socketAddress, 0, udpSocket);
        connection.send(new Message() {
            @Override
            public byte getCommandCode() {
                return 0;
            }

            @Override
            public byte[] getPacket() {
                return new byte[0];
            }
        });
    }

    @Override
    public void disconnect() {
        send(disconnect);
    }

    @Override
    public void send(Message message) {
        connection.send(message);
    }

    @Override
    public void setListener(UDPConnectionListener listener) {
        this.listener = listener;
    }


    public ConnectionStatistics getStatistics() {
        return connection.getStats();
    }
}
