package NetworkedPhysics.Client;

import NetworkedPhysics.Common.PhysicsInput;
import NetworkedPhysics.Common.Protocol.clientCommands.ClientInput;
import NetworkedPhysics.Common.Protocol.clientCommands.GetWorldState;
import NetworkedPhysics.Common.NetworkPhysicsListener;
import NetworkedPhysics.Common.Protocol.serverCommands.WorldState;
import NetworkedPhysics.Common.RewindablePhysicsWorld;
import NetworkedPhysics.Common.Dto.NetworkedPhysicsObjectDto;
import NetworkedPhysics.Network.Message;
import NetworkedPhysics.Network.UDPClient;
import NetworkedPhysics.Network.UDPClientListener;
import NetworkedPhysics.Network.nettyUDP.NettyUDPClient;

import java.net.InetSocketAddress;


public class NetworkedPhysicsClient extends RewindablePhysicsWorld implements Runnable, UDPClientListener {

    UDPClient clientSocket= new NettyUDPClient();

    public NetworkedPhysicsClient(InetSocketAddress socketAddress, NetworkPhysicsListener updateInputsCallback) {
        super(updateInputsCallback);
        clientSocket.connect(socketAddress);
        clientSocket.send(new GetWorldState());
    }


    public void wishAddObject(NetworkedPhysicsObjectDto o){

    }

    public void wishDeleteObject(NetworkedPhysicsObjectDto o){

    }

    public void sendMyInput(PhysicsInput myInput) {
        clientSocket.send(new ClientInput(myInput));
    }

    public void run() {
        running=true;

        while(running){
            stepToActualFrame();
        }
    }

    @Override
    public void newMessage(Message message) {

    }

    public void setWorldState(WorldState worldState) {

    }
}
