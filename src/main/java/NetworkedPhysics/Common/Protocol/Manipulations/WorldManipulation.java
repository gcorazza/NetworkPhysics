package NetworkedPhysics.Common.Protocol.Manipulations;

import NetworkedPhysics.Common.NetworkedPhysics;
import NetworkedPhysics.Common.Protocol.PhysicsMessage;

public abstract class WorldManipulation extends PhysicsMessage {
    public final int frame;

    protected WorldManipulation(int frame, int stamp) {
        super(stamp);
        this.frame = frame;
    }

    public abstract void manipulate(NetworkedPhysics networkedPhysics);
}
