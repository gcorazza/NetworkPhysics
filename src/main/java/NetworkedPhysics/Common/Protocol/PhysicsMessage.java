package NetworkedPhysics.Common.Protocol;

import NetworkedPhysics.Common.NetworkedPhysics;
import NetworkedPhysics.Network.UdpConnection;

import java.net.InetSocketAddress;

public abstract class PhysicsMessage {

    public final int stamp;

    protected PhysicsMessage(int stamp) {
        this.stamp = stamp;
    }

    public abstract byte[] toBlob();
    public abstract PhysicsMessage fromBlob(byte[] blob);
    public abstract void processMessage(NetworkedPhysics networkedPhysics, UdpConnection from);

    public abstract byte getMessageID();

    public byte[] toByteMessage(){
        byte[] message = toBlob();
        byte[] packet=new byte[message.length+1];
        packet[0]=getMessageID();
        System.arraycopy(message,0,packet,1,message.length);
        return packet;
    }
}
