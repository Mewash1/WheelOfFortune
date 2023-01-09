package pap.z26.wheeloffortune;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Locale;

public class WoF_GUI extends JFrame {
    private JButton newGameButton;
    private JProgressBar roundProgress;
    private JButton guessLetter;
    private JButton fullGuess;
    private JTable playersScores;
    private JButton helpButton;
    private JLabel pricePool;
    private JLabel currentPlayer;
    private JPasswordField playerInput;
    private JTextField roundSollution;
    private JPanel mainCardLayout;
    private JList guessesHistory;
    private JCheckBox visibleCheckBox;
    private JLabel roundNr3Label;
    private JButton addPlayerButton;
    private JButton spinWheelButton;
    private JToolBar topToolBar;
    private JSplitPane mainDivider;
    private JPanel leftMenu;
    private JPanel bottomPanel;
    private JPanel topPanel;
    private JScrollPane guessLog;
    private JPanel MainMenuPanel;
    private JPanel GamePanel;
    private JButton SinglePlayerButton;
    private JButton MultiPlayerButton;
    private JButton CreditsButton;
    private JButton ExitButton;
    private JPasswordField ipInput;
    private JPanel menuButtons;
    private JTextField titleText;
    private JPanel HighScoressPanel;
    private JTable HighScoresTable;
    private JLabel HighScoresLabel;
    private JButton backButton;
    private JPanel ScoresButtonPannel;
    private JButton LeaveGameButton;
    private JButton HighScoresButton;
    private JTextField nameField;
    private JPanel MultiLoginPanel;
    private JButton mainMenuButton;
    private JButton joinGameButton;
    private JPasswordField ipInputField;
    private JLabel infoField;
    private JCheckBox ipVisibityCheckBox;
    private JTextField nameLogMessage;
    private JTextField ipLogMessage;

    private final DefaultListModel<String> listModel = new DefaultListModel<>();

    private Game game;

    public void writeToGameLog(String content) {
        listModel.add(0, content);
        guessesHistory.setModel(listModel);
    }

    public void updateGUI() {
        if (game.getState() != GameState.ENDED) {
            String phrase = game.getPhrase();
            if (!game.hasNotGuessedConsonants()) {
                phrase += " [NO CONSONANTS LEFT]";
            }
            roundSollution.setText(phrase);
        } else {
            roundSollution.setText(game.getWinner().getName() + " wins!");
        }
        HashMap<Player, Integer> roundScores = game.getRoundScores();
        HashMap<Player, Integer> playerScoresMap = game.getScores();
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.setColumnCount(3);
        Object[] header = {"Player", "Round score", "Total"};
        tableModel.addRow(header);
        for (Player player : roundScores.keySet()) {
            Object[] row = {player.getName(), roundScores.get(player), playerScoresMap.get(player)};
            tableModel.addRow(row);
        }
        playersScores.setModel(tableModel);
        roundNr3Label.setText(String.valueOf(game.getState()));
        currentPlayer.setText(game.getCategory());
        pricePool.setText(game.getLastRolled());
    }

    private void swap_card(JPanel card) {
        mainCardLayout.removeAll();
        mainCardLayout.add(card);
        mainCardLayout.repaint();
        mainCardLayout.revalidate();
    }

    public WoF_GUI(WheelOfFortune wof) {
        setContentPane(mainCardLayout);
        setTitle("WheelOfFortune");
        setSize(1280, 960);
        roundProgress.setMaximum(5);
        setDefaultCloseOperation((WindowConstants.EXIT_ON_CLOSE));
        setLocationRelativeTo(null);
        setVisible(true);

        this.game = wof.game;

        guessLetter.addActionListener(e -> {
            String toGuess = playerInput.getText();
            if (toGuess.isEmpty()) return;
            char letterToGuess = toGuess.charAt(0);
            game.guessLetter(wof.ourPlayer, letterToGuess);
        });
        fullGuess.addActionListener(e -> {
            String toGuess = playerInput.getText();
            game.guessPhrase(wof.ourPlayer, toGuess);
        });
        visibleCheckBox.addActionListener(e -> {
            JCheckBox c = (JCheckBox) e.getSource();
            playerInput.setEchoChar(c.isSelected() ? '\u0000' : '•');
        });
        spinWheelButton.addActionListener(e -> {
            boolean result = game.spinTheWheel(wof.ourPlayer);
        });
        newGameButton.addActionListener(e -> {
            wof.startGame();
        });
        addPlayerButton.addActionListener(e -> {
            writeToGameLog("Doesn't work yet");
            //game.joinGame(new BotPlayer("RandomBot"));
        });
        ExitButton.addActionListener(e -> {
            System.exit(0);
        });
        SinglePlayerButton.addActionListener(e -> {
            if (wof.loginAndJoin("localhost")) {
                swap_card(GamePanel);
            } else {
                // toast something went wrong
            }
        });
        MultiPlayerButton.addActionListener(e -> {
            swap_card(MultiLoginPanel);
        });
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                swap_card(MainMenuPanel);
            }
        });
        LeaveGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                wof.leaveGame();
                swap_card(MainMenuPanel);
            }
        });
        HighScoresButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                swap_card(HighScoressPanel);
            }
        });
        ipVisibityCheckBox.addActionListener(e -> {
            JCheckBox c = (JCheckBox) e.getSource();
            ipInputField.setEchoChar(c.isSelected() ? '\u0000' : '•');
        });
        joinGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String serverIp = ipInputField.getText();
                String userName = nameField.getText();

                if (userName.isEmpty()) {
                    nameLogMessage.setText("Name can't be empty");
                }else if (wof.game.getPlayers().contains(userName)){//TODO sprawdzić
                    nameLogMessage.setText("Name is taken");
                }
                else {
                    nameLogMessage.setText("");
                    if (serverIp.isEmpty()) serverIp = "localhost";
                    if (wof.loginAndJoin(serverIp)) {
                        swap_card(GamePanel);
                    } else {
                        ipLogMessage.setText("Given Ip is not valid");
                    }
                }
                //todo user name to login
            }
        });
        mainMenuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                swap_card(MainMenuPanel);
            }
        });
        playerInput.addKeyListener(new KeyAdapter() {
            boolean shifted = false;
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_ENTER){
                    if (shifted)
                        fullGuess.doClick();
                    else
                        guessLetter.doClick();
                }
                if (key == KeyEvent.VK_SHIFT){
                    shifted = true;
                }
                if (key == KeyEvent.VK_CONTROL){
                    spinWheelButton.doClick();
                }
            }
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SHIFT){
                    shifted = false;
                }
            }
        });
    }

}

