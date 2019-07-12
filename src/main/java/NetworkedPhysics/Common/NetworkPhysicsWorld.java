package NetworkedPhysics.Common;

import NetworkedPhysics.Common.Dto.NetworkedPhysicsObjectDto;
import NetworkedPhysics.Common.Protocol.clientCommands.InputArguments;
import NetworkedPhysics.Common.Protocol.serverCommands.WorldState;
import NetworkedPhysics.Common.Protocol.serverCommands.Manipulations.WorldManipulation;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import org.apache.commons.lang3.SerializationUtils;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static NetworkedPhysics.Util.Utils.getWorld;

public class NetworkPhysicsWorld {

    protected DiscreteDynamicsWorld world;
    Map<Integer, PhysicsObject> objects = new HashMap<>();
    Map<Integer, PhysicsInput> inputs = new HashMap<>();
    protected int step;
    protected int stepsPerSecond = 60;
    protected long startTime;
    final NetworkPhysicsListener physicsListener;
    private boolean logStatesToFile = true;
    private FileOutputStream stateWriter;

    {
        try {
            stateWriter = new FileOutputStream("_" + Thread.currentThread().getName() + ".states");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public NetworkPhysicsWorld(NetworkPhysicsListener physicsListener) {
        this.physicsListener = physicsListener;
        world = getWorld();
        startTime = System.currentTimeMillis();
    }

    public NetworkPhysicsWorld(WorldState worldState, NetworkPhysicsListener physicsListener) {
        this.physicsListener = physicsListener;
        world = getWorld();
        this.stepsPerSecond = worldState.stepsPerSecond;
        startTime = System.currentTimeMillis() - worldState.timePassed;
        step = worldState.step;
        objects = worldState.getObjectsCopy();
        inputs = worldState.getInputsCopy();
        ((SequentialImpulseConstraintSolver) world.getConstraintSolver()).setRandSeed(worldState.btSeed);
        objects.values().forEach(o -> world.addRigidBody(o.getBody()));
    }

    protected int msPassedSiceStart() {
        return (int) (System.currentTimeMillis() - startTime);
    }

    protected int shouldBeInStep() {
        return (int) (((float) (System.currentTimeMillis() - startTime)) / 1000 * stepsPerSecond);
    }

    public void step(List<WorldManipulation> worldManipulations) {
        if (worldManipulations != null) {
            worldManipulations.forEach(wm -> wm.manipulate(this));
        }
        inputs.values().forEach(in -> in.update(this));
        world.stepSimulation(1f / stepsPerSecond, 0);

        step++;

        if (logStatesToFile) {
            byte[] packet = getState().getPacket();
            try {
                stateWriter.write(packet);
                stateWriter.write('\n');
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void addRigidBody(NetworkedPhysicsObjectDto objectDto, int id) {
        PhysicsObject physicsObject = new PhysicsObject(objectDto, id);
        if (objects.get(id) != null) {
            throw new RuntimeException("ID:" + id + " already exists");
        }
        objects.put(id, physicsObject);
        world.addRigidBody(physicsObject.getBody());
        if (physicsListener != null) {
            physicsListener.newObject(id);
        }
    }

    public int getStepsPerSecond() {
        return stepsPerSecond;
    }

    public long getStartTime() {
        return startTime;
    }

    public int getStep() {
        return step;
    }

    public void setInput(InputArguments input, int inputId) {
        PhysicsInput physicsInput = inputs.get(inputId);
        if (physicsInput != null) {
            physicsInput.setInputArguments(input);
        } else {
            System.out.println("inputId not found: " + inputId);
        }
    }

    public synchronized WorldState getState() {
        WorldState worldState = new WorldState();
        worldState.stepsPerSecond = stepsPerSecond;
        worldState.timePassed = (int) (System.currentTimeMillis() - startTime);
        worldState.step = step;
        worldState.btSeed = ((SequentialImpulseConstraintSolver) world.getConstraintSolver()).getRandSeed();
        Map<Integer, PhysicsObject> objectsCopy = new HashMap<>();
        objects.values().forEach(npo -> objectsCopy.put(npo.id, new PhysicsObject(npo.bodyToDto(), npo.id))
        );
        worldState.objectMap = objectsCopy;
        Map<Integer, PhysicsInput> inputsCopy = new HashMap<>();
        inputs.entrySet().forEach(e -> {
            inputsCopy.put(e.getKey(), SerializationUtils.clone(e.getValue()));
        });
        worldState.inputs = inputsCopy;
        return worldState;
    }

    public PhysicsObject getObject(int objId) {
        return objects.get(objId);
    }

    public void addInput(int id, PhysicsInput input) {
        inputs.put(id, input);
    }

    public PhysicsInput getInput(int inputId) {
        return inputs.get(inputId);
    }
}
