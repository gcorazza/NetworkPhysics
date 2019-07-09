package NetworkedPhysics.Common;

import NetworkedPhysics.Common.Protocol.clientCommands.InputArguments;

import javax.vecmath.Vector3f;
import java.io.Serializable;

public class PhysicsInput implements Serializable {
    int objId;
    private InputArguments inputArguments= new InputArguments();

    public PhysicsInput(int objId) {
        this.objId = objId;
    }

    transient boolean lastClicked;

    public void update(NetworkPhysicsWorld physicsWorld){
        PhysicsObject object = physicsWorld.getObject(objId);
        if (object == null) {
            return;
        }

        if (inputArguments.click){
            if(!lastClicked){
                lastClicked=true;
                System.out.println(object.getBody().getLinearVelocity(new Vector3f()));
                object.getBody().activate();
                object.getBody().setLinearVelocity(new Vector3f(0,10, 2));
                System.out.println(object.getBody().getLinearVelocity(new Vector3f()));
                System.out.println(physicsWorld.world.getCollisionObjectArray().contains(object.getBody()));
            }
        }else{
            lastClicked=false;
        }

    }

    public void setInputArguments(InputArguments inputArguments) {
        this.inputArguments = inputArguments;
    }
}
