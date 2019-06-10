package game;

import NetworkedPhysics.Common.NetworkedPhysicsObject;
import NetworkedPhysics.Network.UdpConnection;
import Rendering.PhysicsWorldRenderer;
import NetworkedPhysics.Common.UpdateInputsCallback;
import NetworkedPhysics.Server.NetworkedPhysicsServer;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

public class GameServer {

    private final PhysicsWorldRenderer physicsWorldRenderer;
    private final NetworkedPhysicsServer networkedPhysicsServer;
    private boolean running=true;

    public GameServer(int port) {
        networkedPhysicsServer = new NetworkedPhysicsServer(port, new UpdateInputsCallback() {
            @Override
            public void updateInputs(DiscreteDynamicsWorld world, List<NetworkedPhysicsObject> objects,  Map<InetSocketAddress, UdpConnection> clients) {

            }
        });

        physicsWorldRenderer = new PhysicsWorldRenderer(networkedPhysicsServer);
    }

    public static void main(String[] args) {
        new GameServer(8080).run();
    }

    private void run() {
        addCube();
        while(running){
            networkedPhysicsServer.update();
        }
    }

    private void addCube(){
        CollisionShape groundShape = new StaticPlaneShape(new Vector3f(0, 1, 0), 0.25f /* m */);
        MotionState groundMotionState = new DefaultMotionState(new Transform(new Matrix4f(
                new Quat4f(0, 0, 0, 1),
                new Vector3f(0, 0, 0), 1.0f)));
        RigidBodyConstructionInfo groundBodyConstructionInfo = new RigidBodyConstructionInfo(0, groundMotionState, groundShape, new Vector3f(0, 0, 0));
        groundBodyConstructionInfo.restitution = 0.25f;
        RigidBody groundRigidBody = new RigidBody(groundBodyConstructionInfo);
        networkedPhysicsServer.addRigidBody(groundRigidBody);
    }
}
