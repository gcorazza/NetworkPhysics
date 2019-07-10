package NetworkedPhysics.Common;

import NetworkedPhysics.Common.Dto.NetworkedPhysicsObjectDto;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;
import org.apache.commons.lang3.SerializationUtils;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.io.Serializable;

import static Util.Utils.gsonPretty;


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
        Vector3f linearVelocity0 = body.getLinearVelocity(new Vector3f());
        Vector3f linearVelocity1 = phyObj.body.getLinearVelocity(new Vector3f());
        linearVelocity0.sub(linearVelocity1);
        diff+=linearVelocity0.length();

        Vector3f angularVelocity0 = body.getAngularVelocity(new Vector3f());
        Vector3f angularVelocity1 = phyObj.body.getAngularVelocity(new Vector3f());
        angularVelocity0.sub(angularVelocity1);
        diff+=angularVelocity0.length();

        return diff;
    }
}
