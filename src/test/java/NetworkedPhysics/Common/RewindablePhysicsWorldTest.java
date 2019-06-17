package NetworkedPhysics.Common;

import NetworkedPhysics.Common.Protocol.Dto.NetworkedPhysicsObjectDto;
import NetworkedPhysics.Common.Protocol.Shape;
import org.junit.jupiter.api.Test;

class RewindablePhysicsWorldTest {

    @Test
    public void testRewindOnStateIfItIsDeterministic() {
        final PhysicsObject[] cube = new PhysicsObject[1];
        RewindablePhysicsWorld world = new RewindablePhysicsWorld(new NetworkPhysicsListenerAdapter() {
            @Override
            public void newObject(PhysicsObject physicsObject) {
                cube[0] = physicsObject;
            }
        });
        ObjectState objectState = new ObjectState();
        NetworkedPhysicsObjectDto body = new NetworkedPhysicsObjectDto(Shape.CUBE, 0.5f, 1, 1, objectState);
        world.addNetworkedPhysicsObjectNow(body);
        for (int i = 0; i < 100; i++) {
            world.step();
            if (cube[0] != null)
                System.out.println(cube[0].bodyToDto().objectState.getOrigin());
        }
    }

}
