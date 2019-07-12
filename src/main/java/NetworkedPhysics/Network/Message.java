package NetworkedPhysics.Network;

import java.io.IOException;

public interface Message {
    byte getCommandCode();
    byte[] getPacket();
}
