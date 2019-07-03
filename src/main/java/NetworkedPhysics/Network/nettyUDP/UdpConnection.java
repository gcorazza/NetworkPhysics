package NetworkedPhysics.Network.nettyUDP;


import NetworkedPhysics.Network.Message;
import NetworkedPhysics.Network.UDPConnectionListener;
import io.netty.channel.socket.DatagramPacket;
import javafx.util.Pair;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static NetworkedPhysics.Network.nettyUDP.UdpSocket.packetToMessage;

class UdpConnection {
    public final InetSocketAddress inetSocketAddress;
    private short messageStamp = 0;
    public final int id;
    private UdpSocket udpSocket;
    private List<Short> receivedStempBuffer = new ArrayList<>();

    private ConnectionStatistics stats = new ConnectionStatistics();
    private int sendTimes = 2;

    private static final int timeOutPeriod = Integer.MAX_VALUE;
    private long lastMessage = System.currentTimeMillis();
    private int recievedStampBufferSize = 1000;

    private Timer pingTimer = new Timer();
    private byte pingCode = Byte.MIN_VALUE + 1;
    private long lastPingSend;
    private int ping;

    public UdpConnection(InetSocketAddress inetSocketAddress, int id, UdpSocket udpSocket) {
        this.inetSocketAddress = inetSocketAddress;
        this.id = id;
        this.udpSocket = udpSocket;
        pingTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                send(new Message() {
                    @Override
                    public byte getCommandCode() {
                        return pingCode;
                    }

                    @Override
                    public byte[] getPacket() {
                        return new byte[0];
                    }
                });
                lastPingSend = System.currentTimeMillis();
            }
        }, 0, 1000);
    }

    public short nextStamp() {
        return ++messageStamp;
    }

    public void send(Message message) {
        short stamp = nextStamp();
        int sendTimes = getSendTimes();
        for (int i = 0; i < sendTimes; i++) {
            udpSocket.send(message, stamp, inetSocketAddress);
        }
    }

    private int getSendTimes() {
        return sendTimes;
    }


    void receiveMessage(DatagramPacket msg, UDPConnectionListener listener) {
        Pair<Message, Short> messageStampPair = packetToMessage(msg);
        Message message = messageStampPair.getKey();
        short stamp = messageStampPair.getValue();

        if (receivedStempBuffer.contains(stamp))
            return;

        stats.received(stamp);
        received(stamp);

        if (message.getCommandCode() == Byte.MIN_VALUE) {
            listener.disconnected(id);
        } else if (message.getCommandCode() == pingCode) {
            ping = (int) (System.currentTimeMillis() - lastPingSend);
        } else
            listener.newMessage(id, message);
    }

    private void received(short stamp) {
        lastMessage = System.currentTimeMillis();
        receivedStempBuffer.add(stamp);
        if (receivedStempBuffer.size() > recievedStampBufferSize) {
            receivedStempBuffer.remove(0);
        }
    }

    public ConnectionStatistics getStats() {
        return stats;
    }

    public boolean hasTimeOut() {
        return (System.currentTimeMillis() - lastMessage) > timeOutPeriod;
    }

    public int getPing() {
        return ping;
    }
}

