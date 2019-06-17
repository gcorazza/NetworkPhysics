package NetworkedPhysics.Common;

import NetworkedPhysics.Common.Protocol.Dto.NetworkedPhysicsObjectDto;
import NetworkedPhysics.Common.Protocol.Shape;
import org.junit.jupiter.api.Test;

class NetworkedPhysicsTest {

    @Test
    public void testRewindOnStateIfItIsDeterministic(){
        NetworkedPhysics networkedPhysics = new NetworkedPhysics(new NetworkPhysicsListenerAdapter());
        ObjectState objectState = new ObjectState();
        NetworkedPhysicsObjectDto body = new NetworkedPhysicsObjectDto(0, Shape.CUBE, 0.5f, 1, 1, objectState);
        networkedPhysics.addNetworkedPhysicsObjectNow(body);
    }

}
