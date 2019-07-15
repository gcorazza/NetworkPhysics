package NetworkedPhysics.Cereal;

import NetworkedPhysics.Common.Dto.NetworkedPhysicsObjectDto;
import NetworkedPhysics.Common.Dto.Shape;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static NetworkedPhysics.Cereal.ObjectStateCereal.objectStateCereal;

public class NetworkedPhysicsObjectDtoCereal implements Cereal<NetworkedPhysicsObjectDto> {

    public static final NetworkedPhysicsObjectDtoCereal networkedPhysicsObjectDtoCereal = new NetworkedPhysicsObjectDtoCereal();

    private NetworkedPhysicsObjectDtoCereal() {
    }

    @Override
    public DataOutputStream put(NetworkedPhysicsObjectDto dto, DataOutputStream out) throws IOException {
        out.writeInt(dto.shape.ordinal());
        out.writeFloat(dto.a);
        out.writeFloat(dto.b);
        out.writeFloat(dto.c);
        out.writeFloat(dto.mass);
        out.writeFloat(dto.friction);
        out.writeFloat(dto.restitution);
        objectStateCereal.put(dto.objectState, out);
        return out;
    }

    @Override
    public NetworkedPhysicsObjectDto get(DataInputStream in) throws IOException {
        return new NetworkedPhysicsObjectDto(
                Shape.values()[in.readInt()],
                in.readFloat(),
                in.readFloat(),
                in.readFloat(),
                in.readFloat(),
                in.readFloat(),
                in.readFloat(),
                objectStateCereal.get(in));
    }
}
