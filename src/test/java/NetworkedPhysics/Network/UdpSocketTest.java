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

import static org.junit.jupiter.api.Assertions.assertTrue;

class UdpSocketTest {

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
        b.connect(localhost).awaitUninterruptibly();
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
        float tooManyPacketloss = 1.6f;
        final int packetSize = 500;
        int port = 8080;
        int packetsPerSecond = 1;
        Exchange<Integer> id = new Exchange<>();

        NettyUDPClient client = new NettyUDPClient(new UDPConnectionAdapter());
        NettyUDPServer server = new NettyUDPServer(new UDPServerAdapter() {
            @Override
            public void newClient(int ID) {
                id.set(ID);
            }

            @Override
            public void newMessage(int fromId, Message message) {
                //System.out.println(message.getCommandCode());
            }
        });

        server.startOn(port);
        client.connect(new InetSocketAddress("127.0.0.1", port));

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
        sleep(aSecond + 100);
        ConnectionStatistics stats = server.getStatistics(id.get());
        double packetLossOverLastSecond = 0;

        while (packetLossOverLastSecond < tooManyPacketloss) {
            long startSendPacketTime= System.currentTimeMillis();
            performOneSecondTackted(packetsPerSecond *= 2, () -> client.send(packetSizedMessage));
            int sendPeriod= (int) (System.currentTimeMillis()-startSendPacketTime);
            int receivedPacketsAmount = stats.howManyPacketsReceivedIn(sendPeriod+500);
            int incorrections = stats.howManyIncorrections(sendPeriod + 500);
            packetLossOverLastSecond = 1f - ((float) receivedPacketsAmount) / packetsPerSecond;
            System.out.println("packetLossOverLastSecond = " + packetLossOverLastSecond);
            System.out.println("receivedPacketsAmount = " + receivedPacketsAmount + "/" + packetsPerSecond);
            System.out.println("incorrections = " + incorrections);
            System.out.println("sendPeriod = " + sendPeriod);
            System.out.println("----");
            sleep(aSecond);
        }

        server.stop();
        client.disconnect();

        assertTrue(true);
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
        for (int i = 0; i < tack; i++) {
            fnc.run();
            float v = aSecondf / tack;
            sleep(v);
        }
    }

    private long timeSince(long startTime) {
        return System.currentTimeMillis() - startTime;
    }

    private void sleep(float millis) {
        if (millis <= 0) {
            return;
        }
        try {
            int nanos = (int) (millis * 1000000);
            if (nanos>=1000000){
                nanos=0;
            }else{
                millis=0;
            }
            Thread.sleep((long) millis, nanos);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
