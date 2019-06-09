package NetworkedPhysics.Common.Protocol;

import NetworkedPhysics.Common.NetworkedPhysics;

import java.net.InetSocketAddress;

public abstract class PhysicsMessage {

    public abstract byte[] toBlob();
    public abstract PhysicsMessage fromBlob(byte[] blob);
    public abstract void processMessage(NetworkedPhysics networkedPhysics, InetSocketAddress from);
    public abstract byte getMessageID();

    public byte[] toByteMessage(){
        byte[] message = toBlob();
        byte[] packet=new byte[message.length+1];
        packet[0]=getMessageID();
        System.arraycopy(message,0,packet,1,message.length);
        return packet;
    }
}
