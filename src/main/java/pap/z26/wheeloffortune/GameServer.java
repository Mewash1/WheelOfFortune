package pap.z26.wheeloffortune;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

public class GameServer {

    private NetworkClient networkClient;
    private ArrayList<Game> games = new ArrayList<>();
    private record IpAndPort(InetAddress address, int port) {}
    private HashMap<Player, IpAndPort> addresses = new HashMap<>();
    private HashMap<String, Player> players = new HashMap<>();

    public GameServer() {
        networkClient = new NetworkClient(26969, this);
        networkClient.start();
    }

    public void receiveDataFromClient(String data, InetAddress ipAddress, int port) {
        System.out.println("[Local server, data from " + ipAddress + ":" + port + "]: " + data);
        JSONObject jsonData = new JSONObject(data);
        String action = jsonData.getString("action");
        switch (action) {
            case "login" -> {
                JSONObject response = new JSONObject();
                response.put("action", "loginconf");
                for(Player player: addresses.keySet()) {
                    if(player.getName().equals(jsonData.getString("player"))) {
                        response.put("message", "You are already logged in");
                    }
                }
                if(!response.has("message")) {
                    response.put("message", "success");
                    addresses.put(new HumanPlayer(jsonData.getString("player")), new IpAndPort(ipAddress, port));
                }
                networkClient.sendData(response.toString(), ipAddress, port);
            }
            case "join" -> {
                Player joiningPlayer = players.get(jsonData.getString("player"));
                for(Game game: games) {
                    if(game.getPlayers().size() < 3) {
                        if(game.joinGame(joiningPlayer)) {
                            break;
                        }
                    }
                }
                if(joiningPlayer.getGame() == null) {
                    Game newGame = new Game(this);
                    newGame.joinGame(joiningPlayer);
                }
                JSONArray inGamePlayers = new JSONArray();
                for(Player inGamePlayer: joiningPlayer.getGame().getPlayers()) {
                    inGamePlayers.put(inGamePlayer.getName());
                }
                JSONObject response = new JSONObject();
                response.put("action", "joinconf");
                response.put("players", inGamePlayers);
                IpAndPort joiningPlayerAddress = addresses.get(joiningPlayer);
                networkClient.sendData(response.toString(), joiningPlayerAddress.address, joiningPlayerAddress.port);
                JSONObject responseToOthers = new JSONObject();
                responseToOthers.put("action", "joinoth");
                responseToOthers.put("player", joiningPlayer.getName());
                for(Player inGamePlayer: joiningPlayer.getGame().getPlayers()) {
                    if(inGamePlayer != joiningPlayer) {
                        IpAndPort alreadyInGamePlayerAddress = addresses.get(inGamePlayer);
                        networkClient.sendData(responseToOthers.toString(), alreadyInGamePlayerAddress.address, alreadyInGamePlayerAddress.port);
                    }
                }
            }
            default -> {
                Player playerStarting = players.get(jsonData.getString("player"));
                Game gameToStart = playerStarting.getGame();
                gameToStart.executeFromServer(jsonData);
                for(Player inGamePlayer: gameToStart.getPlayers()) {
                    if(inGamePlayer != playerStarting) {
                        IpAndPort address = addresses.get(inGamePlayer);
                        networkClient.sendData(data, address.address, address.port);
                    }
                }
            }
        }
    }

    public void tellEveryoneBut(String data, Player player, Game game) {
        for(Player gamePlayer: game.getPlayers()) {
            if(gamePlayer != player && !gamePlayer.isBot()) {
                IpAndPort address = addresses.get(gamePlayer);
                networkClient.sendData(data, address.address, address.port);
            }
        }
    }
}
