package NetworkedPhysics.Common.Protocol;

import NetworkedPhysics.Client.NetworkedPhysicsClient;
import NetworkedPhysics.Common.RewindablePhysicsWorld;
import NetworkedPhysics.Network.UdpConnection;

public class GetWorldState extends PhysicsMessage{
    public static final byte COMMANDID=2;

    public GetWorldState(int stamp) {
        super(stamp);
    }

    @Override
    public byte[] toBlob() {
        return new byte[0];
    }

    public GetWorldState fromBlob(byte[] blob) {
        return this;
    }

    @Override
    public void processMessage(RewindablePhysicsWorld rewindablePhysicsWorld, UdpConnection from) {
        ((NetworkedPhysicsClient) rewindablePhysicsWorld).sendTo(from, rewindablePhysicsWorld.saveState());
    }

    @Override
    public byte getCommandID() {
        return COMMANDID;
    }
}
