import javax.swing.*;

public class GameWindow{
    private JButton newGameButton;
    private JProgressBar RoundProgress;
    private JButton guessLetter;
    private JButton ALLINGUESSFULLButton;
    private JTable playersScores;
    private JButton helpButton;
    private JList guessesHistory;
    private JPanel buttonsPanel;
    private JTextField pricePool;
    private JTextField currentPlayer;
    private JPasswordField playerInput;
    private JTextField roundSollution;

    public static void main(String[] args) {
        GameWindow win = new GameWindow();
    }
}
