package NetworkedPhysics.Common;

import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.vecmath.Vector3f;

public class Util {


    public static DiscreteDynamicsWorld getWorld(){
        DefaultCollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
        CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
        Vector3f worldAabbMin = new Vector3f(-10000,-10000,-10000);
        Vector3f worldAabbMax = new Vector3f(10000,10000,10000);
        AxisSweep3 overlappingPairCache = new AxisSweep3(worldAabbMin, worldAabbMax);
        SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
        DiscreteDynamicsWorld dynamicWorld = new DiscreteDynamicsWorld(dispatcher, overlappingPairCache, solver, collisionConfiguration);
        dynamicWorld.setGravity(new Vector3f(0,-10,0));
        dynamicWorld.getDispatchInfo().allowedCcdPenetration = 0f;

        return dynamicWorld;
    }
    public static Gson gson = new Gson();
    public static Gson gsonPretty = new GsonBuilder().setPrettyPrinting().create();
}
