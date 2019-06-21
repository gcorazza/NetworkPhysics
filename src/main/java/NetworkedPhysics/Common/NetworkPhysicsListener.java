package NetworkedPhysics.Common;

public interface NetworkPhysicsListener {
    public void newObject(int physicsObject);

    public void deleteObject(int id);

    public void newClient(int id);

    public void rewinded();

    void clientInput(PhysicsInput clientInput, int from);
}
