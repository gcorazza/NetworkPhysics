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
import java.util.concurrent.Semaphore;

class UdpSocketTest {

    UdpSocket udpSocket;
    private final int aSecond = 1000;
    private final float aSecondf = 1000f;

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

        a.shutdown().awaitUninterruptibly();
        b.shutdown().awaitUninterruptibly();

    }

    @Test
    void the_packet_loss_should_go_up_when_too_many_packet_are_send() throws InterruptedException {
        //Test fails, when CPU ist slower than Databandwidth from Network
        float tooManyPacketloss = 20.2f;
        final int packetSize = 300;
        int port = 8080;
        int packetsPerSecond = 1;
        Exchange<Integer> id = new Exchange<>();

        NettyUDPClient client = new NettyUDPClient(new UDPConnectionAdapter());
        NettyUDPServer server = new NettyUDPServer(new UDPServerAdapter() {
            @Override
            public void newClient(int ID) {
                id.set(ID);
            }
        });

        server.startOn(port);
        client.connect(new InetSocketAddress("192.168.100.42", port));

        Message packetSizedMessage = new Message() {

            @Override
            public byte getCommandCode() {
                return 0;
            }

            @Override
            public byte[] getPacket() {
                return new byte[packetSize - 1];
            }
        };

        client.send(packetSizedMessage);
        sleep(aSecond);
        ConnectionStatistics stats = server.getStatistics(id.get());
        double packetLossOverLastSecond = 0;

        while (packetLossOverLastSecond < tooManyPacketloss) {
            performOneSecondTackted(packetsPerSecond *= 2, () -> client.send(packetSizedMessage));
            sleep(50);
//            packetLossOverLastSecond = stats.getPacketLossOver(aSecond+50);
            int receivedPacketsAmount = stats.howManyPacketsReceivedIn(aSecond + 50);
            packetLossOverLastSecond= ((float) receivedPacketsAmount)/packetsPerSecond;
            System.out.println(packetLossOverLastSecond);
        }

        server.stop();
        client.disconnect();
    }

    class Exchange<V> {
        private V v;
        private Semaphore semaphore = new Semaphore(0);

        public V get() throws InterruptedException {
            semaphore.acquire();
            semaphore.release();
            return v;
        }

        public void set(V v) {
            this.v = v;
            semaphore.release(1);
        }

    }


    private void performOneSecondTackted(int tack, Runnable fnc) {
        long startTime = System.currentTimeMillis();

        while (timeSince(startTime) < aSecond) {
            fnc.run();
            sleep(aSecondf / tack);
        }
    }

    private long timeSince(long startTime) {
        return System.currentTimeMillis() - startTime;
    }

    private void sleep(float v) {
        if (v <= 0) {
            return;
        }
        try {
            Thread.sleep((long) v);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
