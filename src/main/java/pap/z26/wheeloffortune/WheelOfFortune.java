package pap.z26.wheeloffortune;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.SQLException;

/**
 * Class that manages communication between the {@link NetworkClient network client}, the {@link Game game} instance and
 * the {@link WoF_GUI game window}
 */
public class WheelOfFortune {

    public Game game;
    public HumanPlayer ourPlayer;
    private final WoF_GUI gameWindow;
    private NetworkClient networkClient;
    private String currentIp = "";
    private int currentPort = 0;

    /**
     * Receives the data from the {@link NetworkClient network client} and executes actions according to the received
     * data
     *
     * @param data JSON with a response to a request made by the client or an information about a move in a game
     */
    public void receiveDataFromServer(String data) {
        JSONObject jsonData = new JSONObject(data);
        String action = jsonData.getString("action");
        switch (action) {
            case "loginconf" -> {
                if (jsonData.getString("message").equals("success")) {
                    gameWindow.writeToGameLog("Successfully logged in on the server");
                } else {
                    gameWindow.writeToGameLog(jsonData.getString("message"));
                }
            }
            case "joinconf" -> joinpart(jsonData);
            case "lajconf" -> {
                if (jsonData.getString("message").equals("success")) {
                    joinpart(jsonData);
                } else {
                    gameWindow.setJoinMessage(jsonData.getString("message"));
                }
            }
            case "joinoth" -> game.joinGame(new HumanPlayer(jsonData.getString("player")));
            case "update" -> {
                Database db = Database.getInstance();
                try {
                    db.updateDatabase(jsonData.getJSONObject("phrases"), jsonData.getJSONObject("leaderboard"));
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
            default -> game.executeFromOutside(jsonData, false);
        }
    }

    /**
     * Reacts to a game join confirmation by adding replicas of the players already in game on the server and switching
     * the current card to the game page
     *
     * @param jsonData JSON with data about other players in the game
     */
    private void joinpart(JSONObject jsonData) {
        JSONArray players = jsonData.getJSONArray("players");
        for (int i = 0; i < players.length(); i++) {
            if (ourPlayer.getName().equals(players.getString(i))) {
                game.joinGame(ourPlayer);
            } else {
                game.joinGame(new HumanPlayer(players.getString(i)));
            }
        }
        gameWindow.switchToGameCard();
    }

    public void setNetworkClient(NetworkClient client) {
        networkClient = client;
        game.setNetworkClient(client);
    }

    /**
     * Creates and sends a request to log in to a game server
     */
    public void loginToGameServer() {
        JSONObject loginData = new JSONObject();
        loginData.put("action", "login");
        loginData.put("player", ourPlayer.getName());
        networkClient.sendData(loginData.toString());
        updateDatabase();
    }

    /**
     * Creates and sends a request to log out from the game server
     */
    public void logout() {
        JSONObject logoutData = new JSONObject();
        logoutData.put("action", "logout");
        logoutData.put("player", ourPlayer.getName());
        networkClient.sendData(logoutData.toString());
    }

    /**
     * Creates and sends a request to join a game on the server the client is currently connected to
     */
    public void joinGame() {
        JSONObject joinData = new JSONObject();
        joinData.put("action", "join");
        joinData.put("player", ourPlayer.getName());
        networkClient.sendData(joinData.toString());
    }

    /**
     * Creates and sends a request to leave a game that the user is currently playing
     */
    public void leaveGame() {
        JSONObject leaveData = new JSONObject();
        leaveData.put("action", "leave");
        leaveData.put("player", ourPlayer.getName());
        networkClient.sendData(leaveData.toString());
        game.reset();
    }

    /**
     * Creates and sends a request which both logs in the player on the server and makes them join a game
     */
    private void loginAndJoin() {
        JSONObject lajData = new JSONObject();
        lajData.put("action", "laj");
        lajData.put("player", ourPlayer.getName());
        networkClient.sendData(lajData.toString());
        updateDatabase();
    }

    /**
     * Method that is being called as a result of the join game button being pressed
     *
     * @param serverIp   IP of the server to join a game on
     * @param serverPort port of the server
     * @return text message with an error or "Success" if the request to join was successfully sent
     */
    public String join(String serverIp, int serverPort) {
        if (serverIp.equals(currentIp) && serverPort == currentPort) { // just need to join
            joinGame();
        } else {
            currentIp = serverIp;
            currentPort = serverPort;
            logout();
            if (!networkClient.setServerAddress(serverIp, serverPort)) return "Invalid IP";
            loginAndJoin();
        }
        return "Success";
    }

    /**
     * Requests a game join on the server
     *
     * @param serverIp   IP of the server where the request will be sent
     * @param serverPort port of the server
     */
    public void requestGameJoin(String serverIp, String serverPort) {
        int port;
        try {
            port = Integer.parseInt(serverPort);
        } catch (NumberFormatException e) {
            port = -1;
        }
        if (port < 1 || port > 65535) {
            gameWindow.setJoinMessage("Invalid port number");
            return;
        }
        String response = join(serverIp, port);
        if (response.equals("Success")) {
            gameWindow.setJoinMessage("Connecting...");
        } else {
            gameWindow.setJoinMessage(response);
        }
    }

    /**
     * Changes local player's name and logs them out from the server they're currently connected to
     *
     * @param newName new name to assign to the local player
     */
    public void updatePlayerName(String newName) {
        if (!ourPlayer.getName().equals(newName)) {
            logout();
            ourPlayer.setName(newName);
            currentIp = "invalid";
        }
    }

    /**
     * Creates and sends a request to start the game
     */
    public void startGame() {
        JSONObject startData = new JSONObject();
        startData.put("action", "start");
        startData.put("player", ourPlayer.getName());
        networkClient.sendData(startData.toString());
    }

    /**
     * Creates and sends a request to update the local words, categories and high scores database
     */
    public void updateDatabase() {
//        if ((currentIp.equals("localhost") || currentIp.equals("127.0.0.1")) && currentPort == 26969) return;
//        JSONObject updateData = new JSONObject();
//        updateData.put("action", "update");
//        updateData.put("player", ourPlayer.getName());
//        networkClient.sendData(updateData.toString());
    }

    public WheelOfFortune() {
        game = new Game();
        ourPlayer = new HumanPlayer("You");
        gameWindow = new WoF_GUI(this);
        game.setGameWindow(gameWindow);
    }
}