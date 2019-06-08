package NetworkedPhysics.Client;

import NetworkedPhysics.Client.SyncActions.SyncAction;
import NetworkedPhysics.Common.UpdateInputsCallback;
import NetworkedPhysics.Network.ClientInput;
import NetworkedPhysics.Common.NetworkedPhysics;
import NetworkedPhysics.Common.Util;
import NetworkedPhysics.Common.NetworkedPhysicsObject;
import NetworkedPhysics.Network.IncommingPacketHandlerClient;
import NetworkedPhysics.Network.UdpSocket;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class NetworkedPhysicsClient extends NetworkedPhysics implements Runnable{

    List<SyncAction> timeline= new ArrayList<SyncAction>();

    private InetAddress inetAddress;

    public NetworkedPhysicsClient(InetAddress inetAddress, int port, UpdateInputsCallback updateInputs) {
        super(updateInputs);
        this.inetAddress = inetAddress;
        this.port = port;
        connection = new UdpSocket(8080, new IncommingPacketHandlerClient());
    }



    private void initPhysicsEngine(int timesPassed) {
        world = Util.getWorld();
        startTime = System.currentTimeMillis()-timesPassed;
        frame= (int) (1f/1000*timesPassed*stepsPerSecond);
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
        //getInitialValues
        initPhysicsEngine(0);
        running=true;

        while(running){
            step();
            waitTilNextFrame();
        }
    }
}
