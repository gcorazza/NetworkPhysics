package NetworkedPhysics.Common.Protocol.serverCommands.Manipulations;

import NetworkedPhysics.Common.NetworkPhysicsWorld;
import NetworkedPhysics.Common.PhysicsInput;
import NetworkedPhysics.Common.Protocol.PhysicsMessage;
import NetworkedPhysics.NetworkedPhysicsClient;

import static NetworkedPhysics.Util.Utils.gson;

public class AddInput extends WorldManipulation {
    private int id;
    private PhysicsInput input;
    public static final byte COMMANDID =9;

    public AddInput() {
    }

    public AddInput(int id, PhysicsInput input, int frame) {
        super(frame);
        this.id = id;
        this.input = input;
    }

    @Override
    public void processMessage(NetworkedPhysicsClient physicsClient) {
        physicsClient.getRewindableWorld().addManipulation(this);
    }

    @Override
    public PhysicsMessage fromBlob(byte[] blob) {
        return gson.fromJson(new String(blob), AddInput.class);
    }

    @Override
    public byte getCommandCode() {
        return COMMANDID;
    }

    @Override
    public byte[] getPacket() {
        return gson.toJson(this).getBytes();
    }

    @Override
    public void manipulate(NetworkPhysicsWorld networkedPhysics) {
        networkedPhysics.addInput(id, input);
    }
}
