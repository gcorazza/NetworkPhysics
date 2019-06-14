package NetworkedPhysics.Common.Protocol.Manipulations;

import NetworkedPhysics.Common.NetworkPhysicsWorld;
import NetworkedPhysics.Common.Protocol.PhysicsMessage;

public abstract class WorldManipulation extends PhysicsMessage {
    public final int frame;

    public WorldManipulation(int frame) {
        this.frame = frame;
    }

    protected WorldManipulation(int frame, int stamp) {
        super(stamp);
        this.frame = frame;
    }

    public abstract void manipulate(NetworkPhysicsWorld networkedPhysics);
}
