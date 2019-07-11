package NetworkedPhysics;

import NetworkedPhysics.Common.Protocol.serverCommands.WorldState;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class NetworkedPhysicsServerTest {

    static Gson gson = new Gson();

    @Test
    void compareTwoServerAndClientStatesAndExpect0Diff() throws FileNotFoundException {
        System.out.println("loading states ...");
        ArrayList<WorldState> clientStates = readStates("Client.states");
        ArrayList<WorldState> serverStates = readStates("Server.states");
        double[] diffs = new double[clientStates.size()];

        System.out.println("comparing states ...");
        for (int i = 0; i < clientStates.size(); i++) {
            diffs[i]= clientStates.get(i).getDifference(serverStates.get(i));
        }
        assertArrayEquals(new double[clientStates.size()],diffs);
    }

    private ArrayList<WorldState> readStates(String path) throws FileNotFoundException {
        ArrayList<WorldState> states = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
        reader.lines().forEach(l -> states.add(gson.fromJson(l, WorldState.class)));
        return states;
    }
}
