package NetworkedPhysics.Network;

public interface UDPServerListener extends UDPConnectionListener {
    void newClient(int id);
}
