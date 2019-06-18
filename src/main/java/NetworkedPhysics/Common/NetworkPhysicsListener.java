package NetworkedPhysics.Common;

import com.bulletphysics.dynamics.DiscreteDynamicsWorld;

import java.util.Map;

public interface NetworkPhysicsListener {
    public void newObject(int physicsObject);
    public void deleteObject(int id);
    public void newInput(PhysicsInput input);
    public void deleteInput(int id);
    public void stepInput(DiscreteDynamicsWorld world, Map<Integer, PhysicsObject> objects, PhysicsInput in);
    public void rewinded();
}
