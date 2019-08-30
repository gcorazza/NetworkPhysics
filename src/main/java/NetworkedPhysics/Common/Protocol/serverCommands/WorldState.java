package NetworkedPhysics.Common.Protocol.serverCommands;

import NetworkedPhysics.Cereal.Putter;
import NetworkedPhysics.Cereal.WorldStateCereal;
import NetworkedPhysics.NetworkedPhysicsClient;
import NetworkedPhysics.Common.PhysicsInput;
import NetworkedPhysics.Common.PhysicsObject;
import NetworkedPhysics.Common.Protocol.ServerCommand;
import org.apache.commons.lang3.SerializationUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

import static NetworkedPhysics.Cereal.WorldStateCereal.worldStateCereal;
import static NetworkedPhysics.Util.Utils.*;

public class WorldState implements ServerCommand, Serializable{
    public transient static final byte COMMANDID = 1;
    public int timePassed;
    public int stepsPerSecond;
    public int step;
    public long btSeed;
    public Map<Integer, PhysicsObject> objectMap;
    public Map<Integer, PhysicsInput> inputs;

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

    @Override
    public byte getCommandCode() {
        return COMMANDID;
    }

    @Override
    public WorldState fromBlob(byte[] blob) {
//        return (WorldState) fromByteArray(blob);
        try {
            WorldState worldState = worldStateCereal.get(new DataInputStream(new ByteArrayInputStream(blob)));
            System.out.println("gsonPretty.toJson(worldState) = " + gson.toJson(worldState));

            return worldState;
        } catch (IOException e) {
            e.printStackTrace();
        }

//        return gson.fromJson(new String(blob),WorldState.class);
        return null;
    }

    @Override
    public byte[] getPacket() {
        byte[] mySerialization = runPutter(out2 -> worldStateCereal.put(this, out2));
        byte[] gsonSerialization = gson.toJson(this).getBytes();
        byte[] apache = SerializationUtils.serialize(this);
        byte[] javaSerialisation = toByteArray(this);

        System.out.println("my = " + mySerialization.length);
        System.out.println("apache = " + apache.length);
        System.out.println(new String(apache));
        System.out.println("java = " + javaSerialisation.length);
        System.out.println("gson = " + gsonSerialization.length);
        System.out.println(new String(gsonSerialization));

        return mySerialization;
    }

    public static byte[] runPutter(Putter putter) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream out1 = new DataOutputStream(out);
        try {
            putter.putTo(out1);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    @Override
    public void processMessage(NetworkedPhysicsClient physicsClient) {
        physicsClient.setRemoteWorldState(this);
    }
}
