package NetworkedPhysics.Common;

import NetworkedPhysics.Common.Dto.NetworkedPhysicsObjectDto;
import NetworkedPhysics.Common.Dto.Shape;
import NetworkedPhysics.Common.Protocol.serverCommands.Manipulations.AddRigidBody;
import NetworkedPhysics.Common.Protocol.serverCommands.WorldState;
import org.junit.jupiter.api.Test;

import static Util.Utils.gson;

class RewindablePhysicsWorldTest {

    @Test
    public void testRewindOnStateIfItIsDeterministic() {
        NetworkPhysicsListenerAdapter updateInputs = new NetworkPhysicsListenerAdapter() {
        };
        RewindablePhysicsWorld world = new RewindablePhysicsWorld(updateInputs);

        NetworkedPhysicsObjectDto body = lameCube();
        world.addNetworkedPhysicsObjectNow(body,10);
        stepWorld100Times(world,12);

        WorldState worldState = world.saveState();
        stepWorld100Times(world,0);
        System.out.println(gson.toJson(world.getObject(10).bodyToDto()));
        world.restore(worldState);
        stepWorld100Times(world,0);
        System.out.println(gson.toJson(world.getObject(10).bodyToDto()));
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
        int physicsObjectId = 10;
        world.addNetworkedPhysicsObjectNow(lameCube(), physicsObjectId);
        world.step();
        System.out.println("----STart----");
        world.getObject(physicsObjectId).print();
        WorldState worldState = world.saveState();
        System.out.println(new String(worldState.getPacket()));


        System.out.println("----stepped----");
        world.step(); //
        world.getObject(physicsObjectId).print();

        world.restore(worldState);
        System.out.println("--restored");
        world.getObject(physicsObjectId).print();
        world.step();  //
        world.getObject(physicsObjectId).print();

    }

    @Test
    void should_be_deterministic_when_restored_and_when_manipulated() {
        RewindablePhysicsWorld world1 = new RewindablePhysicsWorld(new NetworkPhysicsListenerAdapter());
        RewindablePhysicsWorld world2 = new RewindablePhysicsWorld(new NetworkPhysicsListenerAdapter());
        world1.addNetworkedPhysicsObjectNow(lameCube(), 11);
        world1.addNetworkedPhysicsObjectNow(lameCube(), 12);
        world1.step();
        WorldState world1State = world1.saveState();
        world1.restore(world1State);
        world2.restore(world1State);
        AddRigidBody manipulation = world1.addNetworkedPhysicsObjectNow(lameCube(), 13);
        world2.addManipulation(manipulation);

        world1.step();

        world2.step();

        System.out.println(world1);
        world1.equals(world2);
    }
}
