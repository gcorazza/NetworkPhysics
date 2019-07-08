package NetworkedPhysics.Common;

import NetworkedPhysics.Common.Dto.NetworkedPhysicsObjectDto;
import NetworkedPhysics.Common.Protocol.clientCommands.InputArguments;
import NetworkedPhysics.Common.Protocol.serverCommands.Manipulations.AddInput;
import NetworkedPhysics.Common.Protocol.serverCommands.Manipulations.AddRigidBody;
import NetworkedPhysics.Common.Protocol.serverCommands.Manipulations.SetInput;
import NetworkedPhysics.Common.Protocol.serverCommands.Manipulations.WorldManipulation;
import NetworkedPhysics.Common.Protocol.serverCommands.WorldState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RewindablePhysicsWorld {


    protected final NetworkPhysicsListener physicsListener;
    protected NetworkPhysicsWorld networkWorld;

    protected Map<Integer, List<WorldManipulation>> manipulations = new HashMap<>();

    protected int port;
    protected boolean running = false;
    //protected UdpSocket connection;
    protected WorldState lastWorldState;



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

    public AddRigidBody addNetworkedPhysicsObjectNow(NetworkedPhysicsObjectDto networkedPhysicsObjectDto, int id) {
        AddRigidBody message = new AddRigidBody(getStep(), id, networkedPhysicsObjectDto);
        addManipulation(message);
        return message;
    }

    public AddInput addInputNow(int id, PhysicsInput input) {
        AddInput addInput = new AddInput(id, input, getStep());
        addManipulation(addInput);
        return addInput;
    }

    public synchronized SetInput setInputNow(InputArguments input, int id) {
        SetInput setInput = new SetInput(networkWorld.getStep(), id, input);
        addManipulation(setInput);
        return setInput;
    }

    public boolean isRunning() {
        return running;
    }
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
        if (lastWorldState ==null || worldState.step>lastWorldState.step){
            lastWorldState = worldState;
        }
        networkWorld = new NetworkPhysicsWorld(worldState, physicsListener);
        physicsListener.rewinded();
    }


    public Map<Integer, PhysicsObject> getObjects() {
        return networkWorld.objects;
    }

    public PhysicsObject getObject(int physicsObjectId) {
        return networkWorld.objects.get(physicsObjectId);
    }

    public int getStep() {
        return networkWorld.getStep();
    }

    public Map<Integer, List<WorldManipulation>> getManipulations() {
        return manipulations;
    }

    public long getStartTime() {
        return networkWorld.getStartTime();
    }
}

/*

1. init engine
2. send state
3. sync inputs

* */
