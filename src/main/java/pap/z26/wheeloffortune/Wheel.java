package pap.z26.wheeloffortune;

import java.util.HashMap;
import java.util.Random;

public class Wheel {

    private final HashMap<GameState, int[]> wheelContents = new HashMap<>();
    private int lastRolled = -3, riggedRoll = -174;
    private final Random random = new Random();

    public Wheel() {
        // 0 = bankrupt, -1 = pass, -2 = roll again
        wheelContents.put(GameState.NOT_STARTED, new int[]{0});
        wheelContents.put(GameState.ROUND1, new int[]{150, -1, 500, 0, 200, 300, 150, 100, 200, 500, -2, 300, 150, 200, 100, 500, 150, 300, 200, 500, 1000, 0, 200, 100});
        wheelContents.put(GameState.ROUND2, new int[]{100, 150, 200, 250, 300, 350, 400, 450, 500}); // trzeba uzupełnić o brakujące opcje
        wheelContents.put(GameState.ROUND3, new int[]{150, 200, 300, 100, 500, 200, 350, 0, 1000, 100, 350, 100, 200, 500, -1, 300, 100, 400, 550, 350, 0, 1500, 150, 150, -2});
        wheelContents.put(GameState.ROUND4, new int[]{1000});
        wheelContents.put(GameState.ROUND5, new int[]{150, 0, 2500, 250, -2, 350, 300, 200, -1, 500, 200, 1500, 0, 2000, 200, 350, 550, 200, 500, -1, 300, 250, 500});
        wheelContents.put(GameState.FINAL, new int[]{2500, 5000, 7500, 10000, 15000, 20000, 25000, 30000, 40000, 50000});
    }

    public int spin(GameState state) {
        if(riggedRoll != -174) { // rigged by server
            lastRolled = riggedRoll;
            riggedRoll = -174;
        } else {
            lastRolled = wheelContents.get(state)[random.nextInt(wheelContents.get(state).length)];
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
