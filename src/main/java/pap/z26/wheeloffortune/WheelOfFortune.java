package pap.z26.wheeloffortune;

import org.json.JSONArray;
import org.json.JSONObject;

public class WheelOfFortune {

    public Game game;
    public HumanPlayer ourPlayer;
    private WoF_GUI gameWindow;
    private NetworkClient networkClient;
    private String currentIp = "";

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
            case "joinconf" -> {
                JSONArray players = jsonData.getJSONArray("players");
                for (int i = 0; i < players.length(); i++) {
                    if (ourPlayer.getName().equals(players.getString(i))) {
                        game.joinGame(ourPlayer);
                    } else {
                        game.joinGame(new HumanPlayer(players.getString(i)));
                    }
                }
            }
            case "lajconf" -> {
                if(jsonData.getString("message").equals("success")) {
                    JSONArray players = jsonData.getJSONArray("players");
                    for (int i = 0; i < players.length(); i++) {
                        if (ourPlayer.getName().equals(players.getString(i))) {
                            game.joinGame(ourPlayer);
                        } else {
                            game.joinGame(new HumanPlayer(players.getString(i)));
                        }
                    }
                } else {
                    gameWindow.writeToGameLog(jsonData.getString("message"));
                }
            }
            case "joinoth" -> {
                game.joinGame(new HumanPlayer(jsonData.getString("player")));
            }
            default -> {
                game.executeFromOutside(jsonData, false);
            }
        }
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
    }

    private void loginAndJoin() {
        JSONObject lajData = new JSONObject();
        lajData.put("action", "laj");
        lajData.put("player", ourPlayer.getName());
        networkClient.sendData(lajData.toString());
    }

    public String join(String serverIp) {
        if(serverIp.equals(currentIp)) { // just need to join
            joinGame();
        } else {
            currentIp = serverIp;
            logout();
            if(!networkClient.setServerAddress(serverIp)) return "Invalid IP";
            loginAndJoin();
        }
        return "Success";
    }


    public void startGame() {
        JSONObject startData = new JSONObject();
        startData.put("action", "start");
        startData.put("player", ourPlayer.getName());
        networkClient.sendData(startData.toString());
    }

    public WheelOfFortune() {
        game = new Game();
        ourPlayer = new HumanPlayer("You");
        gameWindow = new WoF_GUI(this);
        game.setGameWindow(gameWindow);
    }
}