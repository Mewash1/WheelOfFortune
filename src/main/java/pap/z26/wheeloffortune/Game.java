package pap.z26.wheeloffortune;

import org.json.JSONObject;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class Game {

    private final ArrayList<Player> players = new ArrayList<>();
    private Player currentPlayer = null, roundStarter = null, winner = null;
    private final HashMap<Player, Integer> scores = new HashMap<>(), roundScores = new HashMap<>();
    private boolean inProgress;
    private GameState state;
    private GameWord gameWord;
    private String category;
    private final Wheel wheel = new Wheel();
    private GameWindowGUI window;
    private NetworkClient networkClient;
    private GameServer gameServer = null;
    boolean beingExecutedByServer = false;

    private enum MoveState {
        HAS_TO_SPIN,
        HAS_TO_GUESS_CONSONANT,
        CAN_BUY_VOWEL_SPIN_OR_GUESS
    }

    private MoveState moveState;

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

    public Game() {}
    public Game(GameServer server) {
        this.gameServer = server;
    }

    public void setGameWindow(GameWindowGUI window) {
        this.window = window;
    }

    public boolean joinGame(Player player) {
        if (player.getGame() == null && players.size() < 3 && !inProgress && notInGame(player)) {
            players.add(player);
            player.setGame(this);
            if(window != null) {
                window.writeToGameLog("Player " + player.getName() + " joined the game");
            }
            return true;
        }
        return false;
    }

    private boolean notInGame(Player player) {
        for(Player inGamePlayer: players) {
            if(inGamePlayer.getName().equals(player.getName())) return false;
        }
        return true;
    }

    public boolean leaveGame(Player player) {
        if (!inProgress) {
            player.setGame(null);
            players.remove(player);
            if(window != null) {
                window.writeToGameLog("Player " + player.getGame() + " left the game");
            }
            return true;
        }
        return false;
    }

    public void startGame() {
        start();
        advanceRound();
    }

    public void start() {
        winner = null;
        state = GameState.NOT_STARTED;
        while (players.size() < 3) {
            joinGame(new BotPlayer(""));
        }
        inProgress = true;
        roundStarter = players.get(players.size()-1);
        for (Player player : players) {
            roundScores.put(player, 0);
            scores.put(player, 0);
        }
    }

    private void nextMove() {
        if (currentPlayer == null) return;
        if (moveState == MoveState.HAS_TO_SPIN && window != null) {
            window.writeToGameLog("Player " + currentPlayer.getName() + " moves now");
        }
        Mover mover = new Mover(currentPlayer);
        Timer timer = new Timer(3000, mover); // so the moves aren't instant in case of bots
        timer.setRepeats(false);
        timer.start();
    }

    public boolean spinTheWheel(Player player) {
        if (currentPlayer == null) return false;
        if (currentPlayer == player && (moveState == MoveState.HAS_TO_SPIN || moveState == MoveState.CAN_BUY_VOWEL_SPIN_OR_GUESS) && gameWord.hasNotGuessedConsonants()) {
            moveState = MoveState.HAS_TO_GUESS_CONSONANT;
            int result = wheel.spin(state);
            if (result <= 0) {
                if (result == 0) {
                    roundScores.put(currentPlayer, 0);
                }
                if (result >= -1) {
                    assignNextPlayer();
                }
                moveState = MoveState.HAS_TO_SPIN;
            }
            if(window != null) {
                window.writeToGameLog("Player " + player.getName() + " spun the wheel and got " + getLastRolled());
                window.updateGUI();
            }
            nextMove();
            reportActionToServer(player, "spin:" + result);
            return true;
        }
        if(window != null) {
            window.writeToGameLog("You can't spin the wheel now!");
            window.updateGUI();
        }
        return false;
    }

    private void assignNextPlayer() {
        int currentPlayerIndex = players.indexOf(currentPlayer);
        currentPlayer = players.get((currentPlayerIndex + 1) % players.size());
        moveState = MoveState.HAS_TO_SPIN;
    }

    public int guessLetter(Player player, char letter) {
        if (currentPlayer == null || currentPlayer != player) return -3;
        if (moveState == MoveState.HAS_TO_SPIN && hasNotGuessedConsonants()) {
            if(window != null) {
                window.writeToGameLog("You need to spin the wheel first");
            }
            nextMove();
            return -3;
        }
        int result;
        if (moveState == MoveState.HAS_TO_GUESS_CONSONANT) {
            if (!gameWord.hasNotGuessedConsonants()) {
                if(window != null) {
                    window.writeToGameLog("There are no consonants left!");
                }
                nextMove();
                return -3;
            }
            if (GameWord.vowels.contains(letter)) {
                if(window != null) {
                    window.writeToGameLog("You have to guess a consonant!");
                }
                nextMove();
                return -3;
            }
            result = gameWord.guessLetter(letter);
            if(window != null) {
                window.writeToGameLog("Player " + player.getName() + " guessed the letter " + letter + " with " + result + " hits");
            }
            if (result == 0) {
                assignNextPlayer();
            } else {
                int currentScore = roundScores.get(player);
                roundScores.put(player, currentScore + result * wheel.getLastRolled());
                moveState = MoveState.CAN_BUY_VOWEL_SPIN_OR_GUESS;
            }
        } else { // vowel
            if (!GameWord.vowels.contains(letter)) {
                if(window != null) {
                    window.writeToGameLog("You need to either buy a vowel or guess the phrase!");
                }
                nextMove();
                return -3;
            }
            if (roundScores.get(player) < 200) {
                if(window != null) {
                    window.writeToGameLog("You don't have enough money to buy a vowel!");
                }
                nextMove();
                return -3;
            }
            result = gameWord.guessLetter(letter);
            if(window != null) {
                window.writeToGameLog("Player " + player.getName() + " guessed the letter " + letter + " with " + result + " hits");
            }
            roundScores.put(player, roundScores.get(player) - 200);
        }
        if(window != null) {
            window.updateGUI();
        }
        nextMove();
        reportActionToServer(player, "guessl:" + letter);
        return result;
    }

    public boolean guessPhrase(Player player, String phrase) {
        if (currentPlayer == null || player != currentPlayer || (moveState != MoveState.CAN_BUY_VOWEL_SPIN_OR_GUESS && hasNotGuessedConsonants()))
            return false;
        boolean result = gameWord.guessPhrase(phrase);
        if(window != null) {
            window.writeToGameLog("Player " + player.getName() + " tried to guess " + phrase + " and " + (result ? "succeeded!" : "failed."));
            window.updateGUI();
        }
        if (!result) {
            assignNextPlayer();
        } else {
            nextMove();
            reportActionToServer(player, "guessp:" + phrase);
            beingExecutedByServer = false;
            advanceRound();
        }
        return result;
    }

    private void advanceRound() {
        if(gameServer == null && !beingExecutedByServer) return; // only the server can advance rounds
        Database database = Database.getInstance();
        if (state == GameState.FINAL) {
            scores.put(winner, scores.get(winner) + roundScores.get(winner));
            database.saveGameResult(winner.getName(), scores.get(winner));
        } else {
            for (Player player : players) {
                if (currentPlayer != null) {
                    scores.put(player, scores.get(player) + (player == currentPlayer ? roundScores.get(player) : 0));
                }
                roundScores.put(player, 0);
            }
            if(!beingExecutedByServer) {
                Phrase gamePhrase = database.getRandomPhrase(null);
                gameWord = new GameWord(gamePhrase.phrase());
                category = gamePhrase.category();
            }
        }
        state = state.next();
        roundStarter = players.get((players.indexOf(roundStarter)+1)%players.size());
        currentPlayer = roundStarter;
        if (state != GameState.ENDED) {
            if(window != null) {
                window.writeToGameLog("Round " + state.toString() + " is starting!");
            }
        }
        if (state == GameState.FINAL) {
            winner = Collections.max(scores.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
        }
        if (state == GameState.ENDED) {
            currentPlayer = null;
        }
        moveState = MoveState.HAS_TO_SPIN;
        nextMove();
        if(window != null) {
            window.updateGUI();
        }
        reportActionToServer(null, "newword:" + gameWord.getPhrase() + ":" + category);
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

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public String getLastRolled() {
        String lastRolled;
        switch (wheel.getLastRolled()) {
            case -2 -> lastRolled = "Roll again";
            case -1 -> lastRolled = "Pass";
            case 0 -> lastRolled = "Bankrupt!";
            default -> lastRolled = "$" + wheel.getLastRolled();
        }
        return lastRolled;
    }

    public boolean hasNotGuessedConsonants() {
        return gameWord.hasNotGuessedConsonants();
    }

    public Player getWinner() {
        if (state == GameState.ENDED) {
            return winner;
        }
        return null;
    }

    public GameState getState() {
        return state;
    }

    public String getCategory() {
        return category;
    }

    private Player getPlayerByName(String name) {
        for(Player player: players) {
            if(player.getName().equals(name)) {
                return player;
            }
        }
        return null;
    }

    public void setNetworkClient(NetworkClient client) {
        networkClient = client;
    }

    public void executeFromOutside(JSONObject jsonData, boolean executedOnServer) {
        beingExecutedByServer = !executedOnServer;
        Player actionMaker = null;
        if(jsonData.has("player")) {
            actionMaker = getPlayerByName(jsonData.getString("player"));
        }
        switch(jsonData.getString("action")) {
            case "spin" -> {
                int value = jsonData.getInt("value");
                wheel.rig(value);
                spinTheWheel(actionMaker);
            }
            case "guessl" -> {
                String letter = jsonData.getString("letter");
                guessLetter(actionMaker, letter.toCharArray()[0]);
            }
            case "guessp" -> {
                String phrase = jsonData.getString("phrase");
                guessPhrase(actionMaker, phrase);
            }
            case "newword" -> {
                gameWord = new GameWord(jsonData.getString("word"));
                category = jsonData.getString("cat");
                advanceRound();
            }
            case "start" -> {
                start();
            }
        }
        beingExecutedByServer = false;
    }

    private void reportActionToServer(Player player, String action) {
        if(beingExecutedByServer) return;
        JSONObject response = new JSONObject();
        if(player != null) {
            response.put("player", player.getName());
        }
        String[] parts = action.split(":");
        response.put("action", parts[0]);
        switch (parts[0]) {
            case "spin" -> {
                response.put("value", Integer.parseInt(parts[1]));
            }
            case "guessl" -> {
                response.put("letter", parts[1]);
            }
            case "guessp" -> {
                response.put("phrase", parts[1]);
            }
            case "newword" -> {
                response.put("word", parts[1]);
                response.put("cat", parts[2]);
            }
        }
        if(gameServer == null) { // local game
            networkClient.sendData(response.toString());
        } else { // running on server
            gameServer.tellEveryoneBut(response.toString(), player, this);
        }

    }
}
