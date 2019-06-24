package NetworkedPhysics.Network;

public interface UDPConnectionListener {
    void newMessage(int fromId, Message message);
    void disconnected(int id);
}
