package NetworkedPhysics.Common.Protocol.Manipulations;

import NetworkedPhysics.Common.NetworkPhysicsWorld;
import NetworkedPhysics.Common.RewindablePhysicsWorld;
import NetworkedPhysics.Common.PhysicsInput;
import NetworkedPhysics.Common.Protocol.PhysicsMessage;
import NetworkedPhysics.Common.Util;
import NetworkedPhysics.Network.UdpConnection;

public class SetInput extends WorldManipulation{
    public static final byte COMMANDID=4;
    private int id;
    PhysicsInput input;

    public SetInput(int frame, int id, PhysicsInput input) {
        super(frame);
        this.id = id;
        this.input = input;
    }

    @Override
    public void manipulate(NetworkPhysicsWorld networkedPhysics) {
        networkedPhysics.setInput(input, id);
    }

    @Override
    public byte[] toBlob() {
        return Util.gson.toJson(this).getBytes();
    }

    @Override
    public PhysicsMessage fromBlob(byte[] blob) {
        return Util.gson.fromJson(new String(blob), SetInput.class);
    }

    @Override
    public void processMessage(RewindablePhysicsWorld rewindablePhysicsWorld, UdpConnection from) {
        rewindablePhysicsWorld.addManipulation(this);
    }

    @Override
    public byte getCommandID() {
        return COMMANDID;
    }
}
