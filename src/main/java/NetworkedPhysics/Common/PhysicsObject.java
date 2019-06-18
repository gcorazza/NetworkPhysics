package NetworkedPhysics.Common;

import NetworkedPhysics.Common.Protocol.Dto.NetworkedPhysicsObjectDto;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;
import com.google.gson.Gson;
import org.apache.commons.lang3.SerializationUtils;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.io.Serializable;

import static NetworkedPhysics.Common.Util.gson;
import static NetworkedPhysics.Common.Util.gsonPretty;

public class PhysicsObject implements Serializable {

    private final NetworkedPhysicsObjectDto dto;
    private transient RigidBody body;
    public final int id;

    public PhysicsObject(NetworkedPhysicsObjectDto dto) {
        this.dto = dto;
        id = dto.id;
    }

    public RigidBody getBody() {
        if (body == null) {
            body= dto.getRigidBody();
        }
        return body;
    }

    public NetworkedPhysicsObjectDto getDto() {
        return dto;
    }

    public NetworkedPhysicsObjectDto bodyToDto() {
        Transform worldTransform = body.getWorldTransform(new Transform());
        Vector3f angularVelocity = body.getAngularVelocity(new Vector3f());
        Vector3f linearVelocity = body.getLinearVelocity(new Vector3f());
        ObjectState objectState = new ObjectState(worldTransform.origin, worldTransform.getRotation(new Quat4f()), angularVelocity, linearVelocity);
        NetworkedPhysicsObjectDto dtoCopy = SerializationUtils.clone(dto);
        dtoCopy.objectState=objectState;
        return dtoCopy;
    }

    public void print() {
        System.out.println(gsonPretty.toJson(bodyToDto()));
    }
}
