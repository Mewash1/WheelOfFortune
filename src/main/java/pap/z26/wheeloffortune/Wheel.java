package pap.z26.wheeloffortune;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Wheel {

    private HashMap<GameState, ArrayList<Integer>> wheelContents;
    private int lastRolled = -3, riggedRoll = -174;
    private final Random random = new Random();

    public Wheel() {
        Database database = Database.getInstance();
        wheelContents = database.getWheelContents();
    }

    public int spin(GameState state) {
        if(riggedRoll != -174) { // rigged by server
            lastRolled = riggedRoll;
            riggedRoll = -174;
        } else {
            lastRolled = wheelContents.get(state).get(random.nextInt(wheelContents.get(state).size()));
        }
        return lastRolled;
    }

    public void rig(int value) {
        riggedRoll = value;
    }

    public int getLastRolled() {
        return lastRolled;
    }

    public void reset() {
        lastRolled = -3;
    }
}
