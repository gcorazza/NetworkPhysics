package NetworkedPhysics.Cereal;

import NetworkedPhysics.Common.Protocol.clientCommands.InputArguments;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class InputArgumentsCereal implements Cereal<InputArguments> {

    public static final InputArgumentsCereal inputArgumentsCereal = new InputArgumentsCereal();

    @Override
    public DataOutputStream put(InputArguments inputArguments, DataOutputStream out) throws IOException {
        out.writeBoolean(inputArguments.click);
        out.writeBoolean(inputArguments.spawnClick);
        return out;
    }

    @Override
    public InputArguments get(DataInputStream in) throws IOException {
        InputArguments inputArguments = new InputArguments();
            inputArguments.click = in.readBoolean();
            inputArguments.spawnClick = in.readBoolean();
        return inputArguments;
    }
}
