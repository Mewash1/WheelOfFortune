package pap.z26.wheeloffortune;

public class WheelOfFortune {
    public static void main(String[] args) {
        Game game = new Game();
        HumanPlayer ourPlayer = new HumanPlayer(null);
        GameWindowGUI gameWindow = new GameWindowGUI(game, ourPlayer);
        game.setGameWindow(gameWindow);

        game.joinGame(ourPlayer);
        game.joinGame(new BotPlayer("Jeff"));
        game.joinGame(new BotPlayer("Andruszkiewicz"));
    }
}