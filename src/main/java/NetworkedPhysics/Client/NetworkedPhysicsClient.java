package NetworkedPhysics.Client;

import NetworkedPhysics.Common.PhysicsInput;
import NetworkedPhysics.Common.Protocol.ClientInput;
import NetworkedPhysics.Common.Protocol.GetWorldState;
import NetworkedPhysics.Common.Protocol.PhysicsMessage;
import NetworkedPhysics.Common.NetworkPhysicsListener;
import NetworkedPhysics.Common.RewindablePhysicsWorld;
import NetworkedPhysics.Common.Protocol.Dto.NetworkedPhysicsObjectDto;
import NetworkedPhysics.Network.IncommingPacketHandlerClient;
import NetworkedPhysics.Network.UdpConnection;
import NetworkedPhysics.Network.UdpSocket;

import java.net.InetSocketAddress;


public class NetworkedPhysicsClient extends RewindablePhysicsWorld implements Runnable{

    private UdpConnection serverConnection;
    private UdpSocket connection;

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
        sendToServer(new ClientInput(myInput));
    }

    public void run() {
        running=true;

        while(running){
            stepToActualFrame();
        }
    }

    public void init(int timePassed, int stepsPerSecond) {

    }

    public void sendTo(UdpConnection receiver, PhysicsMessage message) {
        message.stamp = receiver.nextStamp();
        connection.send(message, receiver.inetSocketAddress);
    }

    public void sendToServer(PhysicsMessage physicsMessage) {
        sendTo(serverConnection, physicsMessage);
    }

    public UdpConnection getServerConnection() {
        return serverConnection;
    }
}
