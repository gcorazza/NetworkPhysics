package NetworkedPhysics.Network;

import java.net.InetSocketAddress;

public interface UDPClient {
    void connect(InetSocketAddress socketAddress);
    void disconnect();
    void send(Message message);
    void setListener(UDPConnectionListener listener);
}
