package NetworkedPhysics.Common.Protocol;

import NetworkedPhysics.Client.NetworkedPhysicsClient;
import NetworkedPhysics.Common.NetworkedPhysics;
import NetworkedPhysics.Server.NetworkedPhysicsServer;
import com.google.gson.Gson;

import java.net.InetSocketAddress;

public class InitialState extends PhysicsMessage {
    public static final byte COMMANDID=0;
    int timePassed;
    int stepsPerSecond;

    public InitialState() {
    }

    public InitialState(InetSocketAddress from, NetworkedPhysics networkedPhysics) {
        timePassed= (int) (System.currentTimeMillis()-networkedPhysics.getStartTime());
        stepsPerSecond= networkedPhysics.getStepsPerSecond();
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
    public void processMessage(NetworkedPhysics networkedPhysics, InetSocketAddress from) {
        ((NetworkedPhysicsClient) networkedPhysics).init(timePassed, stepsPerSecond);
    }

    @Override
    public byte getMessageID() {
        return COMMANDID;
    }
}
