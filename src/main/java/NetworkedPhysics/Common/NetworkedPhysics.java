package NetworkedPhysics.Common;

import NetworkedPhysics.Common.Protocol.Manipulations.AddRigidBody;
import NetworkedPhysics.Common.Protocol.Manipulations.WorldManipulation;
import NetworkedPhysics.Common.Protocol.PhysicsMessage;
import NetworkedPhysics.Common.Protocol.Dto.NetworkedPhysicsObject;
import NetworkedPhysics.Network.UdpConnection;
import NetworkedPhysics.Network.UdpSocket;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class NetworkedPhysics {

    protected DiscreteDynamicsWorld world;
    List<NetworkedPhysicsObject> objects = new ArrayList<>();

    Map<InetSocketAddress, UdpConnection> clients = new HashMap<>();

    UpdateInputsCallback updateInputs;

    protected Map<Integer, List<WorldManipulation>> manipulations= new HashMap<>();

    protected int frame;

    protected int port;
    protected int stepsPerSecond = 20;
    protected long startTime;
    protected boolean running = false;
    protected UdpSocket connection;

    public NetworkedPhysics(UpdateInputsCallback updateInputs) {
        this.updateInputs = updateInputs;
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

        if (updateInputs != null)
            updateInputs.updateInputs(world, objects, clients);
        world.stepSimulation(1000f/stepsPerSecond);

        frame++;
    }

    private void processManipulations() {
        List<WorldManipulation> worldManipulations = manipulations.get(frame);
        if (worldManipulations != null) {
            //
        }
    }

    public void addManipulation(WorldManipulation worldManipulation) {
        List<WorldManipulation> stepManipulations = manipulations.computeIfAbsent(worldManipulation.frame, k -> new ArrayList<>());
        stepManipulations.add(worldManipulation);
        //send to all
    }

    public void addRigidBody(NetworkedPhysicsObject physicsObject) {
        objects.add(physicsObject);
        world.addRigidBody(physicsObject.getRigidBody());
    }

    public DiscreteDynamicsWorld getWorld() {
        return world;
    }

    public List<NetworkedPhysicsObject> getObjects() {
        return objects;
    }

    public Map<InetSocketAddress, UdpConnection> getClients() {
        return clients;
    }

    public UpdateInputsCallback getUpdateInputs() {
        return updateInputs;
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

    public void send(PhysicsMessage message, UdpConnection udpConnection){
        udpConnection.incrementMessageStamp();
        connection.send(message,udpConnection.inetSocketAddress);
    }

    public void shutDown(){
        connection.shutdown();
    }
}

/*

1. init engine
2. send state
3. sync inputs

* */
