package NetworkedPhysics.Server;

import NetworkedPhysics.Common.Protocol.PhysicsMessage;
import NetworkedPhysics.Common.UpdateInputsCallback;
import NetworkedPhysics.Network.ClientInput;
import NetworkedPhysics.Common.NetworkedPhysics;
import NetworkedPhysics.Common.Util;
import NetworkedPhysics.Common.NetworkedPhysicsObject;
import NetworkedPhysics.Network.IncomingPacketHandlerServer;
import NetworkedPhysics.Network.UdpConnection;
import NetworkedPhysics.Network.UdpSocket;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NetworkedPhysicsServer extends NetworkedPhysics implements Runnable {

    Map<InetSocketAddress, UdpConnection> clients= new HashMap<>();

    public NetworkedPhysicsServer(int port, UpdateInputsCallback updateInputs) {
        super(updateInputs);
        connection= new UdpSocket(port, new IncomingPacketHandlerServer(this));
        world = Util.getWorld();
    }

    public Map<InetSocketAddress, UdpConnection> getClients() {
        return clients;
    }

    @Override
    public void update() {
        stepToActualFrame();

    }

    //calledByServer
    public void setClientInput(ClientInput clientInput) {

    }

    public void newUDPClient(UdpConnection udpConnection) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "new UDP Client");
    }

    public void setStepsPerSecound(int stepsPerSecound) {
        //sync with all
    }

    public void addNetworkedPhysicsObject(NetworkedPhysicsObject networkedPhysicsObject) {

    }

    public void run() {
        startTime= System.currentTimeMillis();
        running=true;

        while (running) {
            stepToActualFrame();
        }
    }

}
