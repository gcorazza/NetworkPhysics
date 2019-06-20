package NetworkedPhysics.Common;

import javax.vecmath.Vector3f;
import java.io.Serializable;

public class PhysicsInput implements Serializable {
    int objId;

    public PhysicsInput(int objId) {
        this.objId = objId;
    }

    boolean click;
    transient boolean lastClicked;

    public void update(NetworkPhysicsWorld physicsWorld){
        PhysicsObject object = physicsWorld.getObject(objId);
        if (object == null) {
            return;
        }

        if (click){
            if(!lastClicked){
                lastClicked=true;
                object.getBody().setLinearVelocity(new Vector3f(10,-10,10));
            }
        }else{
            lastClicked=false;
        }

    }
}
