package NetworkedPhysics.Network.nettyUDP;


import NetworkedPhysics.Network.Message;
import NetworkedPhysics.Network.UDPConnectionListener;
import io.netty.channel.socket.DatagramPacket;
import javafx.util.Pair;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import static NetworkedPhysics.Network.nettyUDP.UdpSocket.packetToMessage;

class UdpConnection {
    public final InetSocketAddress inetSocketAddress;
    private byte messageStamp = 0;
    public final int id;
    private UdpSocket udpSocket;
    private Map<Byte, StampedMessage> sendMsgBuffer = new HashMap<>();
    private Map<Byte, Long> receivedStempBuffer = new HashMap<>();

    private final int stampRoundTime = 50;
    private ConnectionStatistics stats = new ConnectionStatistics();

    public UdpConnection(InetSocketAddress inetSocketAddress, int id, UdpSocket udpSocket) {
        this.inetSocketAddress = inetSocketAddress;
        this.id = id;
        this.udpSocket = udpSocket;
    }

    public byte nextStamp() {
        return ++messageStamp;
    }

    public void send(Message message) {
        byte stamp = nextStamp();
        udpSocket.send(message, stamp, inetSocketAddress);
    }


    private class StampedMessage {
        private Message message;
        private byte stamp;

        public StampedMessage(Message message, byte stamp) {
            this.message = message;
            this.stamp = stamp;
        }
    }

    void receiveMessage(DatagramPacket msg, UDPConnectionListener listener) {
        Pair<Message, Byte> messageStampPair = packetToMessage(msg);
        Message message = messageStampPair.getKey();
        byte stamp = messageStampPair.getValue();

        if (receivedStempBuffer.get(stamp) != null)
            if (System.currentTimeMillis() - receivedStempBuffer.get(stamp) < stampRoundTime)
                return;

        stats.addStamp(stamp);

        receivedStempBuffer.put(stamp, System.currentTimeMillis());
        if (message.getCommandCode() == Byte.MIN_VALUE) {
            listener.disconnected(id);
        } else if (message.getCommandCode() == Byte.MIN_VALUE + 1) {
            send(sendMsgBuffer.get(message.getPacket()[0]).message);
        } else
            listener.newMessage(id, message);
    }

    public ConnectionStatistics getStats() {
        return stats;
    }
}

