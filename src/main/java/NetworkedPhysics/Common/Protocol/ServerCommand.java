package NetworkedPhysics.Common.Protocol;

import NetworkedPhysics.NetworkedPhysicsClient;

public interface ServerCommand extends PhysicsMessage {
    void processMessage(NetworkedPhysicsClient physicsClient);
}
