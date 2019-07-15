package NetworkedPhysics.Cereal;

import NetworkedPhysics.Common.ObjectState;

import javax.vecmath.Quat4f;
import javax.vecmath.Tuple4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ObjectStateCereal implements Cereal<ObjectState> {
    public static final ObjectStateCereal objectStateCereal = new ObjectStateCereal();

    private ObjectStateCereal() {
    }

    @Override
    public DataOutputStream put(ObjectState objectState, DataOutputStream out) throws IOException {
        out.writeFloat(objectState.getOrigin().x);
        out.writeFloat(objectState.getOrigin().y);
        out.writeFloat(objectState.getOrigin().z);

        out.writeFloat(objectState.getRotation().x);
        out.writeFloat(objectState.getRotation().y);
        out.writeFloat(objectState.getRotation().z);
        out.writeFloat(objectState.getRotation().w);

        out.writeFloat(objectState.getAngularVelocity().x);
        out.writeFloat(objectState.getAngularVelocity().y);
        out.writeFloat(objectState.getAngularVelocity().z);

        out.writeFloat(objectState.getLinearVelocity().x);
        out.writeFloat(objectState.getLinearVelocity().y);
        out.writeFloat(objectState.getLinearVelocity().z);

        return out;
    }

    @Override
    public ObjectState get(DataInputStream in) throws IOException {
        return new ObjectState(
                new Vector3f(in.readFloat(), in.readFloat(), in.readFloat()),
                new Vector4f(in.readFloat(), in.readFloat(), in.readFloat(), in.readFloat()),
                new Vector3f(in.readFloat(), in.readFloat(), in.readFloat()),
                new Vector3f(in.readFloat(), in.readFloat(), in.readFloat())
        );
    }

}
