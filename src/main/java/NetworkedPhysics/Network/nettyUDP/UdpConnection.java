package NetworkedPhysics.Network.nettyUDP;


import NetworkedPhysics.Network.Message;
import NetworkedPhysics.Network.UDPConnectionListener;
import io.netty.channel.socket.DatagramPacket;
import javafx.util.Pair;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static NetworkedPhysics.Network.nettyUDP.UdpSocket.packetToMessage;

class UdpConnection {
    public final InetSocketAddress inetSocketAddress;
    private byte messageStamp = 0;
    private byte remoteMessageStamp = 0;
    public final int id;
    private UdpSocket udpSocket;
    private Map<Byte, StampedMessage> sendMsgBuffer = new HashMap<>();
    private Map<Byte, Long> receivedStempBuffer = new HashMap<>();

    int resendTimeOut = 100;
    int lostPacketTimeOut = 1000;
    private byte lowStamp;
    private Timer resendTimer;

    public UdpConnection(InetSocketAddress inetSocketAddress, int id, UdpSocket udpSocket) {
        this.inetSocketAddress = inetSocketAddress;
        this.id = id;
        this.udpSocket = udpSocket;
    }

    public void updateTimeout(long currentTimeMillis) {

    }

    public byte nextStamp() {
        return ++messageStamp;
    }

    public void send(Message message) {
        byte stamp = nextStamp();
        udpSocket.send(message, stamp, inetSocketAddress);

        StampedMessage stampedMessage = new StampedMessage(message, stamp);
        sendMsgBuffer.put(stamp, stampedMessage);
    }

    private void checkResend(byte stamp) {
        for (byte i = lowStamp; i < stamp; i++) {
            Long timeStemp = receivedStempBuffer.get(i);
            if (timeStemp == null) {
                resendTimer.schedule(new Resend(i),resendTimeOut);
            }
        }
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
        boolean alreadyReceived = (receivedStempBuffer.get(stamp) == null);
        if (alreadyReceived)
            return;

        checkResend(stamp);
        receivedStempBuffer.put(stamp, System.currentTimeMillis());
        if (message.getCommandCode() == Byte.MIN_VALUE) {
            listener.disconnected(id);
        } else if (message.getCommandCode() == Byte.MIN_VALUE + 1) {
            send(sendMsgBuffer.get(message.getPacket()[0]).message);
        } else
            listener.newMessage(id, message);
    }

    class Resend extends TimerTask {
        byte stemp;

        public Resend(byte stemp) {
            this.stemp = stemp;
        }

        @Override
        public void run() {
            if (receivedStempBuffer.get(stemp) == null) {
                send(new ResendMsg(stemp));
            }
        }
    }
}

