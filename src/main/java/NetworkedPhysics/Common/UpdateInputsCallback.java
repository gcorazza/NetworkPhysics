package NetworkedPhysics.Common;

import NetworkedPhysics.Network.Client;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;

import java.util.List;

public interface UpdateInputsCallback {
    public void updateInputs(DiscreteDynamicsWorld world, List<NetworkedPhysicsObject> objects, List<Client> clients);
}
