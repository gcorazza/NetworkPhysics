package NetworkedPhysics.Cereal;

import NetworkedPhysics.Common.PhysicsInput;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static NetworkedPhysics.Cereal.InputArgumentsCereal.inputArgumentsCereal;

public class PhysicsInputCereal implements Cereal<PhysicsInput> {
    public static final PhysicsInputCereal physicsInputCereal = new PhysicsInputCereal();

    private PhysicsInputCereal() {
    }

    @Override
    public DataOutputStream put(PhysicsInput physicsInput, DataOutputStream out) throws IOException {
        out.writeInt(physicsInput.getObjId());
        inputArgumentsCereal.put(physicsInput.getInputArguments(), out);
        return out;
    }

    @Override
    public PhysicsInput get(DataInputStream in) throws IOException {
        PhysicsInput physicsInput = new PhysicsInput(in.readInt());
        physicsInput.setInputArguments(inputArgumentsCereal.get(in));
        return physicsInput;
    }
}
