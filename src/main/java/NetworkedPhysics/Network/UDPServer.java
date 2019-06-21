package NetworkedPhysics.Network;

public interface UDPServer {
    void setListener(UDPServerListener listener);
    void start(int port);
    void stop();
    void send(int id, Message message);
    void sendToAll(Message message);
}
