package NetworkedPhysics.Client;

import NetworkedPhysics.Common.PhysicsInput;
import NetworkedPhysics.Common.Protocol.ClientInput;
import NetworkedPhysics.Common.Protocol.GetInit;
import NetworkedPhysics.Common.Protocol.PhysicsMessage;
import NetworkedPhysics.Common.NetworkPhysicsListener;
import NetworkedPhysics.Common.NetworkedPhysics;
import NetworkedPhysics.Common.Util;
import NetworkedPhysics.Common.Protocol.Dto.NetworkedPhysicsObject;
import NetworkedPhysics.Network.IncommingPacketHandlerClient;
import NetworkedPhysics.Network.UdpConnection;
import NetworkedPhysics.Network.UdpSocket;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;


public class NetworkedPhysicsClient extends NetworkedPhysics implements Runnable{

    private UdpConnection serverConnection;

    public NetworkedPhysicsClient(InetSocketAddress socketAddress, NetworkPhysicsListener updateInputsCallback) {
        super(updateInputsCallback);
        connection = new UdpSocket(new IncommingPacketHandlerClient(this));
        connection.connect(socketAddress).awaitUninterruptibly();
        serverConnection= new UdpConnection(socketAddress);
        connection.send(new GetInit(serverConnection.nextStamp()));
    }


    public void wishAddObject(NetworkedPhysicsObject o){

    }

    public void wishDeleteObject(NetworkedPhysicsObject o){

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
        world = Util.getWorld();
        this.stepsPerSecond=stepsPerSecond;
        startTime = System.currentTimeMillis()-timePassed;
        frame= shouldBeInFrame();
    }

    @Override
    public void update() {
        stepToActualFrame();
    }

    public void sendTo(PhysicsMessage physicsMessage) {
        sendTo(serverConnection, physicsMessage);
    }

    public UdpConnection getServerConnection() {
        return serverConnection;
    }
}
