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
