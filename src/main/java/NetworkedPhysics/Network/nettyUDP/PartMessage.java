package NetworkedPhysics.Network.nettyUDP;

import NetworkedPhysics.Network.Message;

import static NetworkedPhysics.Network.nettyUDP.UdpConnection.partCode;

public class PartMessage implements Message {
    private byte messagePartId;
    private byte[] part;

    public PartMessage(byte messagePartId, byte[] part) {
        this.messagePartId = messagePartId;
        this.part = part;
    }

    @Override
    public byte getCommandCode() {
        return partCode;
    }

    @Override
    public byte[] getPacket() {
        byte[] mes = new byte[part.length+1];
        mes[0] = messagePartId;
        System.arraycopy(part, 0, mes, 1, part.length);
        return mes;
    }
}
