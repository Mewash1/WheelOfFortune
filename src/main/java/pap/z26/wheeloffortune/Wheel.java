package pap.z26.wheeloffortune;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Wheel {

    private final HashMap<GameState, ArrayList<Integer>> wheelContents;
    private int lastRolled = -3, riggedRoll = -174;
    private final Random random = new Random();

    /**
     * Creates a Wheel object that downloads the wheel contents from the {@link Database Database}
     */
    public Wheel() {
        Database database = Database.getInstance();
        wheelContents = database.getWheelContents();
    }

    /**
     * Spins the wheel, returning a random value from the wheel corresponding to the given round, or a rigged value set
     * by a response from the {@link GameServer game server}
     *
     * @param state enum specifying which wheel should be spun, corresponding to the current round number
     * @return random value from the wheel, or the one set by the server in advance
     */
    public int spin(GameState state) {
        if (riggedRoll != -174) { // rigged by server
            lastRolled = riggedRoll;
            riggedRoll = -174;
        } else {
            lastRolled = wheelContents.get(state).get(random.nextInt(wheelContents.get(state).size()));
        }
        return lastRolled;
    }

    /**
     * Sets a value that will be returned on the next spinning of the wheel, used by {@link GameServer game server} to
     * simulate a spin done by a player in local games
     *
     * @param value value that will be returned on the next spin
     */
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
