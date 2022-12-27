package pap.z26.wheeloffortune;

public class Main {

    public static void main(String[] args) {
        WheelOfFortune game = new WheelOfFortune();
        NetworkClient networkClient = new NetworkClient("localhost", 26969, game);
        networkClient.run();
        GameServer localServer = new GameServer();
    }
}
