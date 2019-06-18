package NetworkedPhysics.Common;

import NetworkedPhysics.Common.Protocol.Dto.NetworkedPhysicsObjectDto;
import NetworkedPhysics.Common.Protocol.Manipulations.AddRigidBody;
import NetworkedPhysics.Common.Protocol.Manipulations.WorldManipulation;
import NetworkedPhysics.Common.Protocol.WorldState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RewindablePhysicsWorld {


    private final NetworkPhysicsListener physicsListener;
    protected NetworkPhysicsWorld networkWorld;

    protected Map<Integer, List<WorldManipulation>> manipulations = new HashMap<>();

    protected int port;
    protected boolean running = false;
    //protected UdpSocket connection;
    protected WorldState lastWorldState;

    public RewindablePhysicsWorld(NetworkPhysicsListener updateInputs) {
        this.physicsListener = updateInputs;
        networkWorld= new NetworkPhysicsWorld(updateInputs);
    }

    public RewindablePhysicsWorld(NetworkPhysicsListener physicsListener, WorldState worldState) {
        this.physicsListener = physicsListener;
        networkWorld = new NetworkPhysicsWorld(worldState, physicsListener);
    }

    public void stepToActualFrame() {
        int shouldBeInFrame = networkWorld.shouldBeInFrame();

        while (networkWorld.getFrame() < shouldBeInFrame) {
            networkWorld.step(manipulations.get(networkWorld.getFrame()));
        }
    }

    public void step(){
        networkWorld.step(manipulations.get(networkWorld.getFrame()));
    }

    public void addManipulation(WorldManipulation worldManipulation) {
        List<WorldManipulation> stepManipulations = manipulations.computeIfAbsent(worldManipulation.frame, k -> new ArrayList<>());
        stepManipulations.add(worldManipulation);
    }

    public void addNetworkedPhysicsObjectNow(NetworkedPhysicsObjectDto networkedPhysicsObjectDto) {
        AddRigidBody message = new AddRigidBody(networkWorld.getFrame() , networkedPhysicsObjectDto);
        addManipulation(message);
    }

    public boolean isRunning() {
        return running;
    }

    public void update() {
        stepToActualFrame();
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

    protected void rewindToLastState() {
        if (lastWorldState==null){
            networkWorld= new NetworkPhysicsWorld(physicsListener);
        }else{
            restore(lastWorldState);
        }
        physicsListener.rewinded();
    }

    public WorldState getWorldState() {
        return networkWorld.getState();
    }

    public void restore(WorldState worldState){
        networkWorld= new NetworkPhysicsWorld(worldState, physicsListener);
    }


    public Map<Integer, PhysicsObject> getObjects() {
        return networkWorld.objects;
    }

    public PhysicsObject getObject(int physicsObjectId) {
        return networkWorld.objects.get(physicsObjectId);
    }
}

/*

1. init engine
2. send state
3. sync inputs

* */
