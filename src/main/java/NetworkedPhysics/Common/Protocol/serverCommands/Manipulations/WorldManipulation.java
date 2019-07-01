package NetworkedPhysics.Common.Protocol.serverCommands.Manipulations;

import NetworkedPhysics.Common.NetworkPhysicsWorld;
import NetworkedPhysics.Common.Protocol.ServerCommand;

public abstract class WorldManipulation implements ServerCommand {

    public int step;

    public WorldManipulation() {
    }

    protected WorldManipulation(int frame) {
        this.step = frame;
    }

    public abstract void manipulate(NetworkPhysicsWorld networkedPhysics);
}
