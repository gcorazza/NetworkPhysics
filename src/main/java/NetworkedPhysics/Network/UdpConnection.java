package NetworkedPhysics.Network;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class UdpConnection {
    public final InetSocketAddress inetSocketAddress;
    private int messageStamp=0;
    private int remoteMessageStamp;

    public UdpConnection(InetSocketAddress inetSocketAddress) {
        this.inetSocketAddress = inetSocketAddress;
    }

    public void updateTimeout(long currentTimeMillis) {

    }

    public int nextStamp() {
        return ++messageStamp;
    }
}
