package NetworkedPhysics.Common;

import NetworkedPhysics.Common.Dto.NetworkedPhysicsObjectDto;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;
import org.apache.commons.lang3.SerializationUtils;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.io.Serializable;

import static NetworkedPhysics.Util.Utils.gsonPretty;


public class PhysicsObject implements Serializable {

    private final NetworkedPhysicsObjectDto dto;
    private transient RigidBody body;
    public final int id;

    public PhysicsObject(NetworkedPhysicsObjectDto dto, int id) {
        this.dto = dto;
        this.id = id;
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

    public double diff(PhysicsObject phyObj) {
        double diff=0;

        Vector3f origin0 = dto.objectState.getOrigin();
        Vector3f origin1 = phyObj.dto.objectState.getOrigin();
        origin0.sub(origin1);
        diff+=origin0.length();

        return diff;
    }
}
