package game;

import NetworkedPhysics.NetworkedPhysicsClient;
import NetworkedPhysics.Common.NetworkPhysicsListenerAdapter;
import Rendering.PhysicsWorldRenderer;

import java.net.InetSocketAddress;

public class GameClient {

    public static void main(String[] args) throws Exception {
        final PhysicsWorldRenderer physicsWorldRenderer = new PhysicsWorldRenderer();
        InetSocketAddress localhost = new InetSocketAddress("localhost", 8080);
        NetworkedPhysicsClient networkedPhysicsClient = new NetworkedPhysicsClient(localhost, new NetworkPhysicsListenerAdapter() {
            @Override
            public void newObject(int physicsObject) {
                physicsWorldRenderer.newObject(physicsObject);
            }

            @Override
            public void rewinded() {
                physicsWorldRenderer.syncObjects();
            }
        });
        physicsWorldRenderer.setNetworkedPhysics(networkedPhysicsClient);

        physicsWorldRenderer.run();
    }
}
