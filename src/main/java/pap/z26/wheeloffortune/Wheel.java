package pap.z26.wheeloffortune;

import java.util.ArrayList;
import java.util.HashMap;

public class Wheel {

    private HashMap<GameState, ArrayList<Integer>> wheelContents;
    private int lastRolled;

    public Wheel() {

    }

    public int spin(GameState state) {
        return -1;
    }

    public int getLastRolled() {
        return lastRolled;
    }
}
