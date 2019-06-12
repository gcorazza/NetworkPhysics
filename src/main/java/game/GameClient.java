package game;

import NetworkedPhysics.Client.NetworkedPhysicsClient;
import NetworkedPhysics.Common.Protocol.Dto.NetworkedPhysicsObject;
import NetworkedPhysics.Common.UpdateInputsCallback;
import NetworkedPhysics.Network.UdpConnection;
import Rendering.PhysicsWorldRenderer;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

public class GameClient {

    private final NetworkedPhysicsClient networkedPhysicsClient;
    private PhysicsWorldRenderer physicsWorldRenderer;

    public GameClient(InetSocketAddress socketAddress) throws Exception {
        networkedPhysicsClient = new NetworkedPhysicsClient(socketAddress, new UpdateInputsCallback() {
            @Override
            public void updateInputs(DiscreteDynamicsWorld world, List<NetworkedPhysicsObject> objects, Map<InetSocketAddress, UdpConnection> clients) {

            }
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
