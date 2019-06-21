package NetworkedPhysics.Common.Protocol;

import NetworkedPhysics.Client.NetworkedPhysicsClient;
import NetworkedPhysics.Common.RewindablePhysicsWorld;
import NetworkedPhysics.Server.NetworkedPhysicsServer;

public interface ServerCommand extends PhysicsMessage {
    void processMessage(NetworkedPhysicsClient physicsClient);
}
