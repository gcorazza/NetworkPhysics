package NetworkedPhysics.Common.Protocol.serverCommands.Manipulations;

import NetworkedPhysics.NetworkedPhysicsClient;
import NetworkedPhysics.Common.Dto.NetworkedPhysicsObjectDto;
import NetworkedPhysics.Common.NetworkPhysicsWorld;

import static NetworkedPhysics.Util.Utils.gson;

public class AddRigidBody extends WorldManipulation {
    public static final byte COMMANDID=5;
    private int id;
    private NetworkedPhysicsObjectDto physicsObject;

    public AddRigidBody() {
    }

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
        return gson.fromJson(new String(blob), AddRigidBody.class);
    }

    @Override
    public byte[] getPacket() {
        return gson.toJson(this).getBytes();
    }


    @Override
    public void processMessage(NetworkedPhysicsClient physicsClient) {
        physicsClient.getRewindableWorld().addManipulation(this);
    }

    @Override
    public byte getCommandCode() {
        return COMMANDID;
    }
}
