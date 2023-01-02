package pap.z26.wheeloffortune;

public class WheelOfFortune {
    public static void main(String[] args) {
        Game game = new Game();
        HumanPlayer ourPlayer = new HumanPlayer(null);
        WoF_GUI gameWindow = new WoF_GUI(game, ourPlayer);
        game.setGameWindow(gameWindow);

        game.joinGame(ourPlayer);
        game.joinGame(new BotPlayer("Kot"));
        game.joinGame(new BotPlayer("Duke"));
    }
}