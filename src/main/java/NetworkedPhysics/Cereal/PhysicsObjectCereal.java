package NetworkedPhysics.Cereal;

import NetworkedPhysics.Common.PhysicsObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static NetworkedPhysics.Cereal.NetworkedPhysicsObjectDtoCereal.networkedPhysicsObjectDtoCereal;

public class PhysicsObjectCereal implements Cereal<PhysicsObject> {
    public static final PhysicsObjectCereal physicsObjectCereal = new PhysicsObjectCereal();

    @Override
    public DataOutputStream put(PhysicsObject physicsObject, DataOutputStream out) throws IOException {
        networkedPhysicsObjectDtoCereal.put(physicsObject.getDto(), out);
        out.writeInt(physicsObject.id);
        return out;
    }

    @Override
    public PhysicsObject get(DataInputStream in) throws IOException {
        return new PhysicsObject(networkedPhysicsObjectDtoCereal.get(in), in.readInt());
    }

}
