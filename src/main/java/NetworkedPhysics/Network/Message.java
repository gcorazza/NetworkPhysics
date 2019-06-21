package NetworkedPhysics.Network;

public interface Message {
    byte getCommandCode();
    byte[] getPacket();
}
