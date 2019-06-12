package NetworkedPhysics.Common;

import NetworkedPhysics.Common.Protocol.Dto.NetworkedPhysicsObject;
import NetworkedPhysics.Network.UdpConnection;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

public interface UpdateInputsCallback {
    public void updateInputs(DiscreteDynamicsWorld world, List<NetworkedPhysicsObject> objects, Map<InetSocketAddress, UdpConnection> clients);
}
