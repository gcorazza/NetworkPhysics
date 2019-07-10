package NetworkedPhysics.Common.Protocol.clientCommands;

import NetworkedPhysics.Common.Protocol.PhysicsMessage;
import NetworkedPhysics.Common.Protocol.UserCommand;
import NetworkedPhysics.NetworkedPhysicsServer;

import java.io.Serializable;

import static Util.Utils.gson;

public class InputArguments implements UserCommand, Serializable {
    public static final byte COMMANDID = 3;
    public boolean click;
    public boolean spawnClick;

    public InputArguments() {
    }

    @Override
    public byte getCommandCode() {
        return COMMANDID;
    }

    @Override
    public void processMessage(NetworkedPhysicsServer physicsServer, int clientId) {
        physicsServer.gotInputArguments(this, clientId);
    }

    @Override
    public byte[] getPacket() {
        return gson.toJson(this).getBytes();
    }

    @Override
    public PhysicsMessage fromBlob(byte[] blob) {
        return gson.fromJson(new String(blob), InputArguments.class);
    }
}
