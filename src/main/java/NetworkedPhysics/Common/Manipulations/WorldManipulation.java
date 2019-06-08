package NetworkedPhysics.Common.Manipulations;

import NetworkedPhysics.Common.NetworkedPhysics;

public abstract class WorldManipulation {
    final int frame;

    protected WorldManipulation(int frame) {
        this.frame = frame;
    }

    public abstract void manipulate(NetworkedPhysics networkedPhysics);
}
