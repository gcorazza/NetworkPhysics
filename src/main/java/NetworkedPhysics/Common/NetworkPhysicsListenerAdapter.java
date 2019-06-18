package NetworkedPhysics.Common;

import com.bulletphysics.dynamics.DiscreteDynamicsWorld;

import java.util.Map;

public class NetworkPhysicsListenerAdapter implements NetworkPhysicsListener{
    @Override
    public void newObject(int physicsObject) {

    }

    @Override
    public void deleteObject(int id) {

    }

    @Override
    public void newInput(PhysicsInput input) {

    }

    @Override
    public void deleteInput(int id) {

    }

    @Override
    public void stepInput(DiscreteDynamicsWorld world, Map<Integer, PhysicsObject> objects, PhysicsInput in) {

    }

    @Override
    public void rewinded() {

    }

}
