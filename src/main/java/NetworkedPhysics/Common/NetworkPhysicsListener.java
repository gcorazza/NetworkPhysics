package NetworkedPhysics.Common;

import NetworkedPhysics.Network.UdpConnection;

import java.net.InetSocketAddress;

public interface NetworkPhysicsListener {
    public void newObject(int physicsObject);

    public void deleteObject(int id);

    public void newClient(InetSocketAddress id);

    public void rewinded();

    void clientInput(PhysicsInput clientInput, InetSocketAddress from);
}
