package NetworkedPhysics.Common;

import NetworkedPhysics.Common.Protocol.Dto.NetworkedPhysicsObjectDto;
import NetworkedPhysics.Common.Protocol.WorldState;
import NetworkedPhysics.Common.Protocol.Manipulations.WorldManipulation;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import org.apache.commons.lang3.SerializationUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkPhysicsWorld {

    protected DiscreteDynamicsWorld world;
    Map<Integer, PhysicsObject> objects = new HashMap<>();
    Map<Integer, PhysicsInput> inputs = new HashMap<>();
    protected int frame;
    protected int stepsPerSecond = 60;
    protected long startTime;
    final NetworkPhysicsListener physicsListener;


    public NetworkPhysicsWorld(NetworkPhysicsListener physicsListener) {
        this.physicsListener = physicsListener;
        world = Util.getWorld();
        startTime = System.currentTimeMillis();
    }

    public NetworkPhysicsWorld(WorldState worldState, NetworkPhysicsListener physicsListener) {
        this.physicsListener = physicsListener;
        world = Util.getWorld();
        this.stepsPerSecond = worldState.stepsPerSecond;
        startTime = System.currentTimeMillis() - worldState.timePassed;
        frame = worldState.frame;
        objects = worldState.getObjectsCopy();
        inputs = worldState.getInputsCopy();
        ((SequentialImpulseConstraintSolver) world.getConstraintSolver()).setRandSeed(worldState.btSeed);
        objects.values().forEach( o -> world.addRigidBody( o.getBody()));
    }

    protected int msPassedSiceStart() {
        return (int) (System.currentTimeMillis() - startTime);
    }

    protected int shouldBeInFrame() {
        int i = (int) (((float) (System.currentTimeMillis() - startTime)) / 1000 * stepsPerSecond);
        return i;
    }

    protected long timeForFrame(int frame) {
        int v = (int) (frame * (1000f / stepsPerSecond));
        return (startTime + v);
    }

    protected int msToNextFrame() {
        long l = System.currentTimeMillis();
        long tff = timeForFrame(frame + 1);
        return (int) (tff - l);
    }

    protected void waitTilNextFrame() {
        try {
            int i = msToNextFrame();
            if (i > 0)
                Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void step(List<WorldManipulation> worldManipulations) {
        if (worldManipulations != null) {
            worldManipulations.forEach(wm -> wm.manipulate(this));
        }
        inputs.values().forEach(in -> physicsListener.stepInput(world, objects, in));
        world.stepSimulation(1f / stepsPerSecond, 0);

        frame++;
    }


    public void addRigidBody(NetworkedPhysicsObjectDto objectDto) {
        PhysicsObject physicsObject = new PhysicsObject(objectDto);
        objects.put(objectDto.id, physicsObject);
        world.addRigidBody(physicsObject.getBody());
        physicsListener.newObject(objectDto.id);
    }

    public int getStepsPerSecond() {
        return stepsPerSecond;
    }

    public long getStartTime() {
        return startTime;
    }

    public int getFrame() {
        return frame;
    }

    public NetworkPhysicsListener getPhysicsListener() {
        return physicsListener;
    }

    public void setInput(PhysicsInput input) {
        PhysicsInput physicsInput = inputs.get(input.id);
        if (physicsInput == null) {
            physicsListener.newInput(input);
        }
        inputs.put(input.id, input);
    }

    public WorldState getState() {
        WorldState worldState = new WorldState();
        worldState.stepsPerSecond = stepsPerSecond;
        worldState.timePassed = (int) (System.currentTimeMillis() - startTime);
        worldState.frame = frame;
        worldState.btSeed = ((SequentialImpulseConstraintSolver) world.getConstraintSolver()).getRandSeed();
        Map<Integer, PhysicsObject> objectsCopy = new HashMap<>();
        objects.values().forEach(npo -> {
                    objectsCopy.put(npo.id, new PhysicsObject(npo.bodyToDto()));
                }
        );
        worldState.objectMap = objectsCopy;
        Map<Integer, PhysicsInput> inputsCopy = new HashMap<>();
        inputs.values().forEach(in -> {
            inputsCopy.put(in.id, SerializationUtils.clone(in));
        });
        worldState.inputs = inputsCopy;
        return worldState;
    }

    public PhysicsObject getObject(int objId) {
        return objects.get(objId);
    }
}
