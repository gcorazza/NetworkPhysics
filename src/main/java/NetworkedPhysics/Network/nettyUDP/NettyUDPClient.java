package NetworkedPhysics.Network.nettyUDP;

import NetworkedPhysics.Network.Message;
import NetworkedPhysics.Network.UDPClient;
import NetworkedPhysics.Network.UDPClientListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;

public class NettyUDPClient implements UDPClient {

    UDPClientListener listener;

    @Override
    public void connect(InetSocketAddress socketAddress) {
        new UdpSocket(0, new SimpleChannelInboundHandler<DatagramPacket>() {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
                //listener.newMessage();
            }
        });
    }

    @Override
    public void disconnect() {

    }

    @Override
    public void send(Message message) {

    }

    @Override
    public void setListener(UDPClientListener listener) {

    }
}
