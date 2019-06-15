package NetworkedPhysics.Common.Protocol.Dto;

import NetworkedPhysics.Common.NetworkedPhysics;
import NetworkedPhysics.Common.ObjectState;
import NetworkedPhysics.Common.Protocol.Shape;
import NetworkedPhysics.Network.UdpConnection;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class NetworkedPhysicsObjectDto {
    public final int id;
    public final Shape shape;
    public final float a, b, c;
    public final float mass; //if mass = 0 -> is static
    public final float friction;
    public final float restitution;
    public ObjectState objectState;

    public NetworkedPhysicsObjectDto(int id, Shape shape, float a, float b, float c, float mass, float friction, float restitution, ObjectState objectState) {
        this.id = id;
        this.shape = shape;
        this.a = a;
        this.b = b;
        this.c = c;
        this.mass = mass;
        this.friction = friction;
        this.restitution = restitution;
        this.objectState = objectState;
    }

    public RigidBody getRigidBody() {
        CollisionShape shape;
        switch (this.shape) {
            case PLANE: {
                shape = new StaticPlaneShape(new Vector3f(a, b, c), 0.25f /* m */);
                break;
            }
            case CUBE: {
                shape = new BoxShape(new Vector3f(a, b, c));
                break;
            }
            case SPHERE: {
                shape = new SphereShape(a);
                break;
            }
            default:
                return null;
        }

        Transform transform = new Transform();
        transform.setIdentity();
        transform.origin.set(objectState.getOrigin());
        transform.setRotation(objectState.getRotation());
        MotionState groundMotionState = new DefaultMotionState(transform);
        Vector3f localInertia = new Vector3f(0, 0, 0);
        shape.calculateLocalInertia(mass, localInertia);
        RigidBodyConstructionInfo bodyConstructionInfo = new RigidBodyConstructionInfo(mass, groundMotionState, shape, localInertia);
        RigidBody rigidBody = new RigidBody(bodyConstructionInfo);
        rigidBody.setRestitution(restitution);
        rigidBody.setFriction(friction);
        rigidBody.setDamping(0,0);

        return rigidBody;
    }

    public int getId() {
        return id;
    }
}
