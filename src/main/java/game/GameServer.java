package game;

import NetworkedPhysics.Common.NetworkPhysicsListenerAdapter;
import NetworkedPhysics.Common.NetworkedPhysicsObject;
import NetworkedPhysics.Common.Protocol.Dto.NetworkedPhysicsObjectDto;
import NetworkedPhysics.Server.NetworkedPhysicsServer;
import Rendering.PhysicsWorldRenderer;

public class GameServer {

    private PhysicsWorldRenderer physicsWorldRenderer;
    private final NetworkedPhysicsServer networkedPhysicsServer;

    public GameServer(int port) throws Exception {
        networkedPhysicsServer = new NetworkedPhysicsServer(port, new NetworkPhysicsListenerAdapter() {
            @Override
            public void newObject(NetworkedPhysicsObject networkedPhysicsObject) {
                physicsWorldRenderer.newObject(networkedPhysicsObject);
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
