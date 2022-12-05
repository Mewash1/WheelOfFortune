import javax.swing.*;

public class GameWindowGUI extends JFrame {
    private JButton newGameButton;
    private JProgressBar RoundProgress;
    private JButton guessLetter;
    private JButton fullGuess;
    private JTable playersScores;
    private JButton helpButton;
    private JLabel pricePool;
    private JLabel currentPlayer;
    private JTextField playerInput;
    private JTextField roundSollution;
    private JPanel mainPannel;
    private JPanel leftMenu;
    private JPanel rightMenu;
    private JToolBar topToolBar;
    private JSplitPane mainDivider;
    private JSplitPane leftMenuVertDivider;
    private JList guessesHistory;
    private JScrollPane guessLog;
    private JPanel topPanel;
    private JPanel bottomPanel;
    private JCheckBox visibleCheckBox;
    private JLabel roundNr3Label;
    private JButton addPlayerButton;

    public GameWindowGUI() {
        setTitle("WheelOfFortune");
        setSize(450, 300);
        setDefaultCloseOperation((WindowConstants.EXIT_ON_CLOSE));
        setVisible(true);
    }

    public static void main(String[] args) {
        GameWindowGUI myFrame = new GameWindowGUI();
    }
}
