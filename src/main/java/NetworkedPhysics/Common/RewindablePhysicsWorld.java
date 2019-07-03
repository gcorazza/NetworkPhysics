package NetworkedPhysics.Common;

import NetworkedPhysics.Common.Dto.NetworkedPhysicsObjectDto;
import NetworkedPhysics.Common.Protocol.serverCommands.Manipulations.AddRigidBody;
import NetworkedPhysics.Common.Protocol.serverCommands.Manipulations.SetInput;
import NetworkedPhysics.Common.Protocol.serverCommands.Manipulations.WorldManipulation;
import NetworkedPhysics.Common.Protocol.serverCommands.WorldState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class RewindablePhysicsWorld {


    protected final NetworkPhysicsListener physicsListener;
    protected NetworkPhysicsWorld networkWorld;

    protected Map<Integer, List<WorldManipulation>> manipulations = new HashMap<>();

    protected int port;
    protected boolean running = false;
    //protected UdpSocket connection;
    protected WorldState lastWorldState;

    private int objectIdCounter;
    private int newInputId;


    public RewindablePhysicsWorld(NetworkPhysicsListener physicsListener) {
        this.physicsListener = physicsListener;
        networkWorld = new NetworkPhysicsWorld(physicsListener);
    }

    public RewindablePhysicsWorld(NetworkPhysicsListener physicsListener, WorldState worldState) {
        this.physicsListener = physicsListener;
        networkWorld = new NetworkPhysicsWorld(worldState, physicsListener);
    }

    public synchronized void stepToActualFrame() {
        int shouldBeInFrame = networkWorld.shouldBeInStep();

        while (networkWorld.getStep() < shouldBeInFrame) {
            networkWorld.step(manipulations.get(networkWorld.getStep()));
        }
    }

    public synchronized int step() {
        networkWorld.step(manipulations.get(networkWorld.getStep()));
        return networkWorld.getStep();
    }

    public synchronized void addManipulation(WorldManipulation worldManipulation) {
        List<WorldManipulation> stepManipulations = manipulations.computeIfAbsent(worldManipulation.step, k -> new ArrayList<>());
        stepManipulations.add(worldManipulation);

        if (networkWorld.step > worldManipulation.step) {
            rewindToLastState();
        }
    }

    public synchronized int addNetworkedPhysicsObjectNow(NetworkedPhysicsObjectDto networkedPhysicsObjectDto) {
        int id = newObjectId();
        AddRigidBody message = new AddRigidBody(networkWorld.getStep(), id, networkedPhysicsObjectDto);
        addManipulation(message);
        return id;
    }

    public  int addInputNow(PhysicsInput input) {
        int id = newInputId();
        setInput(input, id);
        return id;
    }

    public synchronized void setInput(PhysicsInput input, int id) {
        SetInput setInput = new SetInput(networkWorld.getStep(), id, input);
        addManipulation(setInput);
    }

    public boolean isRunning() {
        return running;
    }

    public abstract int update();

//    public void send(PhysicsMessage message, UdpConnection udpConnection){
//        udpConnection.incrementMessageStamp();
//        connection.send(message,udpConnection.inetSocketAddress);
//    }

//    public void sendToServer(UdpConnection receiver, PhysicsMessage message) {
//        message.stamp = receiver.nextStamp();
//        connection.send(message, receiver.inetSocketAddress);
//    }
//
//    public void shutDown() {
//        connection.shutdown();
//    }

    public void rewindToLastState() {
        if (lastWorldState == null) {
            networkWorld = new NetworkPhysicsWorld(physicsListener);
        } else {
            restore(lastWorldState);
        }
    }

    public WorldState getLastWorldState(){
        if (lastWorldState != null) {
            return lastWorldState;
        }
        lastWorldState = saveState();
        return lastWorldState;
    }

    public WorldState saveState() {
        WorldState state = networkWorld.getState();
        lastWorldState = state;
        return state;
    }

    public void restore(WorldState worldState) {
        networkWorld = new NetworkPhysicsWorld(worldState, physicsListener);
        physicsListener.rewinded();
    }


    public Map<Integer, PhysicsObject> getObjects() {
        return networkWorld.objects;
    }

    public PhysicsObject getObject(int physicsObjectId) {
        return networkWorld.objects.get(physicsObjectId);
    }

    protected int newObjectId() {
        return ++objectIdCounter;
    }

    protected int newInputId() {
        return ++newInputId;
    }


    public int getStep() {
        return networkWorld.getStep();
    }
}

/*

1. init engine
2. send state
3. sync inputs

* */
