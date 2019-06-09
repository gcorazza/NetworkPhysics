package NetworkedPhysics.Common.Protocol;

import NetworkedPhysics.Common.NetworkedPhysics;

import java.net.InetSocketAddress;

public class GetInit extends PhysicsMessage{
    public static final byte COMMANDID=2;


    @Override
    public byte[] toBlob() {
        return new byte[0];
    }

    public PhysicsMessage fromBlob(byte[] blob) {
        return this;
    }

    @Override
    public void processMessage(NetworkedPhysics networkedPhysics, InetSocketAddress from) {
        networkedPhysics.getConnection().send(new InitialState(from, networkedPhysics),from);
    }

    @Override
    public byte getMessageID() {
        return COMMANDID;
    }
}
