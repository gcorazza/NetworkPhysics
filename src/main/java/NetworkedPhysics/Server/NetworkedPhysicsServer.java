package NetworkedPhysics.Server;

import NetworkedPhysics.Common.NetworkPhysicsListener;
import NetworkedPhysics.Common.PhysicsInput;
import NetworkedPhysics.Common.Protocol.ServerCommand;
import NetworkedPhysics.Common.Protocol.UserCommand;
import NetworkedPhysics.Common.Protocol.serverCommands.Manipulations.WorldManipulation;
import NetworkedPhysics.Common.RewindablePhysicsWorld;
import NetworkedPhysics.Network.Message;
import NetworkedPhysics.Network.UDPServer;
import NetworkedPhysics.Network.UDPServerListener;
import NetworkedPhysics.Network.nettyUDP.NettyUDPServer;

import java.util.logging.Level;
import java.util.logging.Logger;

import static NetworkedPhysics.Common.Protocol.Protocol.userCommands;

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
//    public void setClientInput(PhysicsInput clientInput) {
//        SetInput setInput = new SetInput(networkWorld.getStep() + 1, clientInput);
//        super.addManipulation(setInput);
//        sendToAll(setInput);
//    }


    @Override
    public void addManipulation(WorldManipulation worldManipulation) {
        udpServer.sendToAll(worldManipulation);
        super.addManipulation(worldManipulation);
    }

    public void run() {
        running = true;

        while (running) {
            stepToActualFrame();
        }
    }

    public void clientInput(PhysicsInput clientInput, int from) {
        physicsListener.clientInput(clientInput, from);
    }

    public void sendTo(int id, ServerCommand command) {
        udpServer.send(id, command);
    }

    @Override
    public void newClient(int id) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "new UDP Client");
        physicsListener.newClient(id);
    }

    @Override
    public void disconnected(int id) {

    }

    @Override
    public void newMessage(int fromId, Message message) {
        Class<? extends UserCommand> aClass = userCommands.get(message.getCommandCode());
        if (aClass == null) {
            Logger.getGlobal().log(Level.INFO, "No Protocol entry found for: " + message.getCommandCode());
            return;
        }
        try {
            UserCommand userCommand = ((UserCommand) aClass.newInstance().fromBlob(message.getPacket()));
            userCommand.processMessage(this, fromId);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void shutDown() {
        udpServer.stop();
    }
}
