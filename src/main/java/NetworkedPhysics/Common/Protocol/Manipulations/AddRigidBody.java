package NetworkedPhysics.Common.Protocol.Manipulations;

import NetworkedPhysics.Common.NetworkPhysicsWorld;
import NetworkedPhysics.Common.RewindablePhysicsWorld;
import NetworkedPhysics.Common.Dto.NetworkedPhysicsObjectDto;
import NetworkedPhysics.Common.Util;
import NetworkedPhysics.Network.UdpConnection;
import com.google.gson.Gson;

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
    public byte[] toBlob() {
        return Util.gson.toJson(this).getBytes();
    }

    @Override
    public AddRigidBody fromBlob(byte[] blob) {
        return new Gson().fromJson(new String(blob), AddRigidBody.class);
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
