package NetworkedPhysics;

import NetworkedPhysics.Common.Dto.NetworkedPhysicsObjectDto;
import NetworkedPhysics.Common.NetworkPhysicsListener;
import NetworkedPhysics.Common.PhysicsInput;
import NetworkedPhysics.Common.PhysicsObject;
import NetworkedPhysics.Common.Protocol.Protocol;
import NetworkedPhysics.Common.Protocol.ServerCommand;
import NetworkedPhysics.Common.Protocol.UserCommand;
import NetworkedPhysics.Common.Protocol.clientCommands.InputArguments;
import NetworkedPhysics.Common.Protocol.serverCommands.Manipulations.AddInput;
import NetworkedPhysics.Common.Protocol.serverCommands.Manipulations.AddRigidBody;
import NetworkedPhysics.Common.Protocol.serverCommands.Manipulations.SetInput;
import NetworkedPhysics.Common.Protocol.serverCommands.Manipulations.WorldManipulation;
import NetworkedPhysics.Common.Protocol.serverCommands.WorldState;
import NetworkedPhysics.Common.RewindablePhysicsWorld;
import NetworkedPhysics.Network.Message;
import NetworkedPhysics.Network.UDPServer;
import NetworkedPhysics.Network.UDPServerListener;
import NetworkedPhysics.Network.nettyUDP.NettyUDPServer;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NetworkedPhysicsServer implements Runnable, UDPServerListener {

    private UDPServer udpServer = new NettyUDPServer(this);

    private RewindablePhysicsWorld rewindableWorld;
    private NetworkPhysicsListener physicsListener;
    private boolean running;

    private int objectIdCounter;
    private int newInputId;


    public NetworkedPhysicsServer(int port, NetworkPhysicsListener networkPhysicsListener) {
        rewindableWorld = new RewindablePhysicsWorld(networkPhysicsListener);
        this.physicsListener = networkPhysicsListener;
        udpServer.startOn(port);
    }

//    public int getStepsPerSecond() {
//        return networkWorld.getStepsPerSecond();
//    }
//
//    public long getStartTime() {
//        return networkWorld.getStartTime();
//    }

    //calledByServer
//    public void setClientInput(PhysicsInput setClientInput) {
//        SetInput setInput = new SetInput(networkWorld.getStep() + 1, setClientInput);
//        super.addRemoteManipulation(setInput);
//        sendToAll(setInput);
//    }


    public int update() {
        rewindableWorld.stepToActualFrame();
        return rewindableWorld.getStep();
    }

    public void run() {
        running = true;

        while (running) {
            rewindableWorld.stepToActualFrame();
        }
    }

    public void setClientInput(InputArguments clientInput, int from) {
        physicsListener.clientInput(clientInput, from);
    }

    public void sendTo(int id, ServerCommand command) {
        udpServer.send(id, command);
    }

    @Override
    public synchronized void newClient(int id) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "new UDP Client");
        WorldState worldState = getWorldStateNewClient();
        sendTo(id, worldState);
        sendManipulationsSince(id, worldState.step);
        physicsListener.newClient(id);
    }

    private void sendManipulationsSince(int id, int step) {
        for (int i = step; i < rewindableWorld.getStep(); i++) {
            List<WorldManipulation> worldManipulations = rewindableWorld.getManipulations().get(i);
            if (worldManipulations != null) {
                worldManipulations.forEach(wm -> sendTo(id, wm));
            }
        }
    }

    @Override
    public void disconnected(int id) {

    }

    @Override
    public void newMessage(int fromId, Message message) {
        UserCommand command = Protocol.getUserCommand(message);
        if (command != null) {
            command.processMessage(this, fromId);
        }
    }

    public void shutDown() {
        udpServer.stop();
    }

    public PhysicsObject getObject(int physicsObjectId) {
        return rewindableWorld.getObject(physicsObjectId);
    }

    public synchronized int addNetworkedPhysicsObjectNow(NetworkedPhysicsObjectDto networkedPhysicsObjectDto) {
        int id = newObjectId();
        AddRigidBody message = rewindableWorld.addNetworkedPhysicsObjectNow(networkedPhysicsObjectDto, id);
        udpServer.sendToAll(message);
        return id;
    }

    public synchronized int addInputNow(PhysicsInput input) {
        int id = newInputId();
        AddInput addInput = rewindableWorld.addInputNow(id, input);
        udpServer.sendToAll(addInput);
        return id;
    }

    public synchronized void setInputNow(InputArguments input, int id) {
        SetInput setInput = rewindableWorld.setInput(input, id);
        udpServer.sendToAll(setInput);
    }

    protected int newObjectId() {
        return ++objectIdCounter;
    }

    protected int newInputId() {
        return ++newInputId;
    }

    public RewindablePhysicsWorld getRewindableWorld() {
        return rewindableWorld;
    }

    public WorldState getWorldStateNewClient() {
        WorldState worldState = rewindableWorld.saveState();
        rewindableWorld.restore(worldState);
        return worldState;
    }
}

