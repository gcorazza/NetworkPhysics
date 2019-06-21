package NetworkedPhysics.Common.Protocol.clientCommands;

import NetworkedPhysics.Common.Protocol.UserCommand;
import NetworkedPhysics.Server.NetworkedPhysicsServer;

public class GetWorldState implements UserCommand {
    public static final byte COMMANDID=2;

    public GetWorldState() {
    }

    public GetWorldState fromBlob(byte[] blob) {
        return this;
    }

    @Override
    public byte getCommandCode() {
        return COMMANDID;
    }

    @Override
    public void processMessage(NetworkedPhysicsServer physicsServer, int from) {
        physicsServer.sendTo(from,physicsServer.saveState());
    }

    @Override
    public byte[] getPacket() {
        return new byte[0];
    }
}
