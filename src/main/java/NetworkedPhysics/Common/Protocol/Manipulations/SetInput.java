package NetworkedPhysics.Common.Protocol.Manipulations;

import NetworkedPhysics.Common.NetworkedPhysics;
import NetworkedPhysics.Common.PhysicsInput;
import NetworkedPhysics.Common.Protocol.PhysicsMessage;
import NetworkedPhysics.Network.UdpConnection;

public class SetInput extends WorldManipulation{
    PhysicsInput input;

    public SetInput(int frame, PhysicsInput input) {
        super(frame);
        this.input = input;
    }

    @Override
    public void manipulate(NetworkedPhysics networkedPhysics) {
        networkedPhysics.setInput(input);
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
        networkedPhysics.addManipulation(this);
    }

    @Override
    public byte getCommandID() {
        return 0;
    }
}
