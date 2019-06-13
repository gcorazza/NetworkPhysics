package NetworkedPhysics.Common;

import NetworkedPhysics.Common.Protocol.Dto.NetworkedPhysicsObjectDto;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;

import java.util.Map;

public class NetworkPhysicsListenerAdapter implements NetworkPhysicsListener{
    @Override
    public void newObject(NetworkedPhysicsObject networkedPhysicsObject) {

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
    public void stepInput(DiscreteDynamicsWorld world, Map<Integer, NetworkedPhysicsObject> objects, PhysicsInput in) {

    }
}
