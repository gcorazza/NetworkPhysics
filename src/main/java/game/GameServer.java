package game;

import NetworkedPhysics.Common.NetworkPhysicsListenerAdapter;
import NetworkedPhysics.Common.ObjectState;
import NetworkedPhysics.Common.PhysicsObject;
import NetworkedPhysics.Common.Protocol.Dto.NetworkedPhysicsObjectDto;
import NetworkedPhysics.Common.Protocol.Shape;
import NetworkedPhysics.Server.NetworkedPhysicsServer;
import Rendering.PhysicsWorldRenderer;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class GameServer {

    private PhysicsWorldRenderer physicsWorldRenderer;
    private final NetworkedPhysicsServer networkedPhysicsServer;

    public GameServer(int port) throws Exception {
        networkedPhysicsServer = new NetworkedPhysicsServer(port, new NetworkPhysicsListenerAdapter() {
            @Override
            public void newObject(int physicsObjectId) {
                PhysicsObject physicsObject= networkedPhysicsServer.getObject(physicsObjectId);
                physicsWorldRenderer.newObject(physicsObject);
                if (physicsObject.getBody().isStaticObject())
                    return;
                physicsObject.getBody().setLinearVelocity(new Vector3f(-1,0,-1));
                physicsObject.getBody().setAngularVelocity(new Vector3f(1,5,1));
            }
        });
        Quat4f rotation = new Quat4f(0, 0, 0, 10);
        ObjectState objectStateCube = new ObjectState(new Vector3f(0, 3, 0), rotation, new Vector3f(), new Vector3f());
        ObjectState objectStateSphere = new ObjectState(new Vector3f(0, 0, 0), rotation, new Vector3f(), new Vector3f());
        ObjectState objectStatePlane = new ObjectState(new Vector3f(0, -4, 0), rotation, new Vector3f(), new Vector3f());

        NetworkedPhysicsObjectDto cube= new NetworkedPhysicsObjectDto(0, Shape.SPHERE, 0.5f,1,1,1,1.5f,0f, objectStateSphere);
        NetworkedPhysicsObjectDto cube2= new NetworkedPhysicsObjectDto(0, Shape.CUBE, 0.5f,1,1,2,1.5f,0f, objectStateCube);
        NetworkedPhysicsObjectDto plane = new NetworkedPhysicsObjectDto(1, Shape.PLANE, 0, 1, 0, 0, 0.5f, 0.5f, objectStatePlane);

        networkedPhysicsServer.addNetworkedPhysicsObjectNow(cube);
        networkedPhysicsServer.addNetworkedPhysicsObjectNow(cube2);
        networkedPhysicsServer.addNetworkedPhysicsObjectNow(plane);
        physicsWorldRenderer = new PhysicsWorldRenderer(networkedPhysicsServer);
    }

    public static void main(String[] args) throws Exception {
        new GameServer(8080).run();
    }

    private void run() {
        physicsWorldRenderer.run();
        networkedPhysicsServer.shutDown();
    }

}
