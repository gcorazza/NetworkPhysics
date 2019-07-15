package NetworkedPhysics.Cereal;

import java.io.DataOutputStream;
import java.io.IOException;

public interface Putter {
    void putTo(DataOutputStream out) throws IOException;
}
