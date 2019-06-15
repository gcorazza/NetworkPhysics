package NetworkedPhysics.Common.Protocol;

import NetworkedPhysics.Client.NetworkedPhysicsClient;
import NetworkedPhysics.Common.NetworkedPhysics;
import NetworkedPhysics.Common.NetworkedPhysicsObject;
import NetworkedPhysics.Common.PhysicsInput;
import NetworkedPhysics.Network.UdpConnection;
import NetworkedPhysics.Server.NetworkedPhysicsServer;
import com.google.gson.Gson;

import java.util.Map;

public class WorldState extends PhysicsMessage {
    public static final byte COMMANDID=0;
    public int timePassed;
    public int stepsPerSecond;
    public int frame;
    public long btSeed;
    public Map<Integer, NetworkedPhysicsObject> objectMap;
    public Map<Integer, PhysicsInput> inputs;

    @Override
    public byte[] toBlob() {
        Gson gson = new Gson();
        return gson.toJson(this).getBytes();
    }

    @Override
    public WorldState fromBlob(byte[] blob) {
        Gson gson = new Gson();
        return gson.fromJson(new String(blob), WorldState.class);
    }

    @Override
    public void processMessage(NetworkedPhysics networkedPhysics, UdpConnection from) {
        ((NetworkedPhysicsClient) networkedPhysics).init(timePassed, stepsPerSecond);
    }

    @Override
    public byte getCommandID() {
        return COMMANDID;
    }
}
