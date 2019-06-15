package NetworkedPhysics.Common;

import NetworkedPhysics.Common.Protocol.Manipulations.WorldManipulation;
import NetworkedPhysics.Common.Protocol.PhysicsMessage;
import NetworkedPhysics.Common.Protocol.WorldState;
import NetworkedPhysics.Network.UdpConnection;
import NetworkedPhysics.Network.UdpSocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class NetworkedPhysics {


    private final NetworkPhysicsListener physicsListener;
    protected NetworkPhysicsWorld networkWorld;

    protected Map<Integer, List<WorldManipulation>> manipulations = new HashMap<>();

    protected int port;
    protected boolean running = false;
    protected UdpSocket connection;

    public NetworkedPhysics(NetworkPhysicsListener updateInputs) {
        this.physicsListener = updateInputs;
    }


    protected void stepToActualFrame() {

        int shouldBeInFrame = networkWorld.shouldBeInFrame();

        while (networkWorld.getFrame() < shouldBeInFrame) {
            networkWorld.step(manipulations.get(networkWorld.getFrame()));
        }
    }

    public void addManipulation(WorldManipulation worldManipulation) {
        List<WorldManipulation> stepManipulations = manipulations.computeIfAbsent(worldManipulation.frame, k -> new ArrayList<>());
        stepManipulations.add(worldManipulation);
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

    private void rewind(int toFrame) {
        int lfs=findLastFullSyncFrom(toFrame);

    }

    private int findLastFullSyncFrom(int toFrame) {
        return 0;
    }


    public WorldState getWorldState() {
        return networkWorld.getState();
    }


    public Map<Integer, NetworkedPhysicsObject> getObjects() {
        return networkWorld.objects;
    }
}

/*

1. init engine
2. send state
3. sync inputs

* */
