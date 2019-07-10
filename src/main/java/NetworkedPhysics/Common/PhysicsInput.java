package NetworkedPhysics.Common;

import NetworkedPhysics.Common.Protocol.clientCommands.InputArguments;

import javax.vecmath.Vector3f;
import java.io.Serializable;

public class PhysicsInput implements Serializable {
    public int getObjId() {
        return objId;
    }

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
                object.getBody().activate();
                object.getBody().setLinearVelocity(new Vector3f(0,10, 2));
            }
        }else{
            lastClicked=false;
        }

    }

    public void setInputArguments(InputArguments inputArguments) {
        this.inputArguments = inputArguments;
    }

    @Override
    public boolean equals(Object obj) {
        PhysicsInput input= ((PhysicsInput) obj);
        return objId== input.objId && inputArguments.equals(input.inputArguments);
    }
}
