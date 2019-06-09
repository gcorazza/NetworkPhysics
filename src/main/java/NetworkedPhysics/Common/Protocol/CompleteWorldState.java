package NetworkedPhysics.Common.Protocol;

import NetworkedPhysics.Client.NetworkedPhysicsClient;
import NetworkedPhysics.Common.NetworkedPhysics;
import NetworkedPhysics.Network.Client;

import java.net.InetSocketAddress;

public class CompleteWorldState extends PhysicsMessage{
    public static final byte COMMANDID=1;
    int frame;
    long btSeed;

    protected CompleteWorldState(InetSocketAddress from, NetworkedPhysics networkedPhysics) {
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
    public void processMessage(NetworkedPhysics networkedPhysics, InetSocketAddress from) {
    }

    @Override
    public byte getMessageID() {
        return COMMANDID;
    }
}
