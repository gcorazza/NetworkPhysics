package NetworkedPhysics.Network.nettyUDP;

import NetworkedPhysics.Network.Message;
import NetworkedPhysics.Network.UDPServer;
import NetworkedPhysics.Network.UDPServerListener;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;

public class NettyUDPServer implements UDPServer {

    private UDPServerListener listener;
    private UdpSocket serverSocket;

    private ConnectionMapper connectionMapper = new ConnectionMapper();

    @Override
    public void setListener(UDPServerListener listener) {
        this.listener = listener;
    }

    @Override
    public void start(int port) {
        serverSocket = new UdpSocket(port, new SimpleChannelInboundHandler<DatagramPacket>() {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
                UdpConnection connection = connectionMapper.get(msg.sender());

                if (connection == null) {
                    connection = connectionMapper.newConnection(msg.sender());
                    listener.newClient(connection.id);
                }
                listener.newMessage(connection.id, packetToMessage(msg));
            }
        });
    }

    static Message packetToMessage(DatagramPacket packet) {
        final ByteBuf buf = packet.content();
        final int rcvPktLength = buf.readableBytes();
        final byte[] payload = new byte[rcvPktLength - 1];
        byte code = buf.readByte();
        buf.readBytes(payload);
        return new Message() {
            @Override
            public byte getCommandCode() {
                return code;
            }

            @Override
            public byte[] getPacket() {
                return payload;
            }
        };
    }

    @Override
    public void stop() {
        serverSocket.shutdown().awaitUninterruptibly();
    }

    @Override
    public void send(int id, Message message) {
        UdpConnection udpConnection = connectionMapper.get(id);
        if (udpConnection != null) {
            serverSocket.send(message, udpConnection.inetSocketAddress);
        }
    }

    @Override
    public void sendToAll(Message message) {
        connectionMapper.connections().forEach(udpConnection -> serverSocket.send(message, udpConnection.inetSocketAddress));
    }
}
