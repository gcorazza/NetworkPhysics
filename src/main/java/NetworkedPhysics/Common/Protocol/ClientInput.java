package NetworkedPhysics.Common.Protocol;

import NetworkedPhysics.Common.NetworkedPhysics;
import NetworkedPhysics.Common.PhysicsInput;
import NetworkedPhysics.Network.UdpConnection;
import NetworkedPhysics.Server.NetworkedPhysicsServer;

public class ClientInput extends PhysicsMessage {

    PhysicsInput clientInput;

    public ClientInput(PhysicsInput clientInput) {
        this.clientInput = clientInput;
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
        ((NetworkedPhysicsServer) networkedPhysics).setClientInput(clientInput);
    }

    @Override
    public byte getCommandID() {
        return 0;
    }
}
