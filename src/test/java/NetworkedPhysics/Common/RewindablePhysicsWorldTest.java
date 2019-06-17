package NetworkedPhysics.Common;

import NetworkedPhysics.Common.Protocol.Dto.NetworkedPhysicsObjectDto;
import NetworkedPhysics.Common.Protocol.Shape;
import org.junit.jupiter.api.Test;

class RewindablePhysicsWorldTest {

    @Test
    public void testRewindOnStateIfItIsDeterministic(){
        RewindablePhysicsWorld rewindablePhysicsWorld = new RewindablePhysicsWorld(new NetworkPhysicsListenerAdapter());
        ObjectState objectState = new ObjectState();
        NetworkedPhysicsObjectDto body = new NetworkedPhysicsObjectDto(Shape.CUBE, 0.5f, 1, 1, objectState);
        rewindablePhysicsWorld.addNetworkedPhysicsObjectNow(body);
    }

}
