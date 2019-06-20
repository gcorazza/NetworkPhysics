package game;

import NetworkedPhysics.Client.NetworkedPhysicsClient;
import NetworkedPhysics.Common.NetworkPhysicsListenerAdapter;
import Rendering.PhysicsWorldRenderer;

import java.net.InetSocketAddress;

public class GameClient {

    private final NetworkedPhysicsClient networkedPhysicsClient;
    private PhysicsWorldRenderer physicsWorldRenderer;

    public GameClient(InetSocketAddress socketAddress) throws Exception {
        networkedPhysicsClient = new NetworkedPhysicsClient(socketAddress, new NetworkPhysicsListenerAdapter() {
            @Override
            public void newObject(int physicsObject) {
                physicsWorldRenderer.newObject(networkedPhysicsClient.getObject(physicsObject));
            }

            @Override
            public void rewinded() {
                physicsWorldRenderer.syncObjects();
            }
        });
        physicsWorldRenderer= new PhysicsWorldRenderer(networkedPhysicsClient);
    }

    public static void main(String[] args) throws Exception {
        new GameClient(new InetSocketAddress("localhost",8080)).run();
    }

    public void run() {
        physicsWorldRenderer.run();
    }
}
