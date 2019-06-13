package NetworkedPhysics.Common.Protocol.Manipulations;

import NetworkedPhysics.Common.NetworkedPhysics;
import NetworkedPhysics.Common.Protocol.Dto.NetworkedPhysicsObjectDto;
import NetworkedPhysics.Network.UdpConnection;
import com.google.gson.Gson;

public class AddRigidBody extends WorldManipulation {

    private final NetworkedPhysicsObjectDto physicsObject;

    public AddRigidBody(int frame, NetworkedPhysicsObjectDto rigidBody, int stamp) {
        super(frame, stamp);
        this.physicsObject = rigidBody;
    }

    @Override
    public void manipulate(NetworkedPhysics networkedPhysics) {
        networkedPhysics.addRigidBody(physicsObject);
    }

    @Override
    public byte[] toBlob() {
        return new Gson().toJson(this).getBytes();
    }

    @Override
    public AddRigidBody fromBlob(byte[] blob) {
        return new Gson().fromJson(new String(blob), AddRigidBody.class);
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
