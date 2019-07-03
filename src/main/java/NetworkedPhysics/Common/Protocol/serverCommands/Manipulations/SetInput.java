package NetworkedPhysics.Common.Protocol.serverCommands.Manipulations;

import NetworkedPhysics.Common.NetworkPhysicsWorld;
import NetworkedPhysics.Common.PhysicsInput;
import NetworkedPhysics.NetworkedPhysicsClient;

import static Util.Utils.gson;

public class SetInput extends WorldManipulation{
    public static final byte COMMANDID=4;
    private int id;
    PhysicsInput input;

    public SetInput() {
    }

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
    public SetInput fromBlob(byte[] blob) {
        return gson.fromJson(new String(blob), SetInput.class);
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
    public void processMessage(NetworkedPhysicsClient physicsClient) {
        physicsClient.addRemoteManipulation(this);
    }
}
