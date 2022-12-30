package pap.z26.wheeloffortune;

import org.json.JSONArray;
import org.json.JSONObject;

public class WheelOfFortune {

    public Game game;
    public HumanPlayer ourPlayer;
    private GameWindowGUI gameWindow;
    private NetworkClient networkClient;

    public void receiveDataFromServer(String data) {
        JSONObject jsonData = new JSONObject(data);
        String action = jsonData.getString("action");
        if(action.equals("newword") || action.equals("spin") || action.equals("guessl") || action.equals("guessp") || action.equals("start")) {
            game.executeFromServer(new JSONObject());
        } else {
            switch(action) {
                case "loginconf" -> {
                    System.out.println(jsonData.getString("message"));
                }
                case "joinconf" -> {
                    JSONArray players = jsonData.getJSONArray("players");
                    for(int i=0; i<players.length(); i++) {
                        if(ourPlayer.getName().equals(players.getString(i))) {
                            game.joinGame(ourPlayer);
                        } else {
                            game.joinGame(new HumanPlayer(players.getString(i)));
                        }
                    }
                }
                case "joinoth" -> {
                    game.joinGame(new HumanPlayer(jsonData.getString("player")));
                }
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

    public void joinGame() {
        JSONObject joinData = new JSONObject();
        joinData.put("action", "join");
        joinData.put("player", ourPlayer.getName());
        networkClient.sendData(joinData.toString());
    }

    public WheelOfFortune() {
        game = new Game();
        ourPlayer = new HumanPlayer("You");
        gameWindow = new GameWindowGUI(this);
        game.setGameWindow(gameWindow);
    }
}