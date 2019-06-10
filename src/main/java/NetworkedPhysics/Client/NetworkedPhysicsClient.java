package NetworkedPhysics.Client;

import NetworkedPhysics.Client.SyncActions.SyncAction;
import NetworkedPhysics.Common.Protocol.GetInit;
import NetworkedPhysics.Common.Protocol.PhysicsMessage;
import NetworkedPhysics.Common.UpdateInputsCallback;
import NetworkedPhysics.Network.ClientInput;
import NetworkedPhysics.Common.NetworkedPhysics;
import NetworkedPhysics.Common.Util;
import NetworkedPhysics.Common.NetworkedPhysicsObject;
import NetworkedPhysics.Network.IncommingPacketHandlerClient;
import NetworkedPhysics.Network.UdpConnection;
import NetworkedPhysics.Network.UdpSocket;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class NetworkedPhysicsClient extends NetworkedPhysics implements Runnable{

    List<SyncAction> timeline= new ArrayList<SyncAction>();

    private UdpConnection serverConnection;


    public NetworkedPhysicsClient(InetSocketAddress socketAddress, UpdateInputsCallback updateInputsCallback) {
        super(updateInputsCallback);
        connection = new UdpSocket(new IncommingPacketHandlerClient(this));
        connection.connect(socketAddress).awaitUninterruptibly();
        serverConnection= new UdpConnection(socketAddress);
        connection.send(new GetInit(serverConnection.getMessageStap()));
    }


    public void wishAddObject(NetworkedPhysicsObject o){

    }

    public void wishDeleteObject(NetworkedPhysicsObject o){

    }

    public void addSyncAction(SyncAction syncAction){
        timeline.add(syncAction);
        //rewind to syncAction.frame. / optimize simulation buffer
    }

    public void sendClientInput(ClientInput clientInput) {

    }

    private void rewind(int frame){

    }



    public void run() {
        running=true;

        while(running){
            stepToActualFrame();
        }
    }

    public void init(int timePassed, int stepsPerSecond) {
        world = Util.getWorld();
        startTime = System.currentTimeMillis()-timePassed;
        frame= (int) (1f/1000*timePassed*stepsPerSecond);
    }

    @Override
    public void update() {

    }

    public void send(PhysicsMessage physicsMessage) {
        send(physicsMessage, serverConnection);
    }

    public UdpConnection getServerConnection() {
        return serverConnection;
    }
}
