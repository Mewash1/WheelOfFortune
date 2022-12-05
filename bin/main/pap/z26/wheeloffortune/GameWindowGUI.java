import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameWindowGUI extends JFrame {
    private JButton newGameButton;
    private JProgressBar RoundProgress;
    private JButton guessLetter;
    private JButton fullGuess;
    private JTable playersScores;
    private JButton helpButton;
    private JLabel pricePool;
    private JLabel currentPlayer;
    private JPasswordField playerInput;
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
    private JButton spinWheelButton;

    public GameWindowGUI() {
        setContentPane(mainPannel;
        setTitle("WheelOfFortune");
        setSize(450, 300);
        setDefaultCloseOperation((WindowConstants.EXIT_ON_CLOSE));
        setVisible(true);
        guessLetter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(playerInput.getText());
            }
        });
        fullGuess.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(playerInput.getText());
            }
        });
        visibleCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBox c = (JCheckBox) e.getSource();
                playerInput.setEchoChar(c.isSelected() ? '\u0000' : (char) 0);
            }
        });
        spinWheelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Brryyyyt");
            }
        });
        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("We are rich");
            }
        });
        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Nope");

            }
        });
        addPlayerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Hello player nb 2");

            }
        });
    }

    public static void main(String[] args) {
        GameWindowGUI myFrame = new GameWindowGUI();
    }
}
