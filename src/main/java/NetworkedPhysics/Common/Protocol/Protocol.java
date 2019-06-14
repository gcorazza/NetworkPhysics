package NetworkedPhysics.Common.Protocol;

import java.util.HashMap;
import java.util.Map;

public class Protocol {
    public static Map<Byte, Class<? extends PhysicsMessage>> protocol;

    static {
        protocol = new HashMap<>();
        protocol.put(WorldState.COMMANDID, WorldState.class);
        protocol.put(GetWorldState.COMMANDID, GetWorldState.class);
    }
}
