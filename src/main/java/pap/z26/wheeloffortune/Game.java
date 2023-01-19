package pap.z26.wheeloffortune;

import org.json.JSONObject;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

/**
 * Class containing game logic
 */
public class Game {

    private final ArrayList<Player> players = new ArrayList<>();
    private Player currentPlayer = null, roundStarter = null, winner = null;
    private final HashMap<Player, Integer> scores = new HashMap<>(), roundScores = new HashMap<>();
    private boolean inProgress;
    private GameState state;
    private GameWord gameWord;
    private String category;
    private final Wheel wheel = new Wheel();
    private WoF_GUI window;
    private NetworkClient networkClient;
    private GameServer gameServer = null;
    boolean beingExecutedByServer = false;
    private Timer moveTimeoutTimer, moverTimer;
    private Database database;
    private int gameID;

    /**
     * Specifies the current state of a {@link Player player's move}
     */
    public enum MoveState {
        HAS_TO_SPIN,
        HAS_TO_GUESS_CONSONANT,
        CAN_BUY_VOWEL_SPIN_OR_GUESS
    }

    private MoveState moveState;

    /**
     * Class that prompts {@link BotPlayer BotPlayers} to make moves, an instance of this class
     * is being scheduled after each move to make bots move
     */
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

    /**
     * Class that checks whether {@link Player Player} has run out of time to make their move and assigns the current
     * move to the next player or advances the round itself (like in case of the final round)
     */
    private class MoveTimeouter implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (state == GameState.ROUND4) {
                if (gameServer != null) {
                    int index = gameWord.uncoverRandomLetter();
                    reportActionToServer(null, "uncov:" + index);
                }
            } else if (state == GameState.FINAL) {
                if (moveState == MoveState.HAS_TO_GUESS_CONSONANT) {
                    moveState = MoveState.CAN_BUY_VOWEL_SPIN_OR_GUESS;
                    nextMove();
                } else if (moveState == MoveState.CAN_BUY_VOWEL_SPIN_OR_GUESS) {
                    advanceRound();
                }
            } else {
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

    /**
     * Sets up timers responsible for timing out moves and sending bots information when they should move
     */
    private void setup() {
        MoveTimeouter timeouter = new MoveTimeouter();
        moveTimeoutTimer = new Timer(1000000000, timeouter);
        moveTimeoutTimer.setRepeats(true);
        moveTimeoutTimer.start();
        Mover mover = new Mover();
        moverTimer = new Timer(2000, mover);
        database = Database.getInstance();
    }

    public void setGameWindow(WoF_GUI window) {
        this.window = window;
    }

    /**
     * Makes a {@link Player player} join the game if the game isn't in progress and there is an empty slot for a player
     * available
     *
     * @param player player that will join the game
     * @return true if the join was successful, false otherwise
     */
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

    /**
     * Checks whether a player was already added to this {@link Game game} already
     *
     * @param player {@link Player player} to check for existence in this {@link Game game}
     * @return true if the player is not in this game, false otherwise
     */
    private boolean notInGame(Player player) {
        for (Player inGamePlayer : players) {
            if (inGamePlayer.getName().equals(player.getName())) return false;
        }
        return true;
    }

    /**
     * Makes a {@link Player player} leave this game
     *
     * @param player player that will leave this game. If the game is in progress, the player will not leave
     */
    public void leaveGame(Player player) {
        if (!inProgress) {
            player.setGame(null);
            players.remove(player);
            scores.remove(player);
            roundScores.remove(player);
            if (window != null) {
                window.writeToGameLog("Player " + player.getName() + " left the game");
            }
        }
    }

    /**
     * Removes a {@link Player player} from this game and replaces them with a bot. This is a method called by clients
     *
     * @param player             {@link Player player} to remove from the game
     * @param replacementBotName name of the replacement player
     */
    public void leaveGameAndReplace(Player player, String replacementBotName) {
        HumanPlayer replacementPlayer = new HumanPlayer(replacementBotName);
        leaveGameAndReplace(player, replacementPlayer);
    }

    /**
     * Removes a {@link Player player} from this game and replaces them with a bot. This is a method called by server
     *
     * @param player {@link Player player} to remove from the game
     */
    public BotPlayer leaveGameAndReplace(Player player) {
        BotPlayer replacementPlayer = new BotPlayer();
        leaveGameAndReplace(player, replacementPlayer);
        return replacementPlayer;
    }

    /**
     * Private method that executes player replacement, called by both {@link Game#leaveGameAndReplace(Player, String) the client's method}
     * and by {@link Game#leaveGameAndReplace(Player) the server's method}
     *
     * @param player            {@link Player player} that will be removed and replaced with another player
     * @param replacementPlayer {@link Player player} who will be put in the place of the leaving player
     */
    private void leaveGameAndReplace(Player player, Player replacementPlayer) {
        replacementPlayer.setGame(this);
        player.setGame(null);
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

    /**
     * Starts the game by calling the method that initializes the game and advances the round
     */
    public void startGame() {
        start();
        advanceRound();
    }

    /**
     * Initializes all fields used in the game
     */
    public void start() {
        winner = null;
        state = GameState.NOT_STARTED;
        while (players.size() < 3) {
            joinGame(new BotPlayer());
        }
        inProgress = true;
        roundStarter = players.get(players.size() - 1);
        for (Player player : players) {
            roundScores.put(player, 0);
            scores.put(player, 0);
        }
        gameID = gameServer == null ? 0 : database.addNewGame("online");
    }

    /**
     * Applies the move time limit by stopping the current timeout timer and rescheduling it with a new limit
     *
     * @param limit time limit for the next move, in seconds
     */
    private void applyMoveTimeLimit(int limit) {
        limit = limit * 1000;
        moveTimeoutTimer.stop();
        moveTimeoutTimer.setInitialDelay(limit);
        moveTimeoutTimer.setDelay(limit);
        moveTimeoutTimer.setRepeats(state == GameState.ROUND4);
        moveTimeoutTimer.restart();
        moveTimeoutTimer.start();
    }

    /**
     * Begins the next move by rescheduling move timers and reporting the current move to the {@link WoF_GUI game window}
     * Puts "Player "player" moves now" in the log in every round besides the 4th and the final round, when it outputs
     * phrases "Player "player" has 20 seconds to choose a vowel and 3 consonants / to guess the phrase now!
     */
    private void nextMove() {
        if (currentPlayer == null && state != GameState.ROUND4) return;
        if (state != GameState.FINAL && state != GameState.ROUND4) {
            if ((moveState == MoveState.HAS_TO_SPIN || moveState == MoveState.HAS_TO_GUESS_CONSONANT && state == GameState.ROUND2) && window != null) {
                window.writeToGameLog("Player " + currentPlayer.getName() + " moves now");
            }
        } else if (window != null && state == GameState.FINAL) {
            if (moveState == MoveState.HAS_TO_GUESS_CONSONANT) {
                window.writeToGameLog("Player " + winner.getName() + " has 20 seconds to choose a vowel and 3 consonants");
            } else {
                window.writeToGameLog("Player " + winner.getName() + " has 20 seconds to guess the phrase now!");
            }
        }
        moverTimer.stop();
        moverTimer.setRepeats(state == GameState.ROUND4);
        moverTimer.restart();
        int limit = switch (state) {
            case ROUND2 -> 5;
            case ROUND4 -> 2;
            case FINAL -> 20;
            default -> 60;
        };
        applyMoveTimeLimit(limit);
    }

    /**
     * Executes the wheel spinning action. Only the {@link Game#currentPlayer current player} is able to spin the wheel.
     * The result of the spin is saved in the {@link Game#wheel game wheel}
     *
     * @param player {@link Player player} who tries to spin the wheel
     * @return true if the spin was successful, false otherwise
     */
    public boolean spinTheWheel(Player player) {
        if ((currentPlayer == null && state != GameState.FINAL) || (!beingExecutedByServer && state == GameState.ROUND2))
            return false;
        if (state == GameState.FINAL) {
            if (player == null) {
                player = winner;
            } else {
                return false;
            }
        }
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
            if(gameServer != null) database.insertMove(result, null, null, null, gameID, player.getName());
            return true;
        }
        if (window != null) {
            window.writeToGameLog("You can't spin the wheel now!");
            window.updateGUI();
        }
        return false;
    }

    /**
     * Assigns the next player and sets the new moveState depending on the last action
     */
    private void assignNextPlayer() {
        if (!inProgress) return;
        if (state == GameState.FINAL) {
            currentPlayer = winner;
            moveState = MoveState.HAS_TO_GUESS_CONSONANT;
        } else if (state == GameState.ROUND4) {
            currentPlayer = null;
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

    /**
     * Executes the letter guess action. Only the {@link Game#currentPlayer current player} is able to guess
     *
     * @param player {@link Player player} that is trying to guess a letter
     * @param letter letter that is being guessed
     * @return the amount of uncovered letters if the guess was successful, 0 if the guess wasn't successful, or -3 if
     * the move was invalid, for example when a player who shouldn't move now tried to do so, or if a player tried to
     * guess a letter before spinning (outside of round 2). A failure will be reported in the game log
     */
    public int guessLetter(Player player, char letter) {
        if (currentPlayer == null || currentPlayer != player || state == GameState.ROUND4 || state == GameState.FINAL)
            return -3;
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
                window.writeToGameLog("Player " + player.getName() + " guessed the letter " + letter + " with " + result + " hit" + (result != 1 ? "s" : ""));
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
                    window.writeToGameLog("Player " + player.getName() + " guessed the letter " + letter + " with " + result + " hit" + (result != 1 ? "s" : ""));
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
                    window.writeToGameLog("Player " + player.getName() + " guessed the letter " + letter + " with " + result + " hit" + (result != 1 ? "s" : ""));
                }
                roundScores.put(player, roundScores.get(player) - 200);
            }
        }
        if (window != null) {
            window.updateGUI();
        }
        nextMove();
        reportActionToServer(player, "guessl:" + letter);
        if(gameServer != null) database.insertMove(-174, String.valueOf(letter), null, result, gameID, player.getName());
        for (Player playing : players) {
            if (playing.isBot()) playing.notifyLetter(letter);
        }
        return result;
    }

    /**
     * Executes the phrase guessing action. Only the {@link Game#currentPlayer current player} is able to guess, unless
     * the round being currently played is round 2, when all players can guess. It's also used in the final round for
     * providing the four letters to uncover in the first phase of the round. If the guess if correct, the round is
     * advanced
     *
     * @param player {@link Player player} who is trying to guess the phrase
     * @param phrase phrase being guessed
     */
    public void guessPhrase(Player player, String phrase) {
        boolean result;
        if (state != GameState.FINAL) {
            if (currentPlayer == null || player != currentPlayer || (moveState != MoveState.CAN_BUY_VOWEL_SPIN_OR_GUESS && hasNotGuessedConsonants())) {
                if (state != GameState.ROUND4) return;
            }
            result = gameWord.guessPhrase(phrase);
            if (window != null) {
                window.writeToGameLog("Player " + player.getName() + " tried to guess " + phrase + " and " + (result ? "succeeded!" : "failed."));
            }
            reportActionToServer(player, "guessp:" + phrase);
            if(gameServer != null) database.insertMove(-174, null, phrase, result ? 1 : 0, gameID, player.getName());
            if (!result) {
                assignNextPlayer();
                nextMove();
            } else {
                if (state == GameState.ROUND4) {
                    currentPlayer = player;
                    roundScores.put(player, 1000);
                }
                nextMove();
                beingExecutedByServer = false;
                advanceRound();
            }
        } else if (player == winner) {
            if (moveState == MoveState.HAS_TO_GUESS_CONSONANT) {
                ArrayList<Character> lettersToUncover = getLettersToUncover(phrase);
                for (char letter : lettersToUncover) {
                    gameWord.guessLetter(letter);
                }
                moveState = MoveState.CAN_BUY_VOWEL_SPIN_OR_GUESS;
                nextMove();
                reportActionToServer(player, "guessp:" + phrase);
                if(gameServer != null) database.insertMove(-174, null, phrase, 0, gameID, player.getName());
            } else if (moveState == MoveState.CAN_BUY_VOWEL_SPIN_OR_GUESS) {
                result = gameWord.guessPhrase(phrase);
                if (window != null) {
                    window.writeToGameLog("Player " + player.getName() + " tried to guess " + phrase + " and " + (result ? "succeeded!" : "failed."));
                }
                if (result) {
                    roundScores.put(winner, wheel.getLastRolled());
                }
                reportActionToServer(player, "guessp:" + phrase);
                if(gameServer != null) database.insertMove(-174, null, phrase, result ? 1 : 0, gameID, player.getName());
                beingExecutedByServer = false;
                advanceRound();
            }
        }
        if (window != null) {
            window.updateGUI();
        }
    }

    /**
     * Assigns the round starter player according to the current round number
     *
     * @return {@link Player player} who starts the current round
     */
    private Player assignRoundStarter() {
        return switch (state) {
            case NOT_STARTED, ROUND4, ENDED -> null;
            case ROUND1, ROUND2 -> players.get(0);
            case ROUND3 -> players.get(1);
            case ROUND5 -> players.get(2);
            case FINAL -> winner;
        };
    }

    /**
     * Sums up everything that has happened in the current round and begins the next one. Adds points to the total of
     * the player who won the current round and generates a new phrase for the next round, if the game has ended, saves
     * game result to the database. If the next round is the final one, uncovers automatically letters "rstlne".
     */
    private void advanceRound() {
        moverTimer.stop();
        moveTimeoutTimer.stop();
        if (gameServer == null && !beingExecutedByServer) return; // only the server can advance rounds
        Database database = Database.getInstance();
        if (state == GameState.FINAL) {
            scores.put(winner, scores.get(winner) + roundScores.get(winner));
            if(gameServer != null) {
                if(!database.saveGameResult(winner.getName(), scores.get(winner), gameID) && window != null) {
                    window.errorPrompt.setText("Failed to save the record!");
                }
            }
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
            for (char letter : toUncover.toCharArray()) {
                gameWord.guessLetter(letter);
            }
        }
        roundStarter = assignRoundStarter();
        currentPlayer = roundStarter;
        if (state == GameState.ENDED) {
            currentPlayer = null;
            inProgress = false;
        }
        moveState = (state == GameState.FINAL && gameServer != null) ? MoveState.HAS_TO_GUESS_CONSONANT : MoveState.HAS_TO_SPIN;
        nextMove();
        if (window != null) {
            window.updateGUI();
            window.updateWheel();
        }
        reportActionToServer(null, "newword:" + gameWord.getPhrase() + ":" + category);
        if ((state == GameState.ROUND2 || state == GameState.FINAL) && !beingExecutedByServer) {
            int prizeForLetter = wheel.spin(state);
            reportActionToServer(null, "spin:" + prizeForLetter);
            if(gameServer != null) database.insertMove(prizeForLetter, null, null, null, gameID, "SYSTEM");
        }
        for (Player playing : players) {
            if (playing.isBot()) playing.notifyNewRound();
        }
    }

    public String getPhrase() {
        if(gameWord == null) return null;
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

    /**
     * Obtains a text interpretation of the value rolled on the {@link Wheel wheel}
     *
     * @return text containing information about the current wheel value
     */
    public String getLastRolled() {
        String lastRolled;
        switch (wheel.getLastRolled()) {
            case -2 -> lastRolled = "Roll again";
            case -1 -> lastRolled = "Pass";
            case -3 -> lastRolled = " ";
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

    /**
     * Obtains a {@link Player player} with provided name. If the provided name is SYSTEM, the current moving player is
     * returned (server makes a move on behalf of the chosen player)
     *
     * @param name name of the player to find
     * @return {@link Player player} with the given name, of null if they weren't found or the current round is the
     * final one
     */
    public Player getPlayerByName(String name) {
        if (name.equals("SYSTEM")) {
            if (state == GameState.FINAL) return null;
            currentPlayer = currentPlayer == null ? players.get(0) : currentPlayer;
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

    /**
     * Executes a command that arrived from the {@link NetworkClient network client}
     *
     * @param jsonData         data obtained from the network client
     * @param executedOnServer if set to true, it means that this game is an instance created by the server, and certain
     *                         actions like advancing rounds can be executed independently
     */
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
                if(players.size() > 0) {
                    start();
                }
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

    /**
     * Reports a player's action (or the beginning of a new round with a new phrase) to the server. This method will
     * instantly end if it's executed in a game running on a server
     *
     * @param player {@link Player player} who executed an action in the game (or SYSTEM if a new word is assigned)
     * @param action description of the executed action
     */
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
            case "spin" -> response.put("value", Integer.parseInt(parts[1]));
            case "guessl" -> response.put("letter", parts[1]);
            case "guessp" -> response.put("phrase", parts[1]);
            case "newword" -> {
                response.put("word", parts[1]);
                response.put("cat", parts[2]);
            }
            case "uncov" -> response.put("index", Integer.parseInt(parts[1]));
        }
        if (gameServer == null) { // local game
            networkClient.sendData(response.toString());
        } else { // running on server
            gameServer.tellEveryoneBut(response.toString(), player, this);
        }
    }

    /**
     * Resets all fields
     */
    public void reset() {
        inProgress = false;
        ArrayList<Player> playersCopy = new ArrayList<>(players);
        for (Player player : playersCopy) {
            leaveGame(player);
        }
        scores.clear();
        roundScores.clear();
        state = GameState.NOT_STARTED;
        moveTimeoutTimer.stop();
        moverTimer.stop();
        wheel.reset();
    }

    public MoveState getMoveState() {
        return moveState;
    }

    /**
     * Returns an array of characters to uncover in the final round based on the winner's input
     *
     * @param phrase string of letters that the winner has requested to uncover
     * @return an {@link ArrayList array} of characters to uncover, containing at most 1 vowel and 3 consonants
     */
    private ArrayList<Character> getLettersToUncover(String phrase) {
        ArrayList<Character> toUncover = new ArrayList<>();
        int vowelCount = 0, consonantCount = 0;
        for (char letter : phrase.toCharArray()) {
            if (GameWord.vowels.contains(letter) && vowelCount < 1) {
                toUncover.add(letter);
                vowelCount++;
            } else if (GameWord.consonants.contains(letter) && consonantCount < 3) {
                toUncover.add(letter);
                consonantCount++;
            }
            if (vowelCount >= 1 && consonantCount >= 3) break;
        }
        return toUncover;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }
}
