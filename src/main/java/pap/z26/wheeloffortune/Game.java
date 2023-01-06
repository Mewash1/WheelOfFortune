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
    private Timer moveTimeoutTimer, moverTimer;

    private enum MoveState {
        HAS_TO_SPIN,
        HAS_TO_GUESS_CONSONANT,
        CAN_BUY_VOWEL_SPIN_OR_GUESS
    }

    private MoveState moveState;

    private class Mover implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentPlayer != null) {
                currentPlayer.makeAMove();
            } else if (state == GameState.ROUND4) {
                for (Player inGamePlayer : players) {
                    inGamePlayer.makeAMove();
                }
            }
        }
    }

    private class MoveTimeouter implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (state == GameState.ROUND4) {
                if (gameServer != null) {
                    int index = gameWord.uncoverRandomLetter();
                    reportActionToServer(null, "uncov:" + index);
                }
            } else if (state == GameState.FINAL) {
                if(moveState == MoveState.HAS_TO_GUESS_CONSONANT) {
                    nextMove();
                } else if (moveState == MoveState.CAN_BUY_VOWEL_SPIN_OR_GUESS) {
                    advanceRound();
                }
            }
            else {
                assignNextPlayer();
                nextMove();
            }
        }
    }

    public Game() {
        setup();
    }

    public Game(GameServer server) {
        this.gameServer = server;
        setup();
    }

    private void setup() {
        MoveTimeouter timeouter = new MoveTimeouter();
        moveTimeoutTimer = new Timer(1000000000, timeouter);
        moveTimeoutTimer.setRepeats(true);
        moveTimeoutTimer.start();
        Mover mover = new Mover();
        moverTimer = new Timer(2000, mover);
    }

    public void setGameWindow(GameWindowGUI window) {
        this.window = window;
    }

    public boolean joinGame(Player player) {
        if (player.getGame() == null && players.size() < 3 && !inProgress && notInGame(player)) {
            players.add(player);
            player.setGame(this);
            if (window != null) {
                window.writeToGameLog("Player " + player.getName() + " joined the game");
            }
            return true;
        }
        return false;
    }

    private boolean notInGame(Player player) {
        for (Player inGamePlayer : players) {
            if (inGamePlayer.getName().equals(player.getName())) return false;
        }
        return true;
    }

    public boolean leaveGame(Player player) {
        if (!inProgress) {
            player.setGame(null);
            players.remove(player);
            if (window != null) {
                window.writeToGameLog("Player " + player.getName() + " left the game");
            }
            return true;
        }
        return false;
    }

    public void leaveGameAndReplace(Player player, String replacementBotName) {
        HumanPlayer replacementPlayer = new HumanPlayer(replacementBotName);
        leaveGameAndReplace(player, replacementPlayer);
    }

    public BotPlayer leaveGameAndReplace(Player player) {
        BotPlayer replacementPlayer = new BotPlayer();
        leaveGameAndReplace(player, replacementPlayer);
        return replacementPlayer;
    }

    private void leaveGameAndReplace(Player player, Player replacementPlayer) {
        replacementPlayer.setGame(this);
        int playerIndex = players.indexOf(player);
        players.set(playerIndex, replacementPlayer);
        scores.put(replacementPlayer, scores.get(player));
        scores.remove(player);
        roundScores.put(replacementPlayer, roundScores.get(player));
        roundScores.remove(player);
        if (window != null) {
            window.writeToGameLog("Player " + player.getName() + " was replaced by Bot " + replacementPlayer.getName());
        }
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
        roundStarter = players.get(players.size() - 1);
        for (Player player : players) {
            roundScores.put(player, 0);
            scores.put(player, 0);
        }
    }

    private void applyMoveTimeLimit(int limit) {
        limit = limit * 1000;
        moveTimeoutTimer.stop();
        moveTimeoutTimer.setInitialDelay(limit);
        moveTimeoutTimer.setDelay(limit);
        moveTimeoutTimer.setRepeats(state == GameState.ROUND4);
        moveTimeoutTimer.restart();
        moveTimeoutTimer.start();
    }

    private void nextMove() {
        if (currentPlayer == null && state != GameState.ROUND4) return;
        if(state != GameState.FINAL) {
            if ((moveState == MoveState.HAS_TO_SPIN || (moveState == MoveState.HAS_TO_GUESS_CONSONANT && state == GameState.ROUND2)) && window != null && currentPlayer != null) {
                window.writeToGameLog("Player " + currentPlayer.getName() + " moves now");
            }
        } else if (window != null) {
            if(moveState == MoveState.HAS_TO_GUESS_CONSONANT) {
                window.writeToGameLog("Player" + winner.getName() + " has to choose a vowel and 3 consonants");
            } else {
                window.writeToGameLog("Player" + winner.getName() + " has 15 seconds to guess the phrase now!");
            }
        }
        moverTimer.stop();
        moverTimer.setRepeats(state == GameState.ROUND4);
        moverTimer.restart();
        int limit = switch (state) {
            case ROUND2 -> 5;
            case ROUND4 -> 2;
            case FINAL -> 15;
            default -> 60;
        };
        applyMoveTimeLimit(limit);
    }

    public boolean spinTheWheel(Player player) {
        if (currentPlayer == null || (!beingExecutedByServer && state == GameState.ROUND2)) return false;
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
            if (window != null) {
                window.writeToGameLog("Player " + player.getName() + " spun the wheel and got " + getLastRolled());
                window.updateGUI();
            }
            nextMove();
            reportActionToServer(player, "spin:" + result);
            return true;
        }
        if (window != null) {
            window.writeToGameLog("You can't spin the wheel now!");
            window.updateGUI();
        }
        return false;
    }

    private void assignNextPlayer() {
        if (!inProgress) return;
        if (state == GameState.FINAL) {
            currentPlayer = winner;
            moveState = MoveState.HAS_TO_GUESS_CONSONANT;
        } else {
            int currentPlayerIndex = players.indexOf(currentPlayer);
            currentPlayer = players.get((currentPlayerIndex + 1) % players.size());
            if (state == GameState.ROUND2) {
                if (gameWord.hasNotGuessedLetters()) {
                    moveState = MoveState.HAS_TO_GUESS_CONSONANT;
                } else {
                    moveState = MoveState.CAN_BUY_VOWEL_SPIN_OR_GUESS;
                }
            } else {
                moveState = MoveState.HAS_TO_SPIN;
            }
        }
    }

    public int guessLetter(Player player, char letter) {
        if (currentPlayer == null || currentPlayer != player || state == GameState.ROUND4 || state == GameState.FINAL) return -3;
        if (moveState == MoveState.HAS_TO_SPIN && hasNotGuessedConsonants()) {
            if (window != null) {
                window.writeToGameLog("You need to spin the wheel first");
            }
            nextMove();
            return -3;
        }
        int result;
        if (state == GameState.ROUND2) {
            if (moveState != MoveState.HAS_TO_GUESS_CONSONANT) { // in this case any letter goes
                if (window != null) {
                    window.writeToGameLog("You have to try to guess the phrase!");
                }
                return -3;
            }
            result = gameWord.guessLetter(letter);
            if (window != null) {
                window.writeToGameLog("Player " + player.getName() + " guessed the letter " + letter + " with " + result + " hits");
            }
            if (result == 0) {
                assignNextPlayer();
            } else {
                if (GameWord.consonants.contains(letter)) {
                    int currentScore = roundScores.get(player);
                    roundScores.put(player, currentScore + result * wheel.getLastRolled());
                }
                moveState = MoveState.CAN_BUY_VOWEL_SPIN_OR_GUESS;
            }
        } else {
            if (moveState == MoveState.HAS_TO_GUESS_CONSONANT) {
                if (!gameWord.hasNotGuessedConsonants()) {
                    if (window != null) {
                        window.writeToGameLog("There are no consonants left!");
                    }
                    nextMove();
                    return -3;
                }
                if (GameWord.vowels.contains(letter)) {
                    if (window != null) {
                        window.writeToGameLog("You have to guess a consonant!");
                    }
                    nextMove();
                    return -3;
                }
                result = gameWord.guessLetter(letter);
                if (window != null) {
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
                    if (window != null) {
                        window.writeToGameLog("You need to either buy a vowel or guess the phrase!");
                    }
                    nextMove();
                    return -3;
                }
                if (roundScores.get(player) < 200) {
                    if (window != null) {
                        window.writeToGameLog("You don't have enough money to buy a vowel!");
                    }
                    nextMove();
                    return -3;
                }
                result = gameWord.guessLetter(letter);
                if (window != null) {
                    window.writeToGameLog("Player " + player.getName() + " guessed the letter " + letter + " with " + result + " hits");
                }
                roundScores.put(player, roundScores.get(player) - 200);
            }
        }
        if (window != null) {
            window.updateGUI();
        }
        nextMove();
        reportActionToServer(player, "guessl:" + letter);
        return result;
    }

    public boolean guessPhrase(Player player, String phrase) {
        boolean result = false;
        if (state != GameState.FINAL) {
            if (currentPlayer == null || player != currentPlayer || (moveState != MoveState.CAN_BUY_VOWEL_SPIN_OR_GUESS && hasNotGuessedConsonants())) {
                if (state != GameState.ROUND4) return false;
            }
            result = gameWord.guessPhrase(phrase);
            if (window != null) {
                window.writeToGameLog("Player " + player.getName() + " tried to guess " + phrase + " and " + (result ? "succeeded!" : "failed."));
            }
            if (!result) {
                assignNextPlayer();
                nextMove();
            } else {
                nextMove();
                beingExecutedByServer = false;
                advanceRound();
            }
        } else if (player == winner) {
            if (moveState == MoveState.HAS_TO_GUESS_CONSONANT) {
                int chosenLettersCount = Math.min(phrase.length(), 4);
                char[] lettersToUncover = phrase.substring(0, chosenLettersCount).toCharArray();
                for (char letter : lettersToUncover) {
                    gameWord.guessLetter(letter);
                }
                moveState = MoveState.CAN_BUY_VOWEL_SPIN_OR_GUESS;
                nextMove();
            } else if (moveState == MoveState.CAN_BUY_VOWEL_SPIN_OR_GUESS) {
                result = gameWord.guessPhrase(phrase);
                if (window != null) {
                    window.writeToGameLog("Player " + player.getName() + " tried to guess " + phrase + " and " + (result ? "succeeded!" : "failed."));
                }
                if(result) {
                    roundScores.put(winner, wheel.getLastRolled());
                }
                beingExecutedByServer = false;
                advanceRound();
            }
        }
        if(window != null) {
            window.updateGUI();
        }
        reportActionToServer(player, "guessp:" + phrase);
        return result;
    }

    private Player assignRoundStarter() {
        return switch (state) {
            case NOT_STARTED, ROUND4, ENDED -> null;
            case ROUND1, ROUND2 -> players.get(0);
            case ROUND3 -> players.get(1);
            case ROUND5 -> players.get(2);
            case FINAL -> winner;
        };
    }

    private void advanceRound() {
        moverTimer.stop();
        moveTimeoutTimer.stop();
        if (gameServer == null && !beingExecutedByServer) return; // only the server can advance rounds
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
            if (!beingExecutedByServer) {
                Phrase gamePhrase = database.getRandomPhrase(null);
                gameWord = new GameWord(gamePhrase.phrase());
                category = gamePhrase.category();
            }
        }
        state = state.next();
        if (state != GameState.ENDED) {
            if (window != null) {
                window.writeToGameLog("Round " + state.toString() + " is starting!");
            }
        }
        if (state == GameState.FINAL) {
            winner = Collections.max(scores.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
            String toUncover = "rstlne";
            for(char letter: toUncover.toCharArray()) {
                gameWord.guessLetter(letter);
            }
        }
        roundStarter = assignRoundStarter();
        currentPlayer = roundStarter;
        if (state == GameState.ENDED) {
            currentPlayer = null;
        }
        moveState = state != GameState.FINAL ? MoveState.HAS_TO_SPIN : MoveState.HAS_TO_GUESS_CONSONANT;
        nextMove();
        if (window != null) {
            window.updateGUI();
        }
        reportActionToServer(null, "newword:" + gameWord.getPhrase() + ":" + category);
        if (state == GameState.ROUND2 && !beingExecutedByServer) {
            int prizeForLetter = wheel.spin(state);
            reportActionToServer(null, "spin:" + prizeForLetter);
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

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public boolean isInProgress() {
        return inProgress;
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
        if (name.equals("SYSTEM")) {
            currentPlayer = players.get(0);
            name = currentPlayer.getName();
        }
        for (Player player : players) {
            if (player.getName().equals(name)) {
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
        if (jsonData.has("player")) {
            actionMaker = getPlayerByName(jsonData.getString("player"));
        }
        switch (jsonData.getString("action")) {
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
            case "uncov" -> {
                gameWord.uncoverRandomLetter(jsonData.getInt("index"));
                if (window != null) window.updateGUI();
            }
            case "left" -> {
                if (jsonData.has("repl")) {
                    leaveGameAndReplace(actionMaker, jsonData.getString("repl"));
                } else {
                    leaveGame(actionMaker);
                }
            }
        }
        beingExecutedByServer = false;
    }

    private void reportActionToServer(Player player, String action) {
        if (beingExecutedByServer) return;
        JSONObject response = new JSONObject();
        if (player != null) {
            response.put("player", player.getName());
        } else {
            response.put("player", "SYSTEM");
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
            case "uncov" -> {
                response.put("index", Integer.parseInt(parts[1]));
            }
        }
        if (gameServer == null) { // local game
            networkClient.sendData(response.toString());
        } else { // running on server
            gameServer.tellEveryoneBut(response.toString(), player, this);
        }
    }
}
