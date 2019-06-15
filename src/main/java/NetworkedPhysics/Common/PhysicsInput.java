package NetworkedPhysics.Common;

public class PhysicsInput {
    int id;
    boolean click;

    public PhysicsInput copy() {
        PhysicsInput physicsInput = new PhysicsInput();
        physicsInput.id=id;
        physicsInput.click=click;
        return physicsInput;
    }
}
