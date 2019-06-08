package NetworkedPhysics.Network.Messages;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class UdpClient {
    public final InetSocketAddress inetSocketAddress;

    public UdpClient(InetSocketAddress inetSocketAddress) {
        this.inetSocketAddress = inetSocketAddress;
    }

    public void updateTimeout(long currentTimeMillis) {

    }
}
