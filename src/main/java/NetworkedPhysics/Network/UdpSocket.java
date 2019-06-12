package NetworkedPhysics.Network;

import NetworkedPhysics.Common.Protocol.PhysicsMessage;
import io.netty.bootstrap.Bootstrap;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.Charset;


public class UdpSocket {

    private Bootstrap bootstrap;
    private NioDatagramChannel channel;


    public UdpSocket(SimpleChannelInboundHandler<DatagramPacket> new_channel) {
        this(0, new_channel);
    }

    public UdpSocket(int localPort, SimpleChannelInboundHandler<DatagramPacket> new_channel) {
        this(new InetSocketAddress(localPort), new_channel);
    }

    public UdpSocket(InetSocketAddress localSocketAddress, SimpleChannelInboundHandler<DatagramPacket> datagramPacketInboundHandler) {
        final NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new ChannelInitializer<NioDatagramChannel>() {
                        @Override
                        protected void initChannel(NioDatagramChannel ch) throws Exception {
                            ch.pipeline().addLast(datagramPacketInboundHandler);
                        }
                    });
            // Bind and start to accept incoming connections.
            System.out.printf("waiting for message %s\n", String.format(localSocketAddress.toString()));
            System.out.flush();
            channel = (NioDatagramChannel) bootstrap.bind(localSocketAddress).sync().channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ChannelFuture connect(SocketAddress socketAddress) {
        return channel.connect(socketAddress);
    }

    public int getLocalPort() {
        return channel.localAddress().getPort();
    }

    public NioDatagramChannel getChannel() {
        return channel;
    }

    public void send(PhysicsMessage message){
        if (channel.isConnected()){
            ByteBuf buffer = blobToByteBuf(message);
            channel.writeAndFlush(new DatagramPacket(buffer,channel.remoteAddress()));
        }else{
            throw new RuntimeException("Not Connected: give a destination");
        }
    }

    public  void send(PhysicsMessage msg, InetSocketAddress destination){
        ByteBuf buffer = blobToByteBuf(msg);
        channel.writeAndFlush(new DatagramPacket(buffer,destination));
    }

    private ByteBuf blobToByteBuf(PhysicsMessage message) {
        byte[] bytes = message.toByteMessage();
        ByteBuf buffer = channel.alloc().buffer(bytes.length);
        buffer.writeBytes(bytes);
        return buffer;
    }

    public static void main(String[] args) {
        SimpleChannelInboundHandler<DatagramPacket> new_channel = new SimpleChannelInboundHandler<DatagramPacket>() {

            @Override
            protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
                System.out.println("got msg");
                String string = ((DatagramPacket) msg).content().toString(Charset.defaultCharset());
                System.out.print(string);
                ByteBuf buffer = ctx.alloc().buffer(string.getBytes().length);
                buffer.writeBytes(string.toUpperCase().getBytes());
                ctx.writeAndFlush(new DatagramPacket(buffer, ((DatagramPacket) msg).sender()));
            }
        };
        UdpSocket udpSocket = new UdpSocket(8080, new_channel);
        UdpSocket udpSocket2 = new UdpSocket(new_channel);
        udpSocket2.connect(new InetSocketAddress("localhost",8080));
    }

    public void shutdown() {
        bootstrap.config().group().shutdownGracefully();
    }
}
