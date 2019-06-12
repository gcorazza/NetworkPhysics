package NetworkedPhysics.Common.Protocol;

import NetworkedPhysics.Common.NetworkedPhysics;
import NetworkedPhysics.Network.UdpConnection;

public class GetInit extends PhysicsMessage{
    public static final byte COMMANDID=2;

    public GetInit(int stamp) {
        super(stamp);
    }

    @Override
    public byte[] toBlob() {
        return new byte[0];
    }

    public GetInit fromBlob(byte[] blob) {
        return this;
    }

    @Override
    public void processMessage(NetworkedPhysics networkedPhysics, UdpConnection from) {
        networkedPhysics.send(new InitPhysicsEngine(networkedPhysics, from.getMessageStamp()),from);
    }

    @Override
    public byte getCommandID() {
        return COMMANDID;
    }
}
