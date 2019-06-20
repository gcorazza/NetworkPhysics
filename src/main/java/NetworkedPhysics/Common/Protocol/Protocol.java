package NetworkedPhysics.Common.Protocol;

import NetworkedPhysics.Common.Protocol.Manipulations.AddRigidBody;
import NetworkedPhysics.Common.Protocol.Manipulations.SetInput;

import java.util.HashMap;
import java.util.Map;

public class Protocol {
    public static Map<Byte, Class<? extends PhysicsMessage>> protocol;

    static {
        protocol = new HashMap<>();
        protocol.put(WorldState.COMMANDID, WorldState.class);
        protocol.put(GetWorldState.COMMANDID, GetWorldState.class);
        protocol.put(ClientInput.COMMANDID, ClientInput.class);
        protocol.put(SetInput.COMMANDID, SetInput.class);
        protocol.put(AddRigidBody.COMMANDID, AddRigidBody.class);
    }
}
