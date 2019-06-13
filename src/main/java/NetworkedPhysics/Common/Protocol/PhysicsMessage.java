package NetworkedPhysics.Common.Protocol;

import NetworkedPhysics.Common.NetworkedPhysics;
import NetworkedPhysics.Network.UdpConnection;

public abstract class PhysicsMessage {

    public int stamp;

    protected PhysicsMessage(){}

    protected PhysicsMessage(int stamp) {
        this.stamp = stamp;
    }

    public abstract byte[] toBlob();
    public abstract PhysicsMessage fromBlob(byte[] blob);
    public abstract void processMessage(NetworkedPhysics networkedPhysics, UdpConnection from);

    public abstract byte getCommandID();

    public byte[] toByteMessage(){
        byte[] message = toBlob();
        byte[] packet=new byte[message.length+1];
        packet[0]= getCommandID();
        System.arraycopy(message,0,packet,1,message.length);
        return packet;
    }
}
