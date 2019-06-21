package NetworkedPhysics.Common.Protocol;

import NetworkedPhysics.Server.NetworkedPhysicsServer;

public interface UserCommand extends PhysicsMessage{
    void processMessage(NetworkedPhysicsServer physicsServer, int from);
}
