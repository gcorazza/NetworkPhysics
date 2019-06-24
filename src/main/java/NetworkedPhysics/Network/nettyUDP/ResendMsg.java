package NetworkedPhysics.Network.nettyUDP;


import NetworkedPhysics.Network.Message;

public class ResendMsg implements Message {

    byte stemp;

    public ResendMsg(byte stemp) {
        this.stemp = stemp;
    }

    @Override
    public byte getCommandCode() {
        return Byte.MIN_VALUE+1;
    }

    @Override
    public byte[] getPacket() {
        byte[] bytes = new byte[1];
        bytes[0]=stemp;
        return bytes;
    }
}
