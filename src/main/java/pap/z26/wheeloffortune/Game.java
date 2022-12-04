package pap.z26.wheeloffortune;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class Game {

    private final ArrayList<Player> players = new ArrayList<>();
    private Player currentPlayer = null, winner = null;
    private final HashMap<Player, Integer> scores = new HashMap<>(), roundScores = new HashMap<>();
    private boolean inProgress;
    private GameState state;
    private GameWord gameWord;
    private Wheel wheel = new Wheel();
    private GameWindow window;

    private static class Mover implements ActionListener {

        Player player;

        public Mover(Player player) {
            this.player = player;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            player.makeAMove();
        }
    }

    public Game() {

    }

    public boolean joinGame(Player player) {
        if (player.getGame() == null && players.size() < 3 && !inProgress) {
            players.add(player);
            player.setGame(this);
            return true;
        }
        return false;
    }

    public boolean leaveGame(Player player) {
        if (!inProgress) {
            player.setGame(null);
            players.remove(player);
            return true;
        }
        return false;
    }

    public void startGame() {
        inProgress = true;
        winner = null;
        while (players.size() < 3) {
            players.add(new BotPlayer(this));
        }
        currentPlayer = players.get(0);
        advanceRound();
        nextMove();
    }

    private void nextMove() {
        Mover mover = new Mover(currentPlayer);
        Timer timer = new Timer(500, mover); // so the moves aren't instant in case of bots
        timer.setRepeats(false);
        timer.start();
    }

    public boolean spinTheWheel(Player player) {
        if (currentPlayer != null && currentPlayer == player) {
            int result = wheel.spin(state);
            if (result == 0) {
                roundScores.put(currentPlayer, 0);
                assignNextPlayer();
            } else if (result == -1) {
                assignNextPlayer();
            }
            nextMove();
        }
        return false;
    }

    private void assignNextPlayer() {
        int currentPlayerIndex = players.indexOf(currentPlayer);
        currentPlayer = players.get((currentPlayerIndex + 1) % players.size());
    }

    public int guessLetter(Player player, char letter) {
        int result = gameWord.guessLetter(letter);
        if (result == 0) {
            assignNextPlayer();
        } else {
            int currentScore = roundScores.get(player);
            roundScores.put(player, currentScore + result * wheel.getLastRolled());
        }
        nextMove();
        return result;
    }

    public boolean guessPhrase(Player player, String phrase) {
        boolean result = gameWord.guessPhrase(phrase);
        if (!result) {
            assignNextPlayer();
        } else {
            int currentScore = scores.get(player);
            scores.put(player, currentScore + roundScores.get(player));
            advanceRound();
        }
        nextMove();
        return result;
    }

    private void advanceRound() {
        Database database = Database.getInstance();
        if(state == GameState.FINAL) {
            database.saveGameResult(winner.getName(), scores.get(winner));
        } else {
            for(Player player: players) {
                roundScores.put(player, 0);
            }
            gameWord = new GameWord(database.getRandomPhrase(null));
        }
        state = state.next();
        if (state == GameState.FINAL) {
            winner = Collections.max(scores.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
        }
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

    public boolean hasNotGuessedConsonants() {
        return gameWord.hasNotGuessedConsonants();
    }

    public Player getWinner() {
        if(state == GameState.ENDED) {
            return winner;
        }
        return null;
    }
}
