package NetworkedPhysics.Client;

import NetworkedPhysics.Common.PhysicsInput;
import NetworkedPhysics.Common.Protocol.ClientInput;
import NetworkedPhysics.Common.Protocol.GetWorldState;
import NetworkedPhysics.Common.Protocol.PhysicsMessage;
import NetworkedPhysics.Common.NetworkPhysicsListener;
import NetworkedPhysics.Common.NetworkedPhysics;
import NetworkedPhysics.Common.Util;
import NetworkedPhysics.Common.Protocol.Dto.NetworkedPhysicsObjectDto;
import NetworkedPhysics.Network.IncommingPacketHandlerClient;
import NetworkedPhysics.Network.UdpConnection;
import NetworkedPhysics.Network.UdpSocket;

import java.net.InetSocketAddress;


public class NetworkedPhysicsClient extends NetworkedPhysics implements Runnable{

    private UdpConnection serverConnection;

    public NetworkedPhysicsClient(InetSocketAddress socketAddress, NetworkPhysicsListener updateInputsCallback) {
        super(updateInputsCallback);
        connection = new UdpSocket(new IncommingPacketHandlerClient(this));
        connection.connect(socketAddress).awaitUninterruptibly();
        serverConnection= new UdpConnection(socketAddress);
        connection.send(new GetWorldState(serverConnection.nextStamp()));
    }


    public void wishAddObject(NetworkedPhysicsObjectDto o){

    }

    public void wishDeleteObject(NetworkedPhysicsObjectDto o){

    }

    public void sendMyInput(PhysicsInput myInput) {
        sendTo(new ClientInput(myInput));
    }

    public void run() {
        running=true;

        while(running){
            stepToActualFrame();
        }
    }

    public void init(int timePassed, int stepsPerSecond) {

    }

    public void sendTo(PhysicsMessage physicsMessage) {
        sendTo(serverConnection, physicsMessage);
    }

    public UdpConnection getServerConnection() {
        return serverConnection;
    }
}
