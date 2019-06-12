package NetworkedPhysics.Common.Protocol;

import NetworkedPhysics.Client.NetworkedPhysicsClient;
import NetworkedPhysics.Common.NetworkedPhysics;
import NetworkedPhysics.Network.UdpConnection;
import com.google.gson.Gson;

public class InitPhysicsEngine extends PhysicsMessage {
    public static final byte COMMANDID=0;
    int timePassed;
    int stepsPerSecond;

    public InitPhysicsEngine(int stamp) {
        super(stamp);
    }

    public InitPhysicsEngine(NetworkedPhysics networkedPhysics, int messageStamp) {
        super(messageStamp);
        timePassed= (int) (System.currentTimeMillis()-networkedPhysics.getStartTime());
        stepsPerSecond= networkedPhysics.getStepsPerSecond();
    }


    @Override
    public byte[] toBlob() {
        Gson gson = new Gson();
        return gson.toJson(this).getBytes();
    }

    @Override
    public InitPhysicsEngine fromBlob(byte[] blob) {
        Gson gson = new Gson();
        return gson.fromJson(new String(blob), InitPhysicsEngine.class);
    }

    @Override
    public void processMessage(NetworkedPhysics networkedPhysics, UdpConnection from) {
        ((NetworkedPhysicsClient) networkedPhysics).init(timePassed, stepsPerSecond);
    }

    @Override
    public byte getCommandID() {
        return COMMANDID;
    }
}
