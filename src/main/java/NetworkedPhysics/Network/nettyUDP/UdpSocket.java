package NetworkedPhysics.Network.nettyUDP;

import NetworkedPhysics.Network.Message;
import io.netty.bootstrap.Bootstrap;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.concurrent.Future;
import javafx.util.Pair;

import java.net.InetSocketAddress;
import java.net.SocketAddress;


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
            // Bind and startOn to accept incoming connections.
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

    public NioDatagramChannel getChannel() {
        return channel;
    }

    public void send(Message msg, int stamp) {
        if (channel.isConnected()) {
            ByteBuf buffer = messageToByteBuf(msg, stamp);
            channel.writeAndFlush(new DatagramPacket(buffer, channel.remoteAddress()));
        } else {
            throw new RuntimeException("Not Connected: give a destination");
        }
    }

    public void send(Message msg, int stamp, InetSocketAddress destination) {
        ByteBuf buffer = messageToByteBuf(msg, stamp);
        channel.writeAndFlush(new DatagramPacket(buffer, destination));
    }

    private ByteBuf messageToByteBuf(Message msg, int stamp) {
        byte[] bytes = msg.getPacket();
        ByteBuf buffer = channel.alloc().buffer(bytes.length + 2);
        buffer.writeByte(stamp);
        buffer.writeByte(msg.getCommandCode());
        buffer.writeBytes(bytes);
        return buffer;
    }

    static Pair<Message, Byte> packetToMessage(DatagramPacket packet) {
        final ByteBuf buf = packet.content();
        final int rcvPktLength = buf.readableBytes();
        final byte[] payload = new byte[rcvPktLength - 2];
        byte stamp = buf.readByte();
        byte code = buf.readByte();
        buf.readBytes(payload);
        Message message = new Message() {
            @Override
            public byte getCommandCode() {
                return code;
            }

            @Override
            public byte[] getPacket() {
                return payload;
            }
        };
        return new Pair<>(message, Byte.valueOf(stamp));
    }

    public Future<?> shutdown() {
        return bootstrap.config().group().shutdownGracefully();
    }
}
