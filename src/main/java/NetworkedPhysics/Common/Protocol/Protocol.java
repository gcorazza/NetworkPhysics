package NetworkedPhysics.Common.Protocol;

import java.util.HashMap;
import java.util.Map;

public class Protocol {
    public static Map<Byte, Class<? extends PhysicsMessage>> protocol;

    static {
        protocol = new HashMap<>();
        protocol.put(InitialState.COMMANDID, InitialState.class);
        protocol.put(InitialState.COMMANDID, CompleteWorldState.class);
    }
}
