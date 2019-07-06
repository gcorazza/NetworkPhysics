package NetworkedPhysics.Common.Protocol;

import NetworkedPhysics.Common.Protocol.clientCommands.InputArguments;
import NetworkedPhysics.Common.Protocol.clientCommands.GetWorldState;
import NetworkedPhysics.Common.Protocol.serverCommands.Manipulations.AddInput;
import NetworkedPhysics.Common.Protocol.serverCommands.Manipulations.AddRigidBody;
import NetworkedPhysics.Common.Protocol.serverCommands.Manipulations.SetInput;
import NetworkedPhysics.Common.Protocol.serverCommands.WorldState;
import NetworkedPhysics.Network.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Protocol {
    private static Map<Byte, Class<? extends UserCommand>> userCommands;
    private static Map<Byte, Class<? extends ServerCommand>> serverCommands;

    static {
        userCommands = new HashMap<>();
        userCommands.put(GetWorldState.COMMANDID, GetWorldState.class);
        userCommands.put(InputArguments.COMMANDID, InputArguments.class);
    }

    static {
        serverCommands = new HashMap<>();
        serverCommands.put(WorldState.COMMANDID, WorldState.class);
        serverCommands.put(SetInput.COMMANDID, SetInput.class);
        serverCommands.put(AddRigidBody.COMMANDID, AddRigidBody.class);
        serverCommands.put(AddInput.COMMANDID, AddInput.class);
    }

    public static ServerCommand getServerCommand(Message message) {
        Class<? extends ServerCommand> aClass = serverCommands.get(message.getCommandCode());
        if (aClass != null) {
            try {
                return  ((ServerCommand) aClass.newInstance().fromBlob(message.getPacket()));
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        Logger.getGlobal().log(Level.INFO, "No Protocol entry found for: " + message.getCommandCode());
        return null;
    }

    public static UserCommand getUserCommand(Message message) {
        Class<? extends UserCommand> aClass = userCommands.get(message.getCommandCode());
        if (aClass != null) {
            try {
                return  ((UserCommand) aClass.newInstance().fromBlob(message.getPacket()));
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        Logger.getGlobal().log(Level.INFO, "No Protocol entry found for: " + message.getCommandCode());
        return null;
    }
}
