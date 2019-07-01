package NetworkedPhysics.Common.Protocol;

import NetworkedPhysics.NetworkedPhysicsServer;

public interface UserCommand extends PhysicsMessage{
    void processMessage(NetworkedPhysicsServer physicsServer, int from);
}
