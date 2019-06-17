package NetworkedPhysics.Common.Protocol;

import NetworkedPhysics.Client.NetworkedPhysicsClient;
import NetworkedPhysics.Common.RewindablePhysicsWorld;
import NetworkedPhysics.Common.PhysicsObject;
import NetworkedPhysics.Common.PhysicsInput;
import NetworkedPhysics.Network.UdpConnection;
import com.google.gson.Gson;

import java.util.Map;

public class WorldState extends PhysicsMessage {
    public static final byte COMMANDID=0;
    public int timePassed;
    public int stepsPerSecond;
    public int frame;
    public long btSeed;
    public Map<Integer, PhysicsObject> objectMap;
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
    public void processMessage(RewindablePhysicsWorld rewindablePhysicsWorld, UdpConnection from) {
        ((NetworkedPhysicsClient) rewindablePhysicsWorld).init(timePassed, stepsPerSecond);
    }

    @Override
    public byte getCommandID() {
        return COMMANDID;
    }
}
