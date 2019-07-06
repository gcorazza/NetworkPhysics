package NetworkedPhysics.Network.nettyUDP;

import NetworkedPhysics.Network.Message;

import static NetworkedPhysics.Network.nettyUDP.UdpConnection.partCode;

public class PartMessage implements Message {
    private byte messagePartId;
    private byte[] part;
    private byte partAmount;

    public PartMessage(byte messagePartId, byte[] part, byte partAmount) {
        this.messagePartId = messagePartId;
        this.part = part;
        this.partAmount = partAmount;
    }

    @Override
    public byte getCommandCode() {
        return partCode;
    }

    @Override
    public byte[] getPacket() {
        byte[] mes = new byte[part.length+2];
        mes[0] = messagePartId;
        mes[1] = partAmount;
        System.arraycopy(part, 0, mes, 2, part.length);
        return mes;
    }
}
