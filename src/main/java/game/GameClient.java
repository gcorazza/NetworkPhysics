package game;

import NetworkedPhysics.NetworkedPhysicsClient;
import NetworkedPhysics.Common.NetworkPhysicsListenerAdapter;
import Rendering.PhysicsWorldRenderer;

import java.net.InetSocketAddress;

public class GameClient {

    public static void main(String[] args) throws Exception {
        final PhysicsWorldRenderer renderer = new PhysicsWorldRenderer();
        InetSocketAddress localhost = new InetSocketAddress("localhost", 8080);
        NetworkedPhysicsClient physicsClient = new NetworkedPhysicsClient(localhost, new NetworkPhysicsListenerAdapter() {
            @Override
            public void newObject(int physicsObject) {
                renderer.newObject(physicsObject);
            }

            @Override
            public void rewinded() {
                renderer.syncObjects();
            }
        });

        renderer.setNetworkedPhysics(physicsClient.getRewindableWorld());
        renderer.setGame(physicsClient);
        while (!renderer.shouldClose()){
            physicsClient.update();
            renderer.update();
        }
        renderer.free();
        physicsClient.close();

    }
}
