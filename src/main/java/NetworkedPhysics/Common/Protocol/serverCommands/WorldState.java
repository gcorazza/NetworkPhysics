package NetworkedPhysics.Common.Protocol.serverCommands;

import NetworkedPhysics.NetworkedPhysicsClient;
import NetworkedPhysics.Common.PhysicsInput;
import NetworkedPhysics.Common.PhysicsObject;
import NetworkedPhysics.Common.Protocol.ServerCommand;
import org.apache.commons.lang3.SerializationUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static NetworkedPhysics.Util.Utils.gson;

public class WorldState implements ServerCommand {
    public static final byte COMMANDID = 1;
    public int timePassed;
    public int stepsPerSecond;
    public int step;
    public long btSeed;
    public Map<Integer, PhysicsObject> objectMap;
    public Map<Integer, PhysicsInput> inputs;

    public WorldState() {
        System.out.println();
    }

    @Override
    public WorldState fromBlob(byte[] blob) {
        return gson.fromJson(new String(blob), WorldState.class);
    }

    public Map<Integer, PhysicsObject> getObjectsCopy() {
        Map<Integer, PhysicsObject> copy = new HashMap<>();
        objectMap.values().forEach(o -> copy.put(o.id, SerializationUtils.clone(o)));
        return copy;
    }

    public Map<Integer, PhysicsInput> getInputsCopy() {
        Map<Integer, PhysicsInput> copy = new HashMap<>();
        inputs.entrySet().forEach(e -> copy.put(e.getKey(), SerializationUtils.clone(e.getValue())));
        return copy;
    }

    @Override
    public byte getCommandCode() {
        return COMMANDID;
    }

    @Override
    public byte[] getPacket() {
        return gson.toJson(this).getBytes();
    }

    @Override
    public void processMessage(NetworkedPhysicsClient physicsClient) {
        physicsClient.setRemoteWorldState(this);
    }

    public void updateTimesPassed(long startTime) {
        timePassed = (int) (System.currentTimeMillis() - startTime);
    }

    public double getDifference(WorldState ws) {
        double diff = 0;

        if (ws == null || ws.step != step || ws.inputs.size() != inputs.size() || ws.objectMap.size() != objectMap.size()) {
            return -1;
        }

        Iterator<Integer> objKeyIt = objectMap.keySet().iterator();

        while (objKeyIt.hasNext()) {
            int next = objKeyIt.next();
            PhysicsObject physicsObject0 = objectMap.get(next);
            PhysicsObject physicsObject1 = ws.objectMap.get(next);
            if (physicsObject0 == null || physicsObject1 == null) {
                return -1;
            }
            diff+=physicsObject0.diff(physicsObject1);
        }


        return diff;
    }
}
