package game;

import NetworkedPhysics.Client.NetworkedPhysicsClient;
import NetworkedPhysics.Common.NetworkedPhysicsObject;
import NetworkedPhysics.Common.UpdateInputsCallback;
import NetworkedPhysics.Network.Messages.UdpClient;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

public class GameClient {

    private final NetworkedPhysicsClient networkedPhysicsClient;

    public GameClient(InetSocketAddress socketAddress) {
        networkedPhysicsClient = new NetworkedPhysicsClient(socketAddress, new UpdateInputsCallback() {
            @Override
            public void updateInputs(DiscreteDynamicsWorld world, List<NetworkedPhysicsObject> objects, Map<InetSocketAddress, UdpClient> clients) {

            }
        });
    }

    public static void main(String[] args) {
        new GameClient(new InetSocketAddress("localhost",8080)).run();
    }

    private void run() {
        networkedPhysicsClient.run();
    }
}
