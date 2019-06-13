package game;

import NetworkedPhysics.Common.NetworkPhysicsListenerAdapter;
import NetworkedPhysics.Common.NetworkedPhysicsObject;
import NetworkedPhysics.Common.Protocol.Dto.NetworkedPhysicsObjectDto;
import NetworkedPhysics.Common.Protocol.Shape;
import NetworkedPhysics.Server.NetworkedPhysicsServer;
import Rendering.PhysicsWorldEntity;
import Rendering.PhysicsWorldRenderer;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

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
        Quat4f rotation = new Quat4f(0, 0, 0, 1);
        NetworkedPhysicsObjectDto physicsObjectDto= new NetworkedPhysicsObjectDto(0, Shape.CUBE, 1,1,1,1,0,0,
                new Vector3f(0,0,0), rotation);
        NetworkedPhysicsObjectDto plane = new NetworkedPhysicsObjectDto(1, Shape.PLANE, 0, 1, 0, 0, 1, 0.1f,
                new Vector3f(0, -5, 0), rotation);

        networkedPhysicsServer.addNetworkedPhysicsObject(physicsObjectDto);
        networkedPhysicsServer.addNetworkedPhysicsObject(plane);
        physicsWorldRenderer = new PhysicsWorldRenderer(networkedPhysicsServer);
    }

    public static void main(String[] args) throws Exception {
        new GameServer(8080).run();
    }

    private void run() {
        physicsWorldRenderer.run();
    }

}
