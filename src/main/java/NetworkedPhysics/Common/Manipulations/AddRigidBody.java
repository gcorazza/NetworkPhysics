package NetworkedPhysics.Common.Manipulations;

import NetworkedPhysics.Common.NetworkedPhysics;
import com.bulletphysics.dynamics.RigidBody;

public class AddRigidBody extends WorldManipulation {

    private final RigidBody rigidBody;

    public AddRigidBody(int frame, RigidBody rigidBody) {
        super(frame);
        this.rigidBody = rigidBody;
    }

    @Override
    public void manipulate(NetworkedPhysics networkedPhysics) {
        networkedPhysics.addRigidBody(rigidBody);
    }

}
