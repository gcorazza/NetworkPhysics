package game;

import NetworkedPhysics.Common.Protocol.Dto.NetworkedPhysicsObject;
import NetworkedPhysics.Common.NetworkPhysicsListener;
import NetworkedPhysics.Server.NetworkedPhysicsServer;
import Rendering.PhysicsWorldRenderer;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;

import java.util.Map;

public class GameServer {

    private PhysicsWorldRenderer physicsWorldRenderer;
    private final NetworkedPhysicsServer networkedPhysicsServer;

    public GameServer(int port) throws Exception {
        networkedPhysicsServer = new NetworkedPhysicsServer(port, new NetworkPhysicsListener() {
            @Override
            public void updateInputs(DiscreteDynamicsWorld world, Map<Integer, NetworkedPhysicsObject> objects) {

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
