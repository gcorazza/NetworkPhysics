package NetworkedPhysics.Server;

import NetworkedPhysics.Common.*;
import NetworkedPhysics.Common.Protocol.Manipulations.AddRigidBody;
import NetworkedPhysics.Common.Protocol.Manipulations.SetInput;
import NetworkedPhysics.Common.Protocol.PhysicsMessage;
import NetworkedPhysics.Common.Protocol.Dto.NetworkedPhysicsObjectDto;
import NetworkedPhysics.Network.IncomingPacketHandlerServer;
import NetworkedPhysics.Network.UdpConnection;
import NetworkedPhysics.Network.UdpSocket;
import io.netty.util.concurrent.Future;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NetworkedPhysicsServer extends RewindablePhysicsWorld implements Runnable {

    Map<InetSocketAddress, UdpConnection> clients = new HashMap<>();
    private UdpSocket connection;

    public NetworkedPhysicsServer(int port, NetworkPhysicsListener networkPhysicsListener) {
        super(networkPhysicsListener);
        connection= new UdpSocket(port, new IncomingPacketHandlerServer(this));
    }

    public Map<InetSocketAddress, UdpConnection> getClients() {
        return clients;
    }

    public int getStepsPerSecond() {
        return networkWorld.getStepsPerSecond();
    }

    public long getStartTime() {
        return networkWorld.getStartTime();
    }

    //calledByServer
    public void setClientInput(PhysicsInput clientInput) {
        SetInput setInput = new SetInput(networkWorld.getFrame() + 1, clientInput);
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

    public void addNetworkedPhysicsObjectNow(NetworkedPhysicsObjectDto networkedPhysicsObjectDto) {
        AddRigidBody message = new AddRigidBody(networkWorld.getFrame() + 1, networkedPhysicsObjectDto);
        sendToAll(message);
        addManipulation(message);
    }

    public void run() {
        running=true;

        while (running) {
            stepToActualFrame();
        }
    }

    public Future<?> shutDown() {
        return connection.shutdown();
    }
}
