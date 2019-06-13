package NetworkedPhysics.Common.Protocol.Dto;

import NetworkedPhysics.Common.NetworkedPhysics;
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

    private Vector3f position;
    private Quat4f rotation;

    public NetworkedPhysicsObjectDto(int id, Shape shape, float a, float b, float c, float mass, float friction, float restitution, Vector3f position, Quat4f rotation) {
        this.id = id;
        this.shape = shape;
        this.a = a;
        this.b = b;
        this.c = c;
        this.mass = mass;
        this.friction = friction;
        this.restitution = restitution;
        this.position = position;
        this.rotation = rotation;
    }

    public RigidBody getRigidBody() {
        CollisionShape groundShape;
        switch (shape) {
            case PLANE: {
                groundShape = new StaticPlaneShape(new Vector3f(a, b, c), 0.25f /* m */);
                break;
            }
            case CUBE: {
                groundShape = new BoxShape(new Vector3f(a, b, c));
                break;
            }
            case SPHERE: {
                groundShape = new SphereShape(a);
                break;
            }
            default:
                return null;
        }

        Transform transform = new Transform();
        transform.setIdentity();
        transform.origin.set(position);
        transform.setRotation(rotation);
        MotionState groundMotionState = new DefaultMotionState(transform);
        RigidBodyConstructionInfo bodyConstructionInfo = new RigidBodyConstructionInfo(mass, groundMotionState, groundShape, new javax.vecmath.Vector3f(0, 0, 0));
        bodyConstructionInfo.restitution = restitution;
        bodyConstructionInfo.friction = friction;
        RigidBody rigidBody = new RigidBody(bodyConstructionInfo);
        return rigidBody;
    }

    public int getId() {
        return id;
    }
}
