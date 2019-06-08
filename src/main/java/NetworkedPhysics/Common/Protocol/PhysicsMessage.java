package NetworkedPhysics.Common.Protocol;

import NetworkedPhysics.Common.NetworkedPhysics;

import java.net.InetSocketAddress;

public abstract class PhysicsMessage {
    public final InetSocketAddress from;
    public final NetworkedPhysics networkedPhysics;

    protected PhysicsMessage(InetSocketAddress from, NetworkedPhysics networkedPhysics) {
        this.from = from;
        this.networkedPhysics = networkedPhysics;
    }

    public abstract byte[] toBlob();
    public abstract PhysicsMessage fromBlob(byte[] blob);
    public abstract void processMessage();
}
