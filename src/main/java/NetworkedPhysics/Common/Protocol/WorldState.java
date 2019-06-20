package NetworkedPhysics.Common.Protocol;

import NetworkedPhysics.Client.NetworkedPhysicsClient;
import NetworkedPhysics.Common.RewindablePhysicsWorld;
import NetworkedPhysics.Common.PhysicsObject;
import NetworkedPhysics.Common.PhysicsInput;
import NetworkedPhysics.Common.Util;
import NetworkedPhysics.Network.UdpConnection;
import com.google.gson.Gson;
import org.apache.commons.lang3.SerializationUtils;

import java.util.HashMap;
import java.util.Map;

public class WorldState extends PhysicsMessage {
    public static final byte COMMANDID=1;
    public int timePassed;
    public int stepsPerSecond;
    public int step;
    public long btSeed;
    public Map<Integer, PhysicsObject> objectMap;
    public Map<Integer, PhysicsInput> inputs;

    @Override
    public byte[] toBlob() {
        return Util.gson.toJson(this).getBytes();
    }

    @Override
    public WorldState fromBlob(byte[] blob) {
        return Util.gson.fromJson(new String(blob), WorldState.class);
    }

    @Override
    public void processMessage(RewindablePhysicsWorld rewindablePhysicsWorld, UdpConnection from) {
        ((NetworkedPhysicsClient) rewindablePhysicsWorld).init(timePassed, stepsPerSecond);
    }

    @Override
    public byte getCommandID() {
        return COMMANDID;
    }

    public Map<Integer, PhysicsObject> getObjectsCopy() {
        Map<Integer, PhysicsObject> copy = new HashMap<>();
        objectMap.values().forEach(o -> {
            copy.put(o.id, SerializationUtils.clone(o));
        });
        return copy;
    }

    public Map<Integer, PhysicsInput> getInputsCopy() {
        Map<Integer, PhysicsInput> copy = new HashMap<>();
        inputs.entrySet().forEach( e ->{
            copy.put(e.getKey(),SerializationUtils.clone(e.getValue()));
        });
        return copy;
    }
}
