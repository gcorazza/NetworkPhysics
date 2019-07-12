package NetworkedPhysics.Common.Protocol;

import NetworkedPhysics.Network.Message;

import java.io.IOException;

public interface PhysicsMessage extends Message {
    PhysicsMessage fromBlob(byte[] blob);
}
