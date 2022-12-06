package pap.z26.wheeloffortune;

public class WheelOfFortune {
    public static void main(String[] args) {
        System.out.println("PAP Z26");
        Game game = new Game();
        HumanPlayer ourPlayer = new HumanPlayer(game);
        GameWindowGUI myFrame = new GameWindowGUI(game, ourPlayer);
    }
}