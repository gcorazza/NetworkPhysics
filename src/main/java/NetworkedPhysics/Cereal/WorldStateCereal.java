package NetworkedPhysics.Cereal;

import NetworkedPhysics.Common.Protocol.serverCommands.WorldState;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

import static NetworkedPhysics.Cereal.LambdaUtils.silentConsumer;
import static NetworkedPhysics.Cereal.PhysicsInputCereal.physicsInputCereal;
import static NetworkedPhysics.Cereal.PhysicsObjectCereal.physicsObjectCereal;

public class WorldStateCereal implements Cereal<WorldState> {


    public static final WorldStateCereal worldStateCereal = new WorldStateCereal();

    private WorldStateCereal() {
    }

    @Override
    public DataOutputStream put(WorldState worldState, DataOutputStream out) throws IOException {
        out.writeInt(worldState.timePassed);
        out.writeInt(worldState.stepsPerSecond);
        out.writeInt(worldState.step);
        out.writeLong(worldState.btSeed);

        out.writeInt(worldState.objectMap.size());
        worldState.objectMap.entrySet().forEach(silentConsumer(es -> {
            out.writeInt(es.getKey());
            physicsObjectCereal.put(es.getValue(), out);
        }));
        out.writeInt(worldState.inputs.size());
        worldState.inputs.entrySet().forEach(silentConsumer(es -> {
            out.writeInt(es.getKey());
            physicsInputCereal.put(es.getValue(), out);
        }));
        return out;
    }

    @Override
    public WorldState get(DataInputStream in) throws IOException {
        WorldState worldState = new WorldState();
        worldState.timePassed = in.readInt();

        worldState.stepsPerSecond = in.readInt();
        worldState.step = in.readInt();
        worldState.btSeed = in.readLong();

        worldState.objectMap = new HashMap<>();
        int objectMapSize = in.readInt();
        for (int i = 0; i < objectMapSize; i++) {
            worldState.objectMap.put(in.readInt(), physicsObjectCereal.get(in));
        }

        worldState.inputs = new HashMap<>();
        int inputsSize = in.readInt();
        for (int i = 0; i < inputsSize; i++) {
            worldState.inputs.put(in.readInt(), physicsInputCereal.get(in));
        }

        return worldState;
    }
}
