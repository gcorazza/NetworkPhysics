package NetworkedPhysics.Common;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class ObjectState {
    private Vector3f origin;
    private Quat4f rotation;
    private Vector3f angularVelocity;
    private Vector3f linearVelocity;

    public ObjectState() {
        origin= new Vector3f();
        rotation= new Quat4f(0,0,0,1);
        angularVelocity= new Vector3f();
        linearVelocity= new Vector3f();
    }

    public ObjectState(Vector3f origin, Quat4f rotation, Vector3f angularVelocity, Vector3f linearVelocity) {
        this.origin = origin;
        this.rotation = rotation;
        this.angularVelocity = angularVelocity;
        this.linearVelocity = linearVelocity;
    }

    public Vector3f getOrigin() {
        return origin;
    }

    public Quat4f getRotation() {
        return rotation;
    }

    public Vector3f getAngularVelocity() {
        return angularVelocity;
    }

    public Vector3f getLinearVelocity() {
        return linearVelocity;
    }
}
