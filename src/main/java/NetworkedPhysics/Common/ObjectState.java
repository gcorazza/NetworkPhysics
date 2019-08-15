package NetworkedPhysics.Common;

import javax.vecmath.Quat4f;
import javax.vecmath.Tuple4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.io.Serializable;

public class ObjectState implements Serializable {
    private Vector3f origin;
    private Vector4f rotation; //Must be Tuple4f because QUad4f calculates xyzw from input arguments, which leads to incorret bits
    private Vector3f angularVelocity;
    private Vector3f linearVelocity;

    public ObjectState() {
        origin= new Vector3f();
        rotation= new Vector4f(0,0,0,1);
        angularVelocity= new Vector3f();
        linearVelocity= new Vector3f();
    }

    public ObjectState(Vector3f origin, Vector4f rotation, Vector3f angularVelocity, Vector3f linearVelocity) {
        this.origin = origin;
        this.rotation = rotation;
        this.angularVelocity = angularVelocity;
        this.linearVelocity = linearVelocity;
    }

    public Vector3f getOrigin() {
        return origin;
    }

    public Tuple4f getRotation() {
        return rotation;
    }

    public Vector3f getAngularVelocity() {
        return angularVelocity;
    }

    public Vector3f getLinearVelocity() {
        return linearVelocity;
    }
}
