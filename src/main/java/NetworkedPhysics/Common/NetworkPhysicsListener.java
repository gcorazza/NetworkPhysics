package NetworkedPhysics.Common;

import NetworkedPhysics.Common.Protocol.Dto.NetworkedPhysicsObjectDto;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;

import java.util.Map;

public interface NetworkPhysicsListener {
    public void newObject(NetworkedPhysicsObject networkedPhysicsObject);
    public void deleteObject(int id);
    public void newInput(PhysicsInput input);
    public void deleteInput(int id);

    public void stepInput(DiscreteDynamicsWorld world, Map<Integer, NetworkedPhysicsObject> objects, PhysicsInput in);
}
