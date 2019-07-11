package NetworkedPhysics.Network.nettyUDP;


import NetworkedPhysics.Network.Message;
import NetworkedPhysics.Network.UDPConnectionListener;
import NetworkedPhysics.Util.Pair;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.function.BiFunction;

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
    private static byte pingCode = Byte.MIN_VALUE + 1;
    private long lastPingSend;
    private int ping;
    public static final int maxUDPSize = 500;

    public static final byte partCode = Byte.MIN_VALUE + 2;
    private byte messagePartId;
    private HashMap<Byte, MessageAssembler> partMap = new HashMap<>();
    private static Map<Byte, BiFunction<Pair<Message, Short>,UdpConnection, Void>>  protocol = new HashMap<>();
    private static byte disconnectCode = Byte.MIN_VALUE;
    static {
        protocol.put(disconnectCode, (ms, con)-> con.disconnected());
        protocol.put(pingCode, (ms, con)-> con.gotPing());
        protocol.put(partCode, (ms, con)-> con.partMessage(ms));
    }
    private UDPConnectionListener listener;

    public UdpConnection(InetSocketAddress inetSocketAddress, int id, UdpSocket udpSocket, UDPConnectionListener listener) {
        this.inetSocketAddress = inetSocketAddress;
        this.id = id;
        this.udpSocket = udpSocket;
        this.listener = listener;
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
        byte[] packet = message.getPacket();
        if (packet.length > maxUDPSize) {
            messageToParts(message).forEach(this::send);
        } else {
            short stamp = nextStamp();
            int sendTimes = getSendTimes();
            for (int i = 0; i < sendTimes; i++) {
                udpSocket.send(message, stamp, inetSocketAddress);
            }
        }

    }

    private ArrayList<Message> messageToParts(Message message) {
        System.out.println(new String(message.getPacket()));
        ArrayList<Message> parts = new ArrayList<>();
        messagePartId++;
        byte[] msgPacket = message.getPacket();
        byte[] packet = new byte[msgPacket.length + 1];
        packet[0] = message.getCommandCode();
        System.arraycopy(msgPacket, 0, packet, 1, msgPacket.length);
        int sendIndex = 0;
        while (sendIndex < packet.length) {
            byte[] part;

            if (sendIndex + maxUDPSize - 5 > packet.length) {
                part = new byte[packet.length - sendIndex];
            } else {
                part = new byte[maxUDPSize - 5];
            }

            System.arraycopy(packet, sendIndex, part, 0, part.length);
            sendIndex += part.length;
            parts.add(new PartMessage(messagePartId, part, (byte) (packet.length / (maxUDPSize - 5))));
        }


        return parts;
    }

    private int getSendTimes() {
        return sendTimes;
    }


    void receiveMessage(DatagramPacket msg) {
        Pair<Message, Short> messageStampPair = packetToMessage(msg);
        Message message = messageStampPair.getKey();
        short stamp = messageStampPair.getValue();

        stats.received(stamp);
        if (receivedStempBuffer.contains(stamp)) {
            return;
        }

        received(stamp);

        BiFunction<Pair<Message, Short>, UdpConnection, Void> function = protocol.get(message.getCommandCode());
        if (function != null) {
            function.apply(messageStampPair, this);
        }else{
            listener.newMessage(id, message);
        }
    }

    private Void partMessage(Pair<Message, Short> ms) {
        byte[] messagePacket = ms.getKey().getPacket();
        byte partId = messagePacket[0];
        MessageAssembler messagePart = partMap.get(partId);
        if (messagePart == null) {
            int partAmount = messagePacket[1]+1;
            messagePart = new MessageAssembler(partAmount);
            partMap.put(partId, messagePart);
        }
        messagePart.add(ms.getValue(), messagePacket);
        if (messagePart.isReady()) {
            listener.newMessage(id, messagePart.getMessage());
            partMap.remove(partId);
        }
        return null;
    }

    private Void gotPing() {
        ping = (int) (System.currentTimeMillis() - lastPingSend);
        return null;
    }

    private Void disconnected() {
        listener.disconnected(id);
        return null;
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

    private class MessageAssembler {
        private int partAmount;
        private int partsArrived;

        ArrayList<MessagePart> parts = new ArrayList<>();

        public MessageAssembler(int partAmount) {
            this.partAmount = partAmount;
        }

        public void add(short stamp, byte[] messagePacket) {
            partsArrived++;
            parts.add(new MessagePart(stamp, messagePacket));
        }

        public boolean isReady() {
            return partsArrived == partAmount;
        }

        public Message getMessage() {
            parts.sort(Comparator.comparingInt(o -> o.stamp));
            int msgLength = parts.stream().mapToInt(m -> m.messagePacket.length).sum();
            byte[] finalMsg = new byte[msgLength - 1];
            final int[] msgIndex = {0};
            byte[] messagePacket0 = parts.get(0).messagePacket;
            byte code = messagePacket0[0];
            byte[] msgWithoutCode = new byte[messagePacket0.length - 1];
            System.arraycopy(messagePacket0, 1, msgWithoutCode, 0, msgWithoutCode.length);
            parts.get(0).messagePacket=msgWithoutCode;
            parts.forEach(m -> {
                System.arraycopy(m.messagePacket, 0, finalMsg, msgIndex[0], m.messagePacket.length);
                msgIndex[0] += m.messagePacket.length;
            });
            return new Message() {
                @Override
                public byte getCommandCode() {
                    return code;
                }

                @Override
                public byte[] getPacket() {
                    return finalMsg;
                }
            };
        }

        private class MessagePart {
            private short stamp;
            private byte[] messagePacket;

            public MessagePart(short stamp, byte[] messagePacket) {
                this.stamp = stamp;
                this.messagePacket = new byte[messagePacket.length - 2];
                System.arraycopy(messagePacket, 2, this.messagePacket, 0, this.messagePacket.length);
            }

            public short getStamp() {
                return stamp;
            }
        }
    }

    public void close(){
        pingTimer.cancel();
    }
}

