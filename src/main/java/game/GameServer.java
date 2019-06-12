package game;

import NetworkedPhysics.Common.Protocol.Dto.NetworkedPhysicsObject;
import NetworkedPhysics.Common.UpdateInputsCallback;
import NetworkedPhysics.Network.UdpConnection;
import NetworkedPhysics.Server.NetworkedPhysicsServer;
import Rendering.PhysicsWorldRenderer;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

public class GameServer {

    private PhysicsWorldRenderer physicsWorldRenderer;
    private final NetworkedPhysicsServer networkedPhysicsServer;

    public GameServer(int port) throws Exception {
        networkedPhysicsServer = new NetworkedPhysicsServer(port, new UpdateInputsCallback() {
            @Override
            public void updateInputs(DiscreteDynamicsWorld world, List<NetworkedPhysicsObject> objects, Map<InetSocketAddress, UdpConnection> clients) {

            }
        });
        physicsWorldRenderer = new PhysicsWorldRenderer(networkedPhysicsServer);

    }

    public static void main(String[] args) throws Exception {
        new GameServer(8080).run();
    }

    private void run() {
        physicsWorldRenderer.run();
    }

}
