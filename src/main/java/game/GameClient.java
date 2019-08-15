package game;

import NetworkedPhysics.NetworkedPhysicsClient;
import NetworkedPhysics.Common.NetworkPhysicsListenerAdapter;
import Rendering.PhysicsWorldRenderer;

import java.net.InetSocketAddress;

public class GameClient {

    final PhysicsWorldRenderer renderer;
    private final NetworkedPhysicsClient physicsClient;

    public static void main(String[] args) {
        InetSocketAddress localhost = new InetSocketAddress("192.168.0.236", 8080);
        new GameClient(localhost).run();
    }

    public GameClient(InetSocketAddress socketAddress) {
        physicsClient = new NetworkedPhysicsClient(socketAddress, new NetworkPhysicsListenerAdapter() {
            @Override
            public void newObject(int physicsObject) {
                renderer.newObject(physicsClient.getRewindableWorld().getObject(physicsObject));
            }

            @Override
            public void rewinded() {
                renderer.syncObjects(physicsClient.getRewindableWorld());
            }
        });
        renderer = new PhysicsWorldRenderer(physicsClient);
    }

    private void run() {
        while (!renderer.shouldClose()){
            physicsClient.update();
            renderer.update();
        }
        renderer.free();
        physicsClient.close();
    }
}
