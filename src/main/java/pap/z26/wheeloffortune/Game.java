package pap.z26.wheeloffortune;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class Game {

    private final ArrayList<Player> players = new ArrayList<>();
    private Player currentPlayer = null, winner = null;
    private final HashMap<Player, Integer> scores = new HashMap<>(), roundScores = new HashMap<>();
    private boolean inProgress, wheelSpun;
    private GameState state;
    private GameWord gameWord;
    private final Wheel wheel = new Wheel();
    private GameWindowGUI window;

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

    public void setGameWindow(GameWindowGUI window) {
        this.window = window;
    }

    public boolean joinGame(Player player) {
        if (player.getGame() == null && players.size() < 3 && !inProgress) {
            players.add(player);
            player.setGame(this);
            window.writeToGameLog("Player " + player.getName() + " joined the game");
            return true;
        }
        return false;
    }

    public boolean leaveGame(Player player) {
        if (!inProgress) {
            player.setGame(null);
            players.remove(player);
            window.writeToGameLog("Player " + player.getGame() + " left the game");
            return true;
        }
        return false;
    }

    public void startGame() {
        winner = null;
        state = GameState.NOT_STARTED;
        while (players.size() < 3) {
            joinGame(new BotPlayer(null));
        }
        inProgress = true;
        currentPlayer = players.get(0);
        advanceRound();
        window.writeToGameLog("The game has started!");
        window.updateGUI();
        nextMove();
    }

    private void nextMove() {
        Mover mover = new Mover(currentPlayer);
        Timer timer = new Timer(500, mover); // so the moves aren't instant in case of bots
        timer.setRepeats(false);
        timer.start();
    }

    public boolean spinTheWheel(Player player) {
        if (currentPlayer != null && currentPlayer == player && !wheelSpun) {
            wheelSpun = true;
            int result = wheel.spin(state);
            if (result == 0) {
                roundScores.put(currentPlayer, 0);
                assignNextPlayer();
            } else if (result == -1) {
                assignNextPlayer();
            } else if (result == -2) {
                wheelSpun = false;
            }
            window.writeToGameLog("Player " + player.getName() + " spun the wheel and got " + result);
            window.updateGUI();
            nextMove();
            return true;
        }
        window.writeToGameLog("You can't spin the wheel now!");
        window.updateGUI();
        return false;
    }

    private void assignNextPlayer() {
        int currentPlayerIndex = players.indexOf(currentPlayer);
        currentPlayer = players.get((currentPlayerIndex + 1) % players.size());
    }

    public int guessLetter(Player player, char letter) {
        if(!wheelSpun) {
            window.writeToGameLog("You need to spin the wheel first");
            nextMove();
            return -69;
        }
        wheelSpun = false;
        int result = gameWord.guessLetter(letter);
        if (result == 0) {
            assignNextPlayer();
        } else {
            int currentScore = roundScores.get(player);
            roundScores.put(player, currentScore + result * wheel.getLastRolled());
        }
        window.writeToGameLog("Player" + player.getName() + " guessed the letter " + letter + " with " + result + " hits");
        window.updateGUI();
        nextMove();
        return result;
    }

    public boolean guessPhrase(Player player, String phrase) {
//        if(!wheelSpun) {
//            window.writeToGameLog("You need to spin the wheel first");
//            return false;
//        }
        boolean result = gameWord.guessPhrase(phrase);
        if (!result) {
            assignNextPlayer();
        } else {
            int currentScore = scores.get(player);
            scores.put(player, currentScore + roundScores.get(player));
            advanceRound();
        }
        window.writeToGameLog("Player" + player.getName() + " tried to guess " + phrase + " and " + (result?"succeeded!":"failed."));
        window.updateGUI();
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
        window.writeToGameLog("Round " + state.toString() + " is starting!");
        if (state == GameState.FINAL) {
            winner = Collections.max(scores.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
        }
        window.updateGUI();
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

    public boolean isWheelSpun() {
        return wheelSpun;
    }
}
