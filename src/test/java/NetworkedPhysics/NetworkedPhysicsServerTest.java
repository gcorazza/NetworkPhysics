package NetworkedPhysics;

import NetworkedPhysics.Common.Protocol.serverCommands.WorldState;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class NetworkedPhysicsServerTest {

    static Gson gson = new Gson();

    @Test
    void compareTwoServerAndClientStatesAndExpect0Diff() throws FileNotFoundException {
        System.out.println("loading states ...");
        ArrayList<WorldState> clientStates = readStates("_main0.states");
        ArrayList<WorldState> serverStates = readStates("_main.states");
        double[] diffs = new double[clientStates.size()];

        System.out.println("comparing states ...");
        for (int i = 0; i < clientStates.size(); i++) {
            diffs[i]= clientStates.get(i).getDifference(serverStates.get(i));
        }
        for (int i = 0; i < diffs.length; i++) {
            System.out.println(i+";"+diffs[i]);
        }
        assertArrayEquals(new double[clientStates.size()],diffs);
    }

    private ArrayList<WorldState> readStates(String path) throws FileNotFoundException {
        ArrayList<WorldState> states = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
        reader.lines().forEach(l -> states.add(gson.fromJson(l, WorldState.class)));
        return states;
    }

    @Test
    void testDataStream() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(out);
        float magicfloat = 0.296842f;
        dataOutputStream.writeFloat(magicfloat);
        out.close();;
        byte[] bytes = out.toByteArray();

        DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(bytes));
        float v = dataInputStream.readFloat();
        assertEquals(magicfloat, v);
    }
}
