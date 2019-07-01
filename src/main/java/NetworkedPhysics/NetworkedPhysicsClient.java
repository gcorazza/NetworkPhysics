package NetworkedPhysics;

import NetworkedPhysics.Common.Dto.NetworkedPhysicsObjectDto;
import NetworkedPhysics.Common.NetworkPhysicsListener;
import NetworkedPhysics.Common.PhysicsInput;
import NetworkedPhysics.Common.Protocol.Protocol;
import NetworkedPhysics.Common.Protocol.ServerCommand;
import NetworkedPhysics.Common.Protocol.clientCommands.ClientInput;
import NetworkedPhysics.Common.Protocol.clientCommands.GetWorldState;
import NetworkedPhysics.Common.RewindablePhysicsWorld;
import NetworkedPhysics.Network.Message;
import NetworkedPhysics.Network.UDPClient;
import NetworkedPhysics.Network.UDPConnectionListener;
import NetworkedPhysics.Network.nettyUDP.NettyUDPClient;

import java.net.InetSocketAddress;


public class NetworkedPhysicsClient extends RewindablePhysicsWorld implements Runnable, UDPConnectionListener {

    private final InetSocketAddress socketAddress;
    UDPClient clientSocket = new NettyUDPClient(this);

    public NetworkedPhysicsClient(InetSocketAddress socketAddress, NetworkPhysicsListener updateInputsCallback) {
        super(updateInputsCallback);
        this.socketAddress = socketAddress;
    }

    public void wishAddObject(NetworkedPhysicsObjectDto o) {

    }

    public void wishDeleteObject(NetworkedPhysicsObjectDto o) {

    }

    public void sendMyInput(PhysicsInput myInput) {
        clientSocket.send(new ClientInput(myInput));
    }

    public void run() {
        running = true;
        clientSocket.connect(socketAddress);
        while (running) {
            stepToActualFrame();
        }
    }

    @Override
    public void newMessage(int fromId, Message message) {
        ServerCommand command=Protocol.getServerCommand(message);
        if (command != null) {
            command.processMessage(this);
        }
    }

    @Override
    public void disconnected(int id) {

    }
}
