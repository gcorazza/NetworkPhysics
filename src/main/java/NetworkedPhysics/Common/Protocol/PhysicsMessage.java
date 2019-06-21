package NetworkedPhysics.Common.Protocol;

import NetworkedPhysics.Network.Message;

public interface PhysicsMessage extends Message {

    PhysicsMessage fromBlob(byte[] blob);

//
//    public byte[] toByteMessage(){
//        byte[] message = toBlob();
//        byte[] packet=new byte[message.length+1];
//        packet[0]= getCommandCode();
//        System.arraycopy(message,0,packet,1,message.length);
//        return packet;
//    }
//

}
