package NetworkedPhysics.Common.Protocol;

import NetworkedPhysics.Common.NetworkedPhysics;
import com.google.gson.Gson;

import java.net.InetSocketAddress;

public class InitialState extends PhysicsMessage {
    public static final byte COMMANDID=0;
    int timePassed;
    int stepsPerSecond;
    long btSeed;

    public InitialState(InetSocketAddress from, NetworkedPhysics networkedPhysics) {
        super(from, networkedPhysics);
    }


    @Override
    public byte[] toBlob() {
        Gson gson = new Gson();
        return gson.toJson(this).getBytes();
    }

    @Override
    public PhysicsMessage fromBlob(byte[] blob) {
        Gson gson = new Gson();
        return gson.fromJson(new String(blob), InitialState.class);
    }

    @Override
    public void processMessage() {

    }
}
