package NetworkedPhysics;

import NetworkedPhysics.Common.NetworkPhysicsListener;
import NetworkedPhysics.Common.PhysicsInput;
import NetworkedPhysics.Common.Protocol.Protocol;
import NetworkedPhysics.Common.Protocol.ServerCommand;
import NetworkedPhysics.Common.Protocol.UserCommand;
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

public class NetworkedPhysicsServer extends RewindablePhysicsWorld implements Runnable, UDPServerListener {

    private UDPServer udpServer = new NettyUDPServer(this);

    public NetworkedPhysicsServer(int port, NetworkPhysicsListener networkPhysicsListener) {
        super(networkPhysicsListener);
        udpServer.startOn(port);
    }

    public int getStepsPerSecond() {
        return networkWorld.getStepsPerSecond();
    }

    public long getStartTime() {
        return networkWorld.getStartTime();
    }

    //calledByServer
//    public void setClientInput(PhysicsInput setClientInput) {
//        SetInput setInput = new SetInput(networkWorld.getStep() + 1, setClientInput);
//        super.addManipulation(setInput);
//        sendToAll(setInput);
//    }


    @Override
    public synchronized void addManipulation(WorldManipulation worldManipulation) {
        udpServer.sendToAll(worldManipulation);
        super.addManipulation(worldManipulation);
    }

    @Override
    public int update() {
        stepToActualFrame();
        return networkWorld.getStep();
    }

    public void run() {
        running = true;

        while (running) {
            stepToActualFrame();
        }
    }

    public void setClientInput(PhysicsInput clientInput, int from) {
        physicsListener.clientInput(clientInput, from);
    }

    public void sendTo(int id, ServerCommand command) {
        udpServer.send(id, command);
    }

    @Override
    public synchronized void newClient(int id) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "new UDP Client");
        WorldState lastWorldState = getLastWorldState();
        lastWorldState.updateTimesPassed(networkWorld.getStartTime());
        sendTo(id, lastWorldState);
        sendManipulationsSince(id, lastWorldState.step);
        physicsListener.newClient(id);
    }

    private void sendManipulationsSince(int id, int step) {
        for (int i = step; i < networkWorld.getStep(); i++) {
            List<WorldManipulation> worldManipulations = manipulations.get(i);
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
}
