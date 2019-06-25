package NetworkedPhysics.Network;

import NetworkedPhysics.Network.nettyUDP.ConnectionStatistics;
import NetworkedPhysics.Network.nettyUDP.NettyUDPClient;
import NetworkedPhysics.Network.nettyUDP.NettyUDPServer;
import NetworkedPhysics.Network.nettyUDP.UdpSocket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.socket.DatagramPacket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

class UdpSocketTest {

    UdpSocket udpSocket;

    @BeforeEach
    public void before() {

    }

    @Test
    void shouldSendStringFromAtoB() {

        SimpleChannelInboundHandler<DatagramPacket> simpleChannelInboundHandler = new SimpleChannelInboundHandler<DatagramPacket>() {

            @Override
            protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
                System.out.println(msg.content().toString(Charset.defaultCharset()));
            }
        };

        SimpleChannelInboundHandler<DatagramPacket> simpleChannelInboundHandler2 = new SimpleChannelInboundHandler<DatagramPacket>() {

            @Override
            protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
                System.out.println(msg.content().toString(Charset.defaultCharset()));
            }
        };

        UdpSocket a = new UdpSocket(8080, simpleChannelInboundHandler);
        UdpSocket b = new UdpSocket(simpleChannelInboundHandler2);
        InetSocketAddress localhost = new InetSocketAddress("localhost", 8080);
        b.connect(localhost);
        byte[] bytes = "String".getBytes();
        ByteBuf buffer = a.getChannel().alloc().buffer(bytes.length);
        buffer.writeBytes(bytes);
        ByteBuf copy = buffer.copy();
        b.getChannel().writeAndFlush(new DatagramPacket(buffer, localhost));
        a.getChannel().writeAndFlush(new DatagramPacket(copy, localhost));
    }

    @Test
    void the_packet_loss_should_go_up_when_too_many_packet_are_send() {
        float tooManyPacketloss = 0.2f;
        int port = 8080;

        NettyUDPServer server = new NettyUDPServer();
        server.startOn(port);

        NettyUDPClient client = new NettyUDPClient();
        client.connect(new InetSocketAddress("localhost", port));

        ConnectionStatistics stats = client.getStatistics();
        double packetLossOverLastSecond = stats.getPacketLossOver(1000);




    }

}
