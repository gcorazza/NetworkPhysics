package game;

import NetworkedPhysics.Client.NetworkedPhysicsClient;
import NetworkedPhysics.Common.Protocol.Dto.NetworkedPhysicsObject;
import NetworkedPhysics.Common.NetworkPhysicsListener;
import Rendering.PhysicsWorldRenderer;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;

import java.net.InetSocketAddress;
import java.util.Map;

public class GameClient {

    private final NetworkedPhysicsClient networkedPhysicsClient;
    private PhysicsWorldRenderer physicsWorldRenderer;

    public GameClient(InetSocketAddress socketAddress) throws Exception {
        networkedPhysicsClient = new NetworkedPhysicsClient(socketAddress, new NetworkPhysicsListener() {
            @Override
            public void updateInputs(DiscreteDynamicsWorld world, Map<Integer, NetworkedPhysicsObject> objects) {

            }
        });
        physicsWorldRenderer= new PhysicsWorldRenderer(networkedPhysicsClient);
    }

    public static void main(String[] args) throws Exception {
        new GameClient(new InetSocketAddress("localhost",8080)).run();
    }

    private void run() {
        physicsWorldRenderer.run();
    }
}
