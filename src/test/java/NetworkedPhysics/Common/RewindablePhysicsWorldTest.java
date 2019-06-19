package NetworkedPhysics.Common;

import NetworkedPhysics.Common.Protocol.Dto.NetworkedPhysicsObjectDto;
import NetworkedPhysics.Common.Protocol.Shape;
import NetworkedPhysics.Common.Protocol.WorldState;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

class RewindablePhysicsWorldTest {

    @Test
    public void testRewindOnStateIfItIsDeterministic() {
        NetworkPhysicsListenerAdapter updateInputs = new NetworkPhysicsListenerAdapter() {
        };
        RewindablePhysicsWorld world = new RewindablePhysicsWorld(updateInputs);

        NetworkedPhysicsObjectDto body = lameCube();
        world.addNetworkedPhysicsObjectNow(body);
        stepWorld100Times(world,12);

        WorldState worldState = world.getWorldState();
        stepWorld100Times(world,0);
        System.out.println(Util.gson.toJson(world.getObject(0).bodyToDto()));
        world.restore(worldState);
        stepWorld100Times(world,0);
        System.out.println(Util.gson.toJson(world.getObject(0).bodyToDto()));
    }

    private NetworkedPhysicsObjectDto lameCube() {
        ObjectState objectState = new ObjectState();
        return new NetworkedPhysicsObjectDto(Shape.CUBE, 0.5f, 1, 1, objectState);
    }

    private void stepWorld100Times(RewindablePhysicsWorld world, int id) {
        for (int i = 0; i < 10; i++) {
            world.step();
            PhysicsObject object = world.getObject(id);
            if (object != null) {
                System.out.println(i+ " " + object.bodyToDto().objectState.getOrigin());
            }
        }
    }

    @Test
    void testRestore() {
        RewindablePhysicsWorld world = new RewindablePhysicsWorld(new NetworkPhysicsListenerAdapter(){
            @Override
            public void newObject(int physicsObject) {
                System.out.println("new object");
            }
        });
        world.addNetworkedPhysicsObjectNow(lameCube());
        world.step();
        System.out.println("----STart----");
        world.getObject(0).print();
        WorldState worldState = world.getWorldState();
        System.out.println(new String(worldState.toBlob()));


        System.out.println("----stepped----");
        world.step(); //
        world.getObject(0).print();

        world.restore(worldState);
        System.out.println("--restored");
        world.getObject(0).print();
        world.step();  //
        world.getObject(0).print();

    }
}
