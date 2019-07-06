package NetworkedPhysics;

import NetworkedPhysics.Common.Dto.NetworkedPhysicsObjectDto;
import NetworkedPhysics.Common.NetworkPhysicsListener;
import NetworkedPhysics.Common.PhysicsInput;
import NetworkedPhysics.Common.Protocol.Protocol;
import NetworkedPhysics.Common.Protocol.ServerCommand;
import NetworkedPhysics.Common.Protocol.clientCommands.InputArguments;
import NetworkedPhysics.Common.Protocol.serverCommands.Manipulations.WorldManipulation;
import NetworkedPhysics.Common.Protocol.serverCommands.WorldState;
import NetworkedPhysics.Common.RewindablePhysicsWorld;
import NetworkedPhysics.Network.Message;
import NetworkedPhysics.Network.UDPClient;
import NetworkedPhysics.Network.UDPConnectionListener;
import NetworkedPhysics.Network.nettyUDP.NettyUDPClient;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class NetworkedPhysicsClient implements Runnable, UDPConnectionListener {

    private final InetSocketAddress socketAddress;
    UDPClient clientSocket = new NettyUDPClient(this);
    private List<ServerCommand> messages = Collections.synchronizedList(new ArrayList<>());
    private RewindablePhysicsWorld rewindableWorld;
    private WorldState remoteWorldState;

    public NetworkedPhysicsClient(InetSocketAddress socketAddress, NetworkPhysicsListener physicsListener) {
        rewindableWorld = new RewindablePhysicsWorld(physicsListener);
        this.socketAddress = socketAddress;
        clientSocket.connect(socketAddress);
    }

    public void wishAddObject(NetworkedPhysicsObjectDto o) {

    }

    public void wishDeleteObject(NetworkedPhysicsObjectDto o) {

    }

    public void sendMyInput(InputArguments myInput) {
        clientSocket.send(myInput);
    }

    public void run() {
        clientSocket.connect(socketAddress);
    }

    @Override
    public void newMessage(int fromId, Message message) {
        System.out.println(new String(message.getPacket()));
        ServerCommand command = Protocol.getServerCommand(message);
        if (command != null) {
            System.out.println(new String(command.getPacket()));
            messages.add(command);
        }
    }

    @Override
    public void disconnected(int id) {

    }

    public void setRemoteWorldState(WorldState worldState) {
        remoteWorldState = worldState;
        rewindableWorld.restore(remoteWorldState);
    }

    public int update() {
        processMessages();
        rewindableWorld.stepToActualFrame();
        return rewindableWorld.getStep();
    }

    private void processMessages() {
        int size = messages.size();
        for (int i = 0; i < size; i++) {
            messages.remove(0).processMessage(this);
        }
    }

    public void addRemoteManipulation(WorldManipulation manipulation) {
        rewindableWorld.addManipulation(manipulation);
    }

    public RewindablePhysicsWorld getRewindableWorld() {
        return rewindableWorld;
    }
}
