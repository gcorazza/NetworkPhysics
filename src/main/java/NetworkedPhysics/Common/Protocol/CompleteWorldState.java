package NetworkedPhysics.Common.Protocol;

import NetworkedPhysics.Common.NetworkedPhysics;
import NetworkedPhysics.Network.UdpConnection;

public class CompleteWorldState extends PhysicsMessage{
    public static final byte COMMANDID=1;
    int frame;
    long btSeed;

    protected CompleteWorldState(NetworkedPhysics networkedPhysics, int stamp) {
        super(stamp);
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
    public void processMessage(NetworkedPhysics networkedPhysics, UdpConnection from) {

    }

    @Override
    public byte getMessageID() {
        return COMMANDID;
    }
}
