package game;

import NetworkedPhysics.Common.Dto.NetworkedPhysicsObjectDto;
import NetworkedPhysics.Common.Dto.Shape;
import NetworkedPhysics.Common.NetworkPhysicsListenerAdapter;
import NetworkedPhysics.Common.ObjectState;
import NetworkedPhysics.Common.PhysicsInput;
import NetworkedPhysics.Common.Protocol.clientCommands.InputArguments;
import NetworkedPhysics.Common.Protocol.serverCommands.WorldState;
import NetworkedPhysics.NetworkedPhysicsServer;
import Rendering.PhysicsWorldRenderer;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.util.*;

public class GameServer {

    private PhysicsWorldRenderer renderer = new PhysicsWorldRenderer();
    private final NetworkedPhysicsServer physicsServer;
    private Map<Integer, Integer> inputMapping = new HashMap<>();

    public GameServer(int port) {
        physicsServer = new NetworkedPhysicsServer(port, new NetworkPhysicsListenerAdapter() {
            @Override
            public void newObject(int physicsObjectId) {
                renderer.newObject(physicsServer.getRewindableWorld().getObject(physicsObjectId));
            }

            @Override
            public void newClient(int clientID) {
                int playerCubeId = physicsServer.addNetworkedPhysicsObjectNow(playerCube());
                int inId = physicsServer.addInputNow(new PhysicsInput(playerCubeId));
                inputMapping.put(clientID, inId);
            }

            @Override
            public void gotClientInput(InputArguments clientInput, int clientID) {
                Integer inputId = inputMapping.get(clientID);
                physicsServer.setInputNow(clientInput, inputId);
                if (clientInput.spawnClick) {
                    clientInput.spawnClick = false;
                    ObjectState objectState = new ObjectState();
                    physicsServer.addNetworkedPhysicsObjectNow(new NetworkedPhysicsObjectDto(Shape.CUBE, 1, 1, 1, objectState));
                }
            }

            @Override
            public void rewinded() {
                renderer.syncObjects(physicsServer.getRewindableWorld());
            }
        });
        Vector3f linearVelocity = new Vector3f(-1, 0, -1);
        Vector3f angularVelocity = new Vector3f(1, 5, 1);
        Vector4f rotation = new Vector4f(0, 0, 0, 10);
        Vector4f leftWall = new Vector4f(0, 0, -1, 2);
        Vector4f rightWall = new Vector4f(0, 0, 1, 2);
        Vector4f frontWall = new Vector4f(-1, 0, 0, 2);

        ObjectState objectStateSphere = new ObjectState(new Vector3f(0, 0, 0), rotation, angularVelocity, linearVelocity);
        ObjectState planeFloor = new ObjectState(new Vector3f(0, -4, 0), rotation, new Vector3f(), new Vector3f());
        ObjectState planeLeft = new ObjectState(new Vector3f(-20, 0, 0), leftWall, new Vector3f(), new Vector3f());
        ObjectState planeRight = new ObjectState(new Vector3f(20, 0, 0), rightWall, new Vector3f(), new Vector3f());
        ObjectState planeFront = new ObjectState(new Vector3f(0, 0, 20), frontWall, new Vector3f(), new Vector3f());
        ObjectState objectStateCube = new ObjectState(new Vector3f(0, 3, 0), rotation, angularVelocity, linearVelocity);

        NetworkedPhysicsObjectDto sphere = new NetworkedPhysicsObjectDto(Shape.SPHERE, 0.5f, 1, 1, 1, 1.5f, 0f, objectStateSphere);
        NetworkedPhysicsObjectDto cube = new NetworkedPhysicsObjectDto(Shape.CUBE, 0.5f, 1, 1, 2, 1.5f, 0f, objectStateCube);
        NetworkedPhysicsObjectDto floor = new NetworkedPhysicsObjectDto(Shape.PLANE, 0, 1, 0, 0, 0.5f, 0.5f, planeFloor);
        NetworkedPhysicsObjectDto left = new NetworkedPhysicsObjectDto(Shape.PLANE, 0, 1, 0, 0, 0.5f, 0.5f, planeLeft);
        NetworkedPhysicsObjectDto front = new NetworkedPhysicsObjectDto(Shape.PLANE, 0, 1, 0, 0, 0.5f, 0.5f, planeFront);
        NetworkedPhysicsObjectDto right = new NetworkedPhysicsObjectDto(Shape.PLANE, 0, 1, 0, 0, 0.5f, 0.5f, planeRight);


        //physicsServer.addNetworkedPhysicsObjectNow(sphere);
        //physicsServer.addNetworkedPhysicsObjectNow(cube);
        physicsServer.addNetworkedPhysicsObjectNow(floor);
        physicsServer.addNetworkedPhysicsObjectNow(left);
        physicsServer.addNetworkedPhysicsObjectNow(front);
        physicsServer.addNetworkedPhysicsObjectNow(right);

//        for (float i = 5; i < 100; i++) {
//            physicsServer.addNetworkedPhysicsObjectNow(playerCube(0, i / 1.5f, 0));
//        }
        physicsServer.getWorldStateNewClient().getPacket();
        physicsServer.addNetworkedPhysicsObjectNow(playerCube(0.000001f, 1, 0));
    }

    public static void main(String[] args) {
        new GameServer(8080).run();
    }

    public void run() {
        Timer randomCubeTimer = new Timer("cube Timer");
        /*        randomCubeTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Vector3f linearVelocity = new Vector3f(-1, 0, -1);
                Vector3f angularVelocity = new Vector3f(1, 5, 1);
                Vector4f rotation = new Vector4f(0, 0, 0, 10);
                ObjectState objectStateCube = new ObjectState(new Vector3f(0, 3, 0), rotation, angularVelocity, linearVelocity);
                NetworkedPhysicsObjectDto cube = new NetworkedPhysicsObjectDto(Shape.CUBE, 0.5f, 1, 1, 2, 1.5f, 0f, objectStateCube);
                physicsServer.addNetworkedPhysicsObjectNow(cube);
            }
        },2000,20000);*/

//&& physicsServer.getStep() < 1600
        while (!renderer.shouldClose() ) {
            physicsServer.update();
            renderer.update();
        }
        randomCubeTimer.cancel();
        renderer.free();
        physicsServer.shutDown();
    }

    private NetworkedPhysicsObjectDto playerCube() {
        int x = new Random().nextInt(10) - 5;
        int z = new Random().nextInt(10) - 5;
        return playerCube(x, 3, z);
    }

    private NetworkedPhysicsObjectDto playerCube(float x, float y, float z) {
        Vector4f rotation = new Vector4f(0, 0, 0, 10);
        ObjectState objectStateCube = new ObjectState(new Vector3f(x, y, z), rotation, new Vector3f(), new Vector3f());
        return new NetworkedPhysicsObjectDto(Shape.CUBE, 0.3f, 0.3f, 0.3f, 2, 1.5f, 0f, objectStateCube);
    }

}
