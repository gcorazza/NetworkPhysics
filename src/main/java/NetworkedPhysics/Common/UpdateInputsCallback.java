package NetworkedPhysics.Common;

import NetworkedPhysics.Network.Client;
import NetworkedPhysics.Network.Messages.UdpClient;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

public interface UpdateInputsCallback {
    public void updateInputs(DiscreteDynamicsWorld world, List<NetworkedPhysicsObject> objects,  Map<InetSocketAddress, UdpClient> clients);
}
