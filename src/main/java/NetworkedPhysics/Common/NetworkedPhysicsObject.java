package NetworkedPhysics.Common;

import NetworkedPhysics.Common.Protocol.Dto.NetworkedPhysicsObjectDto;
import com.bulletphysics.dynamics.RigidBody;

public class NetworkedPhysicsObject {
    NetworkedPhysicsObjectDto dto;
    RigidBody body;
    public final int id;

    public NetworkedPhysicsObject(NetworkedPhysicsObjectDto dto) {
        this.dto = dto;
        body= dto.getRigidBody();
        id= dto.id;
    }

    public RigidBody getBody() {
        return body;
    }

    public NetworkedPhysicsObjectDto getDto() {
        return dto;
    }
}
