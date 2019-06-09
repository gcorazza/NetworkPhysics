package NetworkedPhysics.Network;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.List;

class UdpSocketTest {

    UdpSocket  udpSocket;

    @BeforeEach
    public void before(){

    }

    @Test
    void shouldSendStringFromAtoB() throws InterruptedException {

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
        b.getChannel().writeAndFlush(new DatagramPacket(buffer,localhost));
        a.getChannel().writeAndFlush(new DatagramPacket(copy,localhost));
    }

    @Test
    void send() {
    }

    @Test
    void getLocalPort() {
    }
}
