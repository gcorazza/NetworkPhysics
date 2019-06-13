package NetworkedPhysics.Common;

import NetworkedPhysics.Common.Protocol.Dto.NetworkedPhysicsObjectDto;
import NetworkedPhysics.Common.Protocol.Manipulations.WorldManipulation;
import NetworkedPhysics.Common.Protocol.PhysicsMessage;
import NetworkedPhysics.Network.UdpConnection;
import NetworkedPhysics.Network.UdpSocket;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class NetworkedPhysics {

    protected DiscreteDynamicsWorld world;
    Map<Integer, NetworkedPhysicsObject> objects = new HashMap<>();
    Map<Integer, PhysicsInput> inputs = new HashMap<>();

    NetworkPhysicsListener physicsListener;

    protected Map<Integer, List<WorldManipulation>> manipulations = new HashMap<>();


    protected int frame;

    protected int port;
    protected int stepsPerSecond = 2;
    protected long startTime;
    protected boolean running = false;
    protected UdpSocket connection;

    public NetworkedPhysics(NetworkPhysicsListener updateInputs) {
        this.physicsListener = updateInputs;
    }

    protected int msPassedSiceStart() {
        return (int) (System.currentTimeMillis() - startTime);
    }

    protected int shouldBeInFrame() {
        return (int) (((float) (System.currentTimeMillis() - startTime)) / 1000 * stepsPerSecond);
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

    protected void stepToActualFrame() {
        if (world == null) {
            return;
        }

        int shouldBeInFrame = shouldBeInFrame();

        for (int frame = 0; frame < shouldBeInFrame; frame++) {
            step();
        }
    }

    private void step() {
        processManipulations();

        inputs.values().forEach(in -> physicsListener.stepInput(world, objects, in));
        world.stepSimulation(1000f / stepsPerSecond);

        frame++;
    }

    private void processManipulations() {
        List<WorldManipulation> worldManipulations = manipulations.get(frame);
        if (worldManipulations != null) {
            worldManipulations.forEach(wm -> wm.manipulate(this));
        }
    }

    public void addManipulation(WorldManipulation worldManipulation) {
        List<WorldManipulation> stepManipulations = manipulations.computeIfAbsent(worldManipulation.frame, k -> new ArrayList<>());
        stepManipulations.add(worldManipulation);
    }

    public void addRigidBody(NetworkedPhysicsObjectDto objectDto) {
        NetworkedPhysicsObject physicsObject = new NetworkedPhysicsObject(objectDto);
        objects.put(objectDto.getId(), physicsObject);
        world.addRigidBody(physicsObject.getBody());
        physicsListener.newObject(physicsObject);
    }

    public DiscreteDynamicsWorld getWorld() {
        return world;
    }

    public Map<Integer, NetworkedPhysicsObject> getObjects() {
        return objects;
    }

    public NetworkPhysicsListener getPhysicsListener() {
        return physicsListener;
    }

    public UdpSocket getUdpConnection() {
        return connection;
    }

    public Map<Integer, List<WorldManipulation>> getManipulations() {
        return manipulations;
    }

    public int getFrame() {
        return frame;
    }

    public int getPort() {
        return port;
    }

    public int getStepsPerSecond() {
        return stepsPerSecond;
    }

    public long getStartTime() {
        return startTime;
    }

    public boolean isRunning() {
        return running;
    }

    public abstract void update();

//    public void send(PhysicsMessage message, UdpConnection udpConnection){
//        udpConnection.incrementMessageStamp();
//        connection.send(message,udpConnection.inetSocketAddress);
//    }

    public void sendTo(UdpConnection receiver, PhysicsMessage message) {
        message.stamp = receiver.nextStamp();
        connection.send(message, receiver.inetSocketAddress);
    }

    public void shutDown() {
        connection.shutdown();
    }

    public void setInput(PhysicsInput input) {
        PhysicsInput physicsInput = inputs.get(input.id);
        if (physicsInput == null) {
            physicsListener.newInput(input);
        }
        inputs.put(input.id, input);
    }

    private void rewind(int toFrame) {
        int lfs=findLastFullSyncFrom(toFrame);
    }

    private int findLastFullSyncFrom(int toFrame) {
        return 0;
    }
}

/*

1. init engine
2. send state
3. sync inputs

* */
