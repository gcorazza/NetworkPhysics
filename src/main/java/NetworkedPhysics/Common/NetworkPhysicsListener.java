package NetworkedPhysics.Common;

import NetworkedPhysics.Common.Protocol.clientCommands.InputArguments;

public interface NetworkPhysicsListener {
    public void newObject(int physicsObject);

    public void deleteObject(int id);

    public void newClient(int id);

    public void rewinded();

    void clientInput(InputArguments clientInput, int from);
}
