package NetworkedPhysics.Network.nettyUDP;


import java.net.InetSocketAddress;

class UdpConnection {
    public final InetSocketAddress inetSocketAddress;
    private int messageStamp=0;
    private int remoteMessageStamp=0;
    public final int id;

    public UdpConnection(InetSocketAddress inetSocketAddress, int id) {
        this.inetSocketAddress = inetSocketAddress;
        this.id = id;
    }

    public void updateTimeout(long currentTimeMillis) {

    }

    public int nextStamp() {
        return ++messageStamp;
    }

}
