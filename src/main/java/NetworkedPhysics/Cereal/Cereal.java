package NetworkedPhysics.Cereal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;


public interface Cereal<V> {
    DataOutputStream put(V v, DataOutputStream out) throws IOException;
//    DataOutputStream put(List<V> v, DataOutputStream out);
    V get(DataInputStream in) throws IOException;
//    List<V> get(DataInputStream in);
}
