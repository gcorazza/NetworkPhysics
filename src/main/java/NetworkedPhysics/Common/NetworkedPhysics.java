package NetworkedPhysics.Common;

import NetworkedPhysics.Common.Manipulations.AddRigidBody;
import NetworkedPhysics.Common.Manipulations.WorldManipulation;
import NetworkedPhysics.Network.Client;
import NetworkedPhysics.Network.Messages.UdpClient;
import NetworkedPhysics.Network.UdpSocket;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkedPhysics {

    protected DiscreteDynamicsWorld world;
    List<NetworkedPhysicsObject> objects = new ArrayList<NetworkedPhysicsObject>();
    Map<InetSocketAddress, UdpClient> clients = new HashMap<>();

    UpdateInputsCallback updateInputs;

    protected UdpSocket udpSocket;
    protected Map<Integer, List<WorldManipulation>> manipulations= new HashMap<>();

    public NetworkedPhysics(UpdateInputsCallback updateInputs) {
        this.updateInputs = updateInputs;
    }

    protected int frame;
    protected int port;
    protected int stepsPerSecond = 20;
    protected long startTime;
    protected boolean running = false;

    public UdpSocket getConnection() {
        return connection;
    }

    protected UdpSocket connection;

    protected int msPassedSiceStart() {
        return (int) (System.currentTimeMillis() - startTime);
    }

    protected int shouldBeInFrame() {
        return (int) (((float) (System.currentTimeMillis() - startTime)) / 1000 * stepsPerSecond);
    }

    protected long timeForFrame(int frame) {
        int v = (int) (frame * (1000f / stepsPerSecond));
        return (long) (startTime + v);
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

    protected void step() {
        if (world == null) {
            return;
        }
        waitTilNextFrame();

        processManipulations();

        if (updateInputs != null)
            updateInputs.updateInputs(world, objects, clients);
        world.stepSimulation(1000f/stepsPerSecond);

        frame++;
        //FullSync
    }

    private void processManipulations() {
        List<WorldManipulation> worldManipulations = manipulations.get(frame);
        if (worldManipulations != null) {
            //
        }
    }

    protected void addManipulation(WorldManipulation worldManipulation) {
        List<WorldManipulation> stepManipulations = manipulations.computeIfAbsent(frame, k -> new ArrayList<>());
        stepManipulations.add(worldManipulation);
        //send to all
    }

    public void addRigidBody(RigidBody rigidBody) {
        addManipulation(new AddRigidBody(frame, rigidBody));
    }

    public DiscreteDynamicsWorld getWorld() {
        return world;
    }

    public List<NetworkedPhysicsObject> getObjects() {
        return objects;
    }

    public Map<InetSocketAddress, UdpClient> getClients() {
        return clients;
    }

    public UpdateInputsCallback getUpdateInputs() {
        return updateInputs;
    }

    public UdpSocket getUdpSocket() {
        return udpSocket;
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
}

/*

1. init engine
2. send state
3. sync inputs

* */
