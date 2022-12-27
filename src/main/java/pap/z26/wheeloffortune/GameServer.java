package pap.z26.wheeloffortune;

import java.util.ArrayList;

public class GameServer {

    private NetworkClient networkClient;
    private ArrayList<Game> games = new ArrayList<>();

    public GameServer() {
        networkClient = new NetworkClient(26969, this);
        networkClient.run();
    }

    public void receiveDataFromServer(String data) {
        // nop
    }
}
