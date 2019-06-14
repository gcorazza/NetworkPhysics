package NetworkedPhysics.Common.Protocol;

import NetworkedPhysics.Common.NetworkedPhysics;
import NetworkedPhysics.Network.UdpConnection;
import NetworkedPhysics.Server.NetworkedPhysicsServer;

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
    public void processMessage(NetworkedPhysics networkedPhysics, UdpConnection from) {
        networkedPhysics.sendTo(from, networkedPhysics.getWorldState());
    }

    @Override
    public byte getCommandID() {
        return COMMANDID;
    }
}
