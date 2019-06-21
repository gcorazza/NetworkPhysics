package NetworkedPhysics.Common.Protocol.serverCommands.Manipulations;

import NetworkedPhysics.Client.NetworkedPhysicsClient;
import NetworkedPhysics.Common.Dto.NetworkedPhysicsObjectDto;
import NetworkedPhysics.Common.NetworkPhysicsWorld;
import com.google.gson.Gson;

import static Util.Utils.gson;

public class AddRigidBody extends WorldManipulation {
    public static final byte COMMANDID=5;
    private final int id;
    private final NetworkedPhysicsObjectDto physicsObject;

    public AddRigidBody(int step, int id, NetworkedPhysicsObjectDto physicsObjectDto) {
        super(step);
        this.id = id;
        this.physicsObject = physicsObjectDto;
    }

    @Override
    public void manipulate(NetworkPhysicsWorld networkedPhysics) {
        networkedPhysics.addRigidBody(physicsObject, id);
    }

    @Override
    public AddRigidBody fromBlob(byte[] blob) {
        return new Gson().fromJson(new String(blob), AddRigidBody.class);
    }


    @Override
    public void processMessage(NetworkedPhysicsClient physicsClient) {
        physicsClient.addManipulation(this);
    }

    @Override
    public byte getCommandCode() {
        return COMMANDID;
    }

    @Override
    public byte[] getPacket() {
        return gson.toJson(this).getBytes();
    }
}
