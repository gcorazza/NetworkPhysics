package game;

import NetworkedPhysics.Common.Dto.NetworkedPhysicsObjectDto;
import NetworkedPhysics.Common.Dto.Shape;
import NetworkedPhysics.Common.NetworkPhysicsListenerAdapter;
import NetworkedPhysics.Common.ObjectState;
import NetworkedPhysics.Common.PhysicsInput;
import NetworkedPhysics.Common.PhysicsObject;
import NetworkedPhysics.NetworkedPhysicsServer;
import Rendering.PhysicsWorldRenderer;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.util.*;

public class GameServer {

    private PhysicsWorldRenderer physicsWorldRenderer;
    private final NetworkedPhysicsServer networkedPhysicsServer;

    Map<Integer, Integer> inputMapping = new HashMap();

    public GameServer(int port) throws Exception {
        networkedPhysicsServer = new NetworkedPhysicsServer(port, new NetworkPhysicsListenerAdapter() {
            @Override
            public void newObject(int physicsObjectId) {
                PhysicsObject physicsObject = networkedPhysicsServer.getObject(physicsObjectId);
                physicsWorldRenderer.newObject(physicsObjectId);
                if (physicsObject.getBody().isStaticObject()) {
                    return;
                }
            }

            @Override
            public void newClient(int clientID) {
                int playerCubeId = networkedPhysicsServer.addNetworkedPhysicsObjectNow(playerCube());
                int inId = networkedPhysicsServer.addInputNow(new PhysicsInput(playerCubeId));
                inputMapping.put(clientID, inId);
            }

            @Override
            public void clientInput(PhysicsInput clientInput, int clientID) {
                int inputId = inputMapping.get(clientID);
                networkedPhysicsServer.setInput(clientInput, inputId);
            }

            @Override
            public void rewinded() {
                physicsWorldRenderer.syncObjects();
            }
        });
        Vector3f linearVelocity = new Vector3f(-1, 0, -1);
        Vector3f angularVelocity = new Vector3f(1, 5, 1);
        Quat4f rotation = new Quat4f(0, 0, 0, 10);
        ObjectState objectStateSphere = new ObjectState(new Vector3f(0, 0, 0), rotation, angularVelocity, linearVelocity);
        ObjectState objectStatePlane = new ObjectState(new Vector3f(0, -4, 0), rotation, new Vector3f(), new Vector3f());
        ObjectState objectStateCube = new ObjectState(new Vector3f(0, 3, 0), rotation, angularVelocity, linearVelocity);
        NetworkedPhysicsObjectDto sphere = new NetworkedPhysicsObjectDto(Shape.SPHERE, 0.5f, 1, 1, 1, 1.5f, 0f, objectStateSphere);
        NetworkedPhysicsObjectDto cube = new NetworkedPhysicsObjectDto(Shape.CUBE, 0.5f, 1, 1, 2, 1.5f, 0f, objectStateCube);
        NetworkedPhysicsObjectDto plane = new NetworkedPhysicsObjectDto(Shape.PLANE, 0, 1, 0, 0, 0.5f, 0.5f, objectStatePlane);

        networkedPhysicsServer.addNetworkedPhysicsObjectNow(sphere);
        networkedPhysicsServer.addNetworkedPhysicsObjectNow(cube);
        networkedPhysicsServer.addNetworkedPhysicsObjectNow(plane);
        physicsWorldRenderer = new PhysicsWorldRenderer(networkedPhysicsServer);
    }

    public static void main(String[] args) throws Exception {
        new GameServer(8080).run();
    }

    public void run() {
        Timer randomCubeTimer = new Timer();
//        randomCubeTimer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                Vector3f linearVelocity = new Vector3f(-1, 0, -1);
//                Vector3f angularVelocity = new Vector3f(1, 5, 1);
//                Quat4f rotation = new Quat4f(0, 0, 0, 10);
//                ObjectState objectStateCube = new ObjectState(new Vector3f(0, 3, 0), rotation, angularVelocity, linearVelocity);
//                NetworkedPhysicsObjectDto cube = new NetworkedPhysicsObjectDto(Shape.CUBE, 0.5f, 1, 1, 2, 1.5f, 0f, objectStateCube);
//                networkedPhysicsServer.addNetworkedPhysicsObjectNow(cube);
//            }
//        },2000,20000);
        physicsWorldRenderer.run();
        networkedPhysicsServer.shutDown();
    }

    private NetworkedPhysicsObjectDto playerCube() {
        Quat4f rotation = new Quat4f(0, 0, 0, 10);
        int x = new Random().nextInt(10) - 5;
        int z = new Random().nextInt(10) - 5;
        ObjectState objectStateCube = new ObjectState(new Vector3f(x, 3, z), rotation, new Vector3f(), new Vector3f());
        return new NetworkedPhysicsObjectDto(Shape.CUBE, 0.3f, 0.3f, 0.3f, 2, 1.5f, 0f, objectStateCube);
    }

}
