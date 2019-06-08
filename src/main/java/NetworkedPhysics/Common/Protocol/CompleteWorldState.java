package NetworkedPhysics.Common.Protocol;

import NetworkedPhysics.Common.NetworkedPhysics;

import java.net.InetSocketAddress;

public class CompleteWorldState extends PhysicsMessage{
    int frame;
    long btSeed;

    protected CompleteWorldState(InetSocketAddress from, NetworkedPhysics networkedPhysics) {
        super(from, networkedPhysics);
    }

    @Override
    public byte[] toBlob() {
        return new byte[0];
    }

    @Override
    public PhysicsMessage fromBlob(byte[] blob) {
        return null;
    }

    @Override
    public void processMessage() {

    }
}
