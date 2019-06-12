package NetworkedPhysics.Network;

import NetworkedPhysics.Common.NetworkedPhysics;
import NetworkedPhysics.Common.Protocol.PhysicsMessage;
import NetworkedPhysics.Common.Protocol.Protocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public abstract class IncomingPacketHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private static final Map<Byte, Class<? extends PhysicsMessage>> commandMap = Protocol.protocol;
    protected final NetworkedPhysics networkedPhysics;

    protected IncomingPacketHandler(NetworkedPhysics networkedPhysics) {
        this.networkedPhysics = networkedPhysics;
    }


    static byte[] readByteBuffer(DatagramPacket packet) {
        final ByteBuf buf = packet.content();
        final int rcvPktLength = buf.readableBytes();
        final byte[] rcvPktBuf = new byte[rcvPktLength];
        buf.readBytes(rcvPktBuf);
        return rcvPktBuf;
    }

    void processIncomingMessage(UdpConnection udpConnection, DatagramPacket packet) {

        final byte[] rcvPktBuf = readByteBuffer(packet);

        System.out.println(new String(rcvPktBuf));

        byte[] packetObject = new byte[rcvPktBuf.length - 1];
        byte commandCode = rcvPktBuf[0];

        try {
            System.arraycopy(rcvPktBuf, 1, packetObject, 0, packetObject.length);

            PhysicsMessage instance = (((commandMap.get(commandCode))
                    .getConstructor(int.class)
                    .newInstance(0)))
                    .fromBlob(packetObject);
            instance.processMessage(networkedPhysics,udpConnection);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
