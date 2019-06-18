package NetworkedPhysics.Common.Protocol.Manipulations;

import NetworkedPhysics.Common.NetworkPhysicsWorld;
import NetworkedPhysics.Common.RewindablePhysicsWorld;
import NetworkedPhysics.Common.Protocol.Dto.NetworkedPhysicsObjectDto;
import NetworkedPhysics.Common.Util;
import NetworkedPhysics.Network.UdpConnection;
import com.google.gson.Gson;

public class AddRigidBody extends WorldManipulation {

    private final NetworkedPhysicsObjectDto physicsObject;

    public AddRigidBody(int frame, NetworkedPhysicsObjectDto physicsObjectDto) {
        super(frame);
        this.physicsObject = physicsObjectDto;
    }

    @Override
    public void manipulate(NetworkPhysicsWorld networkedPhysics) {
        networkedPhysics.addRigidBody(physicsObject);
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
        return 0;
    }
}
