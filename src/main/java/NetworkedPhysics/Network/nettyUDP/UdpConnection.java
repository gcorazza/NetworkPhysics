package NetworkedPhysics.Network.nettyUDP;


import NetworkedPhysics.Network.Message;
import NetworkedPhysics.Network.UDPConnectionListener;
import io.netty.channel.socket.DatagramPacket;
import javafx.util.Pair;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import static NetworkedPhysics.Network.nettyUDP.UdpSocket.packetToMessage;

class UdpConnection {
    public final InetSocketAddress inetSocketAddress;
    private short messageStamp = 0;
    public final int id;
    private UdpSocket udpSocket;
    private List<Short> receivedStempBuffer = new ArrayList<>();

    private final int stampRoundTime = 50;
    private ConnectionStatistics stats = new ConnectionStatistics();
    private int sendTimes=2;

    public UdpConnection(InetSocketAddress inetSocketAddress, int id, UdpSocket udpSocket) {
        this.inetSocketAddress = inetSocketAddress;
        this.id = id;
        this.udpSocket = udpSocket;
    }

    public short nextStamp() {
        return ++messageStamp;
    }

    public void send(Message message) {
        short stamp = nextStamp();
        int sendTimes= getSendTimes();
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
        } else
            listener.newMessage(id, message);
    }

    private void received(short stamp) {
        receivedStempBuffer.add(stamp);
        if (receivedStempBuffer.size()>10000){
            receivedStempBuffer.remove(0);
        }
    }

    public ConnectionStatistics getStats() {
        return stats;
    }
}

