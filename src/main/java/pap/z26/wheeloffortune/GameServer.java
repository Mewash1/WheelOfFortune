package pap.z26.wheeloffortune;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

public class GameServer {

    private NetworkClient networkClient;
    private ArrayList<Game> games = new ArrayList<>();

    private record IpAndPort(InetAddress address, int port) {
    }

    private HashMap<Player, IpAndPort> addresses = new HashMap<>();
    private HashMap<String, Player> players = new HashMap<>();

    public GameServer() {
        networkClient = new NetworkClient(26969, this);
        networkClient.start();
    }

    public void receiveDataFromClient(String data, InetAddress ipAddress, int port) {
        JSONObject jsonData = new JSONObject(data);
        String action = jsonData.getString("action");
        switch (action) {
            case "login" -> {
                JSONObject response = new JSONObject();
                response.put("action", "loginconf");
                response.put("message", login(jsonData, ipAddress, port));
                networkClient.sendData(response.toString(), ipAddress, port);
            }
            case "logout" -> {
                Player loggingOut = players.get(jsonData.getString("player"));
                if(loggingOut == null) return;
                if (loggingOut.getGame() != null) {
                    leaveGame(jsonData);
                }
                players.remove(jsonData.getString("player"));
                addresses.remove(loggingOut);
            }
            case "join" -> {
                JSONObject response = new JSONObject();
                response.put("action", "joinconf");
                Player joiningPlayer = players.get(jsonData.getString("player"));
                response = join(jsonData, response, joiningPlayer);
                IpAndPort joiningPlayerAddress = addresses.get(joiningPlayer);
                networkClient.sendData(response.toString(), joiningPlayerAddress.address, joiningPlayerAddress.port);
                joinOth(joiningPlayer);
            }
            case "laj" -> {
                JSONObject response = new JSONObject();
                response.put("action", "lajconf");
                String loginResult = login(jsonData, ipAddress, port);
                response.put("message", loginResult);
                if(loginResult.equals("success")) {
                    Player joiningPlayer = players.get(jsonData.getString("player"));
                    response = join(jsonData, response, joiningPlayer);
                    IpAndPort joiningPlayerAddress = addresses.get(joiningPlayer);
                    networkClient.sendData(response.toString(), joiningPlayerAddress.address, joiningPlayerAddress.port);
                    joinOth(joiningPlayer);
                }
            }
            case "leave" -> {
                leaveGame(jsonData);
            }
            case "start" -> {
                Player playerStarting = players.get(jsonData.getString("player"));
                Game gameToStart = playerStarting.getGame();
                if(gameToStart.isInProgress()) return;
                while (gameToStart.getPlayers().size() < 3) {
                    BotPlayer fillerPlayer = new BotPlayer();
                    while (!gameToStart.joinGame(fillerPlayer)) fillerPlayer = new BotPlayer();
                    JSONObject joinothResponse = new JSONObject();
                    joinothResponse.put("action", "joinoth");
                    joinothResponse.put("player", fillerPlayer.getName());
                    for (Player playerToNotify : gameToStart.getPlayers()) {
                        if (!playerToNotify.isBot()) {
                            IpAndPort alreadyInGamePlayerAddress = addresses.get(playerToNotify);
                            networkClient.sendData(joinothResponse.toString(), alreadyInGamePlayerAddress.address, alreadyInGamePlayerAddress.port);
                        }
                    }
                }
                for (Player inGamePlayer : gameToStart.getPlayers()) {
                    if (!inGamePlayer.isBot()) {
                        IpAndPort address = addresses.get(inGamePlayer);
                        networkClient.sendData(data, address.address, address.port);
                    }
                }
                gameToStart.startGame();
            }
            default -> {
                Player playerStarting = players.get(jsonData.getString("player"));
                Game gameToStart = playerStarting.getGame();
                gameToStart.executeFromOutside(jsonData, true);
            }
        }
    }

    public void tellEveryoneBut(String data, Player player, Game game) {
        for (Player gamePlayer : game.getPlayers()) {
            if (gamePlayer != player && !gamePlayer.isBot()) {
                IpAndPort address = addresses.get(gamePlayer);
                networkClient.sendData(data, address.address, address.port);
            }
        }
    }

    private void leaveGame(JSONObject jsonData) {
        Player leavingPlayer = players.get(jsonData.getString("player"));
        Game gameBeingLeft = leavingPlayer.getGame();
        JSONObject response = new JSONObject();
        response.put("action", "left");
        response.put("player", jsonData.getString("player"));
        if (gameBeingLeft.isInProgress()) {
            BotPlayer replacement = gameBeingLeft.leaveGameAndReplace(leavingPlayer);
            response.put("repl", replacement.getName());
        } else {
            gameBeingLeft.leaveGame(leavingPlayer);
        }
        boolean onlyBots = true;
        for(Player inGamePlayer: gameBeingLeft.getPlayers()) {
            if(!inGamePlayer.isBot()) {
                onlyBots = false;
                break;
            }
        }
        if(onlyBots) {
            gameBeingLeft.reset();
        } else {
            tellEveryoneBut(response.toString(), leavingPlayer, gameBeingLeft);
        }
    }

    private String login(JSONObject jsonData, InetAddress ipAddress, int port) {
        for (Player player : addresses.keySet()) {
            if (player.getName().equals(jsonData.getString("player"))) {
               return "You are already logged in";
            }
        }
        if (jsonData.getString("player").equals("SYSTEM")) {
            return "Invalid user name";
        }
        HumanPlayer newPlayer = new HumanPlayer(jsonData.getString("player"));
        addresses.put(newPlayer, new IpAndPort(ipAddress, port));
        players.put(jsonData.getString("player"), newPlayer);
        return "success";
    }

    private JSONObject join(JSONObject jsonData, JSONObject response, Player joiningPlayer) {
        for (Game game : games) {
            if (game.getPlayers().size() < 3) {
                if (game.joinGame(joiningPlayer)) {
                    break;
                }
            }
        }
        if (joiningPlayer.getGame() == null) {
            Game newGame = new Game(this);
            games.add(newGame);
            newGame.joinGame(joiningPlayer);
        }
        JSONArray inGamePlayers = new JSONArray();
        for (Player inGamePlayer : joiningPlayer.getGame().getPlayers()) {
            inGamePlayers.put(inGamePlayer.getName());
        }
        response.put("players", inGamePlayers);
        return response;
    }

    private void joinOth(Player joiningPlayer) {
        JSONObject responseToOthers = new JSONObject();
        responseToOthers.put("action", "joinoth");
        responseToOthers.put("player", joiningPlayer.getName());
        for (Player inGamePlayer : joiningPlayer.getGame().getPlayers()) {
            if (inGamePlayer != joiningPlayer) {
                IpAndPort alreadyInGamePlayerAddress = addresses.get(inGamePlayer);
                networkClient.sendData(responseToOthers.toString(), alreadyInGamePlayerAddress.address, alreadyInGamePlayerAddress.port);
            }
        }
    }
}