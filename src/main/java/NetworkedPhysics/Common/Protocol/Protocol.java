package NetworkedPhysics.Common.Protocol;

import NetworkedPhysics.Common.Protocol.clientCommands.ClientInput;
import NetworkedPhysics.Common.Protocol.clientCommands.GetWorldState;
import NetworkedPhysics.Common.Protocol.serverCommands.Manipulations.AddRigidBody;
import NetworkedPhysics.Common.Protocol.serverCommands.Manipulations.SetInput;
import NetworkedPhysics.Common.Protocol.serverCommands.WorldState;

import java.util.HashMap;
import java.util.Map;

public class Protocol {
    public static Map<Byte, Class<? extends UserCommand>> userCommands;
    public static Map<Byte, Class<? extends ServerCommand>> serverCommands;

    static {
        userCommands = new HashMap<>();
        userCommands.put(GetWorldState.COMMANDID, GetWorldState.class);
        userCommands.put(ClientInput.COMMANDID,ClientInput.class);
    }

    static {
        serverCommands = new HashMap<>();
        serverCommands.put(WorldState.COMMANDID, WorldState.class);
        serverCommands.put(SetInput.COMMANDID, SetInput.class);
        serverCommands.put(AddRigidBody.COMMANDID, AddRigidBody.class);
    }
}
