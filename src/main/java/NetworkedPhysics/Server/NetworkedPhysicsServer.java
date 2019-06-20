package NetworkedPhysics.Server;

import NetworkedPhysics.Common.NetworkPhysicsListener;
import NetworkedPhysics.Common.PhysicsInput;
import NetworkedPhysics.Common.Protocol.Manipulations.WorldManipulation;
import NetworkedPhysics.Common.Protocol.PhysicsMessage;
import NetworkedPhysics.Common.RewindablePhysicsWorld;
import NetworkedPhysics.Network.IncomingPacketHandlerServer;
import NetworkedPhysics.Network.UdpConnection;
import NetworkedPhysics.Network.UdpSocket;

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
//    public void setClientInput(PhysicsInput clientInput) {
//        SetInput setInput = new SetInput(networkWorld.getStep() + 1, clientInput);
//        super.addManipulation(setInput);
//        sendToAll(setInput);
//    }

    private void sendToAll(PhysicsMessage message) {
        clients.keySet().forEach( c -> connection.send(message, c));
    }

    public void newUDPClient(UdpConnection udpConnection) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "new UDP Client");
        clients.put(udpConnection.inetSocketAddress,udpConnection);
        physicsListener.newClient(udpConnection.inetSocketAddress);
    }

    public void setStepsPerSecound(int stepsPerSecound) {
        //sync with all
    }

    @Override
    public void addManipulation(WorldManipulation worldManipulation) {
        sendToAll(worldManipulation);
        super.addManipulation(worldManipulation);
    }

    public void run() {
        running=true;

        while (running) {
            stepToActualFrame();
        }
    }

    public void shutDown() {
        connection.shutdown();
    }

    public void clientInput(PhysicsInput clientInput, UdpConnection from) {
        physicsListener.clientInput(clientInput, from.inetSocketAddress);
    }
}
