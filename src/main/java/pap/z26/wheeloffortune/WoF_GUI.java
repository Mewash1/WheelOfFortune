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
import java.util.Arrays;
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
    private JButton addBotButton;
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
    private JCheckBox ipVisibleCheck;
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
        helpButton.addActionListener(e -> writeToGameLog("There's no help available"));
        addPlayerButton.addActionListener(e -> {
            writeToGameLog("Doesn't work yet");
            //game.joinGame(new BotPlayer("RandomBot"));
        });
        ipVisibleCheck.addActionListener(e -> {
            JCheckBox c = (JCheckBox) e.getSource();
            ipInput.setEchoChar(c.isSelected() ? '\u0000' : '•');
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
            String serverIp = ipInput.getText();
            if (serverIp.isEmpty()) serverIp = "localhost";
            if (wof.loginAndJoin(serverIp)) {
                swap_card(GamePanel);
            } else {
                // toast something went wrong
            }
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
    }
}

