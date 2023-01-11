package pap.z26.wheeloffortune;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.SQLException;

public class WheelOfFortune {

    public Game game;
    public HumanPlayer ourPlayer;
    private final WoF_GUI gameWindow;
    private NetworkClient networkClient;
    private String currentIp = "";
    private int currentPort = 0;

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
                if(jsonData.getString("message").equals("success")) {
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
                } catch (SQLException ignored) {}

            }
            default -> game.executeFromOutside(jsonData, false);
        }
    }

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

    public void loginToGameServer() {
        JSONObject loginData = new JSONObject();
        loginData.put("action", "login");
        loginData.put("player", ourPlayer.getName());
        networkClient.sendData(loginData.toString());
        updateDatabase();
    }

    public void logout() {
        JSONObject logoutData = new JSONObject();
        logoutData.put("action", "logout");
        logoutData.put("player", ourPlayer.getName());
        networkClient.sendData(logoutData.toString());
    }

    public void joinGame() {
        JSONObject joinData = new JSONObject();
        joinData.put("action", "join");
        joinData.put("player", ourPlayer.getName());
        networkClient.sendData(joinData.toString());
    }

    public void leaveGame() {
        JSONObject leaveData = new JSONObject();
        leaveData.put("action", "leave");
        leaveData.put("player", ourPlayer.getName());
        networkClient.sendData(leaveData.toString());
        game.reset();
    }

    private void loginAndJoin() {
        JSONObject lajData = new JSONObject();
        lajData.put("action", "laj");
        lajData.put("player", ourPlayer.getName());
        networkClient.sendData(lajData.toString());
        updateDatabase();
    }

    public String join(String serverIp, int serverPort) {
        if(serverIp.equals(currentIp) && serverPort == currentPort) { // just need to join
            joinGame();
        } else {
            currentIp = serverIp;
            currentPort = serverPort;
            logout();
            if(!networkClient.setServerAddress(serverIp, serverPort)) return "Invalid IP";
            loginAndJoin();
        }
        return "Success";
    }

    public void requestGameJoin(String serverIp, String serverPort) {
        int port;
        try {
            port = Integer.parseInt(serverPort);
        } catch (NumberFormatException e) {
            port = -1;
        }
        if(port < 1 || port > 65535) {
            gameWindow.setJoinMessage("Invalid port number");
            return;
        }
        String response = join(serverIp, port);
        if(response.equals("Success")) {
            gameWindow.setJoinMessage("Connecting...");
        } else {
            gameWindow.setJoinMessage(response);
        }
    }

    public void updatePlayerName(String newName) {
        if(!ourPlayer.getName().equals(newName)) {
            logout();
            ourPlayer.setName(newName);
            currentIp = "invalid";
        }
    }

    public void startGame() {
        JSONObject startData = new JSONObject();
        startData.put("action", "start");
        startData.put("player", ourPlayer.getName());
        networkClient.sendData(startData.toString());
    }

    public void updateDatabase(){
        if(currentIp.equals("localhost") || currentIp.equals("127.0.0.1")) return;
        JSONObject updateData = new JSONObject();
        updateData.put("action", "update");
        updateData.put("player", ourPlayer.getName());
        networkClient.sendData(updateData.toString());
    }

    public WheelOfFortune() {
        game = new Game();
        ourPlayer = new HumanPlayer("You");
        gameWindow = new WoF_GUI(this);
        game.setGameWindow(gameWindow);
    }
}