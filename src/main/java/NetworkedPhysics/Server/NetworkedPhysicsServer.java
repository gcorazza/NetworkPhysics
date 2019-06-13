package NetworkedPhysics.Server;

import NetworkedPhysics.Common.NetworkPhysicsListener;
import NetworkedPhysics.Common.NetworkedPhysics;
import NetworkedPhysics.Common.PhysicsInput;
import NetworkedPhysics.Common.Protocol.Manipulations.SetInput;
import NetworkedPhysics.Common.Protocol.PhysicsMessage;
import NetworkedPhysics.Common.Util;
import NetworkedPhysics.Common.Protocol.Dto.NetworkedPhysicsObjectDto;
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

    public NetworkedPhysicsServer(int port, NetworkPhysicsListener updateInputs) {
        super(updateInputs);
        connection= new UdpSocket(port, new IncomingPacketHandlerServer(this));
        world = Util.getWorld();
        startTime= System.currentTimeMillis();
    }

    public Map<InetSocketAddress, UdpConnection> getClients() {
        return clients;
    }

    @Override
    public void update() {
        stepToActualFrame();
    }

    //calledByServer
    public void setClientInput(PhysicsInput clientInput) {
        SetInput setInput = new SetInput(frame + 1, clientInput);
        super.addManipulation(setInput);
        sendToAll(setInput);
    }

    private void sendToAll(PhysicsMessage message) {
        clients.keySet().forEach( c -> connection.send(message, c));
    }



    public void newUDPClient(UdpConnection udpConnection) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "new UDP Client");
    }

    public void setStepsPerSecound(int stepsPerSecound) {
        //sync with all
    }

    public void addNetworkedPhysicsObject(NetworkedPhysicsObjectDto networkedPhysicsObjectDto) {

    }

    public void run() {
        running=true;

        while (running) {
            stepToActualFrame();
        }
    }

}
