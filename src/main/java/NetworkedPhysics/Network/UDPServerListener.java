package NetworkedPhysics.Network;

public interface UDPServerListener {
    void newClient(int id);
    void disconnected(int id);
    void newMessage(int fromId, Message message);
}
