package NetworkedPhysics.Common.Protocol.clientCommands;

import NetworkedPhysics.Common.PhysicsInput;
import NetworkedPhysics.Common.Protocol.PhysicsMessage;
import NetworkedPhysics.Common.Protocol.UserCommand;
import NetworkedPhysics.Server.NetworkedPhysicsServer;

import static Util.Utils.gson;

public class ClientInput implements UserCommand {
    public static final byte COMMANDID = 3;

    PhysicsInput clientInput;

    public ClientInput() {
    }

    public ClientInput(PhysicsInput clientInput) {
        this.clientInput = clientInput;
    }


    @Override
    public byte getCommandCode() {
        return COMMANDID;
    }

    @Override
    public void processMessage(NetworkedPhysicsServer physicsServer, int from) {
        physicsServer.clientInput(clientInput, from);
    }

    @Override
    public byte[] getPacket() {
        return gson.toJson(this).getBytes();
    }

    @Override
    public PhysicsMessage fromBlob(byte[] blob) {
        return gson.fromJson(new String(blob), ClientInput.class);
    }
}
