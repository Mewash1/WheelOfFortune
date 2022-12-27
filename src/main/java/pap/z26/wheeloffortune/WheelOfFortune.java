package pap.z26.wheeloffortune;

public class WheelOfFortune {

    private Game game;
    private HumanPlayer ourPlayer;
    private GameWindowGUI gameWindow;

    public void receiveDataFromServer(String data) {
        // nop
    }

    public void setNetworkClient(NetworkClient client) {
        game.setNetworkClient(client);
    }

    public WheelOfFortune() {
        game = new Game();
        ourPlayer = new HumanPlayer(null);
        gameWindow = new GameWindowGUI(game, ourPlayer);
        game.setGameWindow(gameWindow);

        game.joinGame(ourPlayer);
        game.joinGame(new BotPlayer("Kot"));
        game.joinGame(new BotPlayer("Duke"));
    }
}