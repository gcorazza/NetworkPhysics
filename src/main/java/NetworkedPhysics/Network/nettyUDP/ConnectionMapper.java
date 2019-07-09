package NetworkedPhysics.Network.nettyUDP;

import NetworkedPhysics.Network.UDPConnectionListener;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class ConnectionMapper {
    private Map<Integer, UdpConnection> idMap= new HashMap<>();
    private Map<InetSocketAddress, UdpConnection> iNetMap= new HashMap<>();
    private int clientIdCounter;
    private final UdpSocket udpSocket;
    private UDPConnectionListener listener;

    ConnectionMapper(UdpSocket udpSocket, UDPConnectionListener listener) {
        this.udpSocket = udpSocket;
        this.listener = listener;
    }

    public UdpConnection newConnection(InetSocketAddress address){
        int id = newClientId();
        UdpConnection connection = new UdpConnection(address, id, udpSocket, listener);
        idMap.put(id,connection);
        iNetMap.put(address, connection);
        return connection;
    }

    public UdpConnection get(InetSocketAddress address){
        return iNetMap.get(address);
    }

    public UdpConnection get(int id){
        return idMap.get(id);
    }

    public void deleteConnection(InetSocketAddress address){
        UdpConnection connection = iNetMap.get(address);
        iNetMap.remove(address);
        idMap.remove(connection.id);
    }

    public void deleteConnection(int id){

    }

    private int newClientId() {
        return ++clientIdCounter;
    }

    public Collection<UdpConnection> connections() {
        return idMap.values();
    }
}
