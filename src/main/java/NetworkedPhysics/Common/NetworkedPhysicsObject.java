package NetworkedPhysics.Common;

import NetworkedPhysics.Common.Protocol.Dto.NetworkedPhysicsObjectDto;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class NetworkedPhysicsObject {
    NetworkedPhysicsObjectDto dto;
    RigidBody body;
    public final int id;

    public NetworkedPhysicsObject(NetworkedPhysicsObjectDto dto) {
        this.dto = dto;
        body= dto.getRigidBody();
        id= dto.id;
    }

    public RigidBody getBody() {
        return body;
    }

    public NetworkedPhysicsObjectDto getDto() {
        return dto;
    }

    public NetworkedPhysicsObjectDto bodyToDto(){
        Transform worldTransform = body.getMotionState().getWorldTransform(new Transform());
        Vector3f angularVelocity = body.getAngularVelocity(new Vector3f());
        Vector3f linearVelocity = body.getLinearVelocity(new Vector3f());
        ObjectState objectState = new ObjectState(worldTransform.origin, worldTransform.getRotation(new Quat4f()), angularVelocity, linearVelocity);
        NetworkedPhysicsObjectDto networkedPhysicsObjectDto = new NetworkedPhysicsObjectDto(id, dto.shape, dto.a, dto.b, dto.c, dto.mass, dto.friction,
                dto.restitution, objectState);
        return networkedPhysicsObjectDto;
    }
}
