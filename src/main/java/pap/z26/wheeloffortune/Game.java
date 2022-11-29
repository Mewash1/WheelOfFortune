package pap.z26.wheeloffortune;

import java.util.ArrayList;
import java.util.HashMap;

public class Game {

    private ArrayList<Player> players;
    private HashMap<Player, Integer> scores, roundScores;
    private boolean inProgress;
    private GameState state;
    private GameWord gameWord;
    private Player whoMoves;
    private Wheel wheel;
    private GameWindow window;

    public Game() {

    }

    public boolean joinGame(Player player) {
        return false;
    }

    public boolean leaveGame(Player player) {
        return false;
    }

    private void notifyAboutMove(Player player) {

    }

    public void startGame() {

    }

    public boolean spinTheWheel(Player player) {
        return false;
    }

    public boolean guessLetter(Player player) {
        return false;
    }

    public boolean guessPhrase(Player player) {
        return false;
    }

    public String getPhrase() {
        return gameWord.getCurrentState();
    }

    public HashMap<Player, Integer> getScores() {
        return scores;
    }

    public HashMap<Player, Integer> getRoundScores() {
        return roundScores;
    }

    public int getLastRolled() {
        return wheel.getLastRolled();
    }
}
