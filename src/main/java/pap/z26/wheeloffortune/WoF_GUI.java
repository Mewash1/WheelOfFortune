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

    public void setJoinMessage(String message) {
        ipLogMessage.setText(message);
    }

    public void switchToGameCard() {
        ipLogMessage.setText(" ");
        currentPlayer.setText(" ");
        pricePool.setText(" ");
        swap_card(GamePanel);
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
            if (wof.join("localhost").equals("Success")) {
                swap_card(GamePanel);
                wof.startGame();
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
                listModel.clear();
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
        joinGameButton.addActionListener(e -> {
            String serverIp = ipInputField.getText();
            String userName = nameField.getText();

            if (userName.isEmpty() || userName.equals("SYSTEM")) {
                nameLogMessage.setText("Invalid user name");
            } else {
                wof.updatePlayerName(userName);
                nameLogMessage.setText("");
                if (serverIp.isEmpty()) serverIp = "localhost";
                wof.requestGameJoin(serverIp);
            }
            //todo user name to login
        });
        mainMenuButton.addActionListener(e -> {
            ipLogMessage.setText("");
            swap_card(MainMenuPanel);
        });
        playerInput.addKeyListener(new KeyAdapter() {
            boolean shifted = false;

            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_ENTER) {
                    if (shifted)
                        fullGuess.doClick();
                    else
                        guessLetter.doClick();
                }
                if (key == KeyEvent.VK_SHIFT) {
                    shifted = true;
                }
                if (key == KeyEvent.VK_CONTROL) {
                    spinWheelButton.doClick();
                }
            }

            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    shifted = false;
                }
            }
        });
    }

//    public static void main(String[] args) {
//        System.out.println("UI Compiled");
//    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainCardLayout = new JPanel();
        mainCardLayout.setLayout(new CardLayout(0, 0));
        mainCardLayout.setEnabled(true);
        mainCardLayout.setPreferredSize(new Dimension(1000, 750));
        mainCardLayout.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        MainMenuPanel = new JPanel();
        MainMenuPanel.setLayout(new GridLayoutManager(2, 1, new Insets(40, 20, 0, 20), -1, -1));
        mainCardLayout.add(MainMenuPanel, "MenuCard");
        MainMenuPanel.setBorder(BorderFactory.createTitledBorder(null, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        titleText = new JTextField();
        titleText.setEditable(false);
        titleText.setEnabled(true);
        Font titleTextFont = this.$$$getFont$$$("Arial Black", Font.BOLD, 48, titleText.getFont());
        if (titleTextFont != null) titleText.setFont(titleTextFont);
        titleText.setText(" Wheel Of Fortune The Game ");
        MainMenuPanel.add(titleText, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        menuButtons = new JPanel();
        menuButtons.setLayout(new GridLayoutManager(5, 1, new Insets(0, 0, 0, 0), -1, -1));
        MainMenuPanel.add(menuButtons, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        SinglePlayerButton = new JButton();
        Font SinglePlayerButtonFont = this.$$$getFont$$$(null, Font.ITALIC, 24, SinglePlayerButton.getFont());
        if (SinglePlayerButtonFont != null) SinglePlayerButton.setFont(SinglePlayerButtonFont);
        SinglePlayerButton.setForeground(new Color(-7829368));
        SinglePlayerButton.setHorizontalAlignment(2);
        SinglePlayerButton.setText("Single  Player");
        menuButtons.add(SinglePlayerButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(500, -1), null, 0, false));
        ExitButton = new JButton();
        Font ExitButtonFont = this.$$$getFont$$$(null, Font.ITALIC, 24, ExitButton.getFont());
        if (ExitButtonFont != null) ExitButton.setFont(ExitButtonFont);
        ExitButton.setForeground(new Color(-4521982));
        ExitButton.setHorizontalAlignment(2);
        ExitButton.setText("Exit");
        menuButtons.add(ExitButton, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(500, -1), null, 0, false));
        CreditsButton = new JButton();
        Font CreditsButtonFont = this.$$$getFont$$$(null, Font.ITALIC, 24, CreditsButton.getFont());
        if (CreditsButtonFont != null) CreditsButton.setFont(CreditsButtonFont);
        CreditsButton.setForeground(new Color(-7829368));
        CreditsButton.setHorizontalAlignment(2);
        CreditsButton.setText("Credits");
        menuButtons.add(CreditsButton, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(500, -1), null, 0, false));
        MultiPlayerButton = new JButton();
        Font MultiPlayerButtonFont = this.$$$getFont$$$(null, Font.ITALIC, 24, MultiPlayerButton.getFont());
        if (MultiPlayerButtonFont != null) MultiPlayerButton.setFont(MultiPlayerButtonFont);
        MultiPlayerButton.setForeground(new Color(-7829368));
        MultiPlayerButton.setHorizontalAlignment(2);
        MultiPlayerButton.setText("Multi Player");
        menuButtons.add(MultiPlayerButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(500, -1), null, 0, false));
        HighScoresButton = new JButton();
        Font HighScoresButtonFont = this.$$$getFont$$$(null, Font.ITALIC, 24, HighScoresButton.getFont());
        if (HighScoresButtonFont != null) HighScoresButton.setFont(HighScoresButtonFont);
        HighScoresButton.setForeground(new Color(-7829368));
        HighScoresButton.setHorizontalAlignment(2);
        HighScoresButton.setText("High Scores");
        menuButtons.add(HighScoresButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(500, -1), null, 0, false));
        GamePanel = new JPanel();
        GamePanel.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        GamePanel.setEnabled(true);
        GamePanel.setPreferredSize(new Dimension(1280, 960));
        mainCardLayout.add(GamePanel, "GameCard");
        topToolBar = new JToolBar();
        topToolBar.setFloatable(false);
        GamePanel.add(topToolBar, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(475, 20), null, 0, false));
        newGameButton = new JButton();
        newGameButton.setText("New Game");
        topToolBar.add(newGameButton);
        addPlayerButton = new JButton();
        addPlayerButton.setText("Add Player");
        topToolBar.add(addPlayerButton);
        LeaveGameButton = new JButton();
        LeaveGameButton.setText("Leave Game");
        topToolBar.add(LeaveGameButton);
        roundProgress = new JProgressBar();
        roundProgress.setString("75%");
        GamePanel.add(roundProgress, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(475, 4), null, 0, false));
        mainDivider = new JSplitPane();
        mainDivider.setContinuousLayout(true);
        mainDivider.setEnabled(true);
        mainDivider.setResizeWeight(0.8);
        GamePanel.add(mainDivider, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        leftMenu = new JPanel();
        leftMenu.setLayout(new GridLayoutManager(5, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainDivider.setLeftComponent(leftMenu);
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), -1, -1));
        leftMenu.add(bottomPanel, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        guessLetter = new JButton();
        guessLetter.setText("LETTER");
        bottomPanel.add(guessLetter, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        playerInput = new JPasswordField();
        playerInput.setEditable(true);
        Font playerInputFont = this.$$$getFont$$$(null, -1, 22, playerInput.getFont());
        if (playerInputFont != null) playerInput.setFont(playerInputFont);
        playerInput.setText("");
        playerInput.setToolTipText("Your guess goes here:");
        playerInput.setVerifyInputWhenFocusTarget(true);
        playerInput.setVisible(true);
        bottomPanel.add(playerInput, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(49, 33), null, 0, false));
        spinWheelButton = new JButton();
        spinWheelButton.setText("Spin The Wheel!!!");
        bottomPanel.add(spinWheelButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fullGuess = new JButton();
        fullGuess.setText("PHRASE");
        bottomPanel.add(fullGuess, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        visibleCheckBox = new JCheckBox();
        visibleCheckBox.setHideActionText(true);
        visibleCheckBox.setHorizontalAlignment(10);
        visibleCheckBox.setText("Visible");
        visibleCheckBox.setVerticalTextPosition(1);
        bottomPanel.add(visibleCheckBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(76, 33), null, 0, false));
        topPanel = new JPanel();
        topPanel.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        topPanel.setToolTipText("");
        leftMenu.add(topPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        topPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        currentPlayer = new JLabel();
        currentPlayer.setEnabled(true);
        currentPlayer.setText("Category");
        currentPlayer.setToolTipText("Category");
        topPanel.add(currentPlayer, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        roundNr3Label = new JLabel();
        roundNr3Label.setHorizontalAlignment(0);
        roundNr3Label.setHorizontalTextPosition(0);
        roundNr3Label.setText("Round");
        roundNr3Label.setToolTipText("Round number");
        topPanel.add(roundNr3Label, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, -1), null, 0, false));
        pricePool = new JLabel();
        pricePool.setEnabled(true);
        pricePool.setText("");
        pricePool.setToolTipText("Price for guessing a letter");
        topPanel.add(pricePool, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.setBackground(new Color(-16174180));
        leftMenu.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        roundSollution = new JTextField();
        roundSollution.setEditable(false);
        roundSollution.setFocusable(false);
        Font roundSollutionFont = this.$$$getFont$$$(null, -1, 22, roundSollution.getFont());
        if (roundSollutionFont != null) roundSollution.setFont(roundSollutionFont);
        roundSollution.setText("PHRASE ");
        roundSollution.setToolTipText("");
        leftMenu.add(roundSollution, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        playersScores = new JTable();
        playersScores.setShowHorizontalLines(true);
        playersScores.setShowVerticalLines(false);
        playersScores.setToolTipText("players");
        leftMenu.add(playersScores, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        guessLog = new JScrollPane();
        mainDivider.setRightComponent(guessLog);
        guessLog.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), "Logs", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        guessesHistory = new JList();
        guessesHistory.setEnabled(true);
        final DefaultListModel defaultListModel1 = new DefaultListModel();
        guessesHistory.setModel(defaultListModel1);
        guessesHistory.setToolTipText("Game logs");
        guessLog.setViewportView(guessesHistory);
        HighScoressPanel = new JPanel();
        HighScoressPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainCardLayout.add(HighScoressPanel, "Card1");
        HighScoressPanel.setBorder(BorderFactory.createTitledBorder(null, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        HighScoresLabel = new JLabel();
        Font HighScoresLabelFont = this.$$$getFont$$$("Impact", -1, 22, HighScoresLabel.getFont());
        if (HighScoresLabelFont != null) HighScoresLabel.setFont(HighScoresLabelFont);
        HighScoresLabel.setText("High Scores!!!");
        HighScoressPanel.add(HighScoresLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        HighScoressPanel.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel2.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        HighScoresTable = new JTable();
        HighScoresTable.setEnabled(false);
        Font HighScoresTableFont = this.$$$getFont$$$(null, -1, 18, HighScoresTable.getFont());
        if (HighScoresTableFont != null) HighScoresTable.setFont(HighScoresTableFont);
        scrollPane1.setViewportView(HighScoresTable);
        ScoresButtonPannel = new JPanel();
        ScoresButtonPannel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(ScoresButtonPannel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        backButton = new JButton();
        backButton.setText("Back");
        ScoresButtonPannel.add(backButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        MultiLoginPanel = new JPanel();
        MultiLoginPanel.setLayout(new GridLayoutManager(5, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainCardLayout.add(MultiLoginPanel, "Card2");
        final JLabel label1 = new JLabel();
        label1.setText("Name:");
        MultiLoginPanel.add(label1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(5, 1, new Insets(0, 0, 0, 0), -1, -1));
        MultiLoginPanel.add(panel3, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Server IP:");
        panel3.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel3.add(spacer1, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel4, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        joinGameButton = new JButton();
        joinGameButton.setText("Join Game");
        panel4.add(joinGameButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mainMenuButton = new JButton();
        mainMenuButton.setText("Main Menu");
        panel4.add(mainMenuButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel5, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        ipVisibityCheckBox = new JCheckBox();
        ipVisibityCheckBox.setHideActionText(true);
        ipVisibityCheckBox.setHorizontalAlignment(10);
        ipVisibityCheckBox.setText("Visible");
        ipVisibityCheckBox.setVerticalTextPosition(1);
        panel5.add(ipVisibityCheckBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ipInputField = new JPasswordField();
        ipInputField.setEditable(true);
        Font ipInputFieldFont = this.$$$getFont$$$(null, -1, 22, ipInputField.getFont());
        if (ipInputFieldFont != null) ipInputField.setFont(ipInputFieldFont);
        ipInputField.setText("");
        ipInputField.setToolTipText("Your guess goes here:");
        ipInputField.setVerifyInputWhenFocusTarget(true);
        ipInputField.setVisible(true);
        panel5.add(ipInputField, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ipLogMessage = new JTextField();
        ipLogMessage.setEditable(false);
        ipLogMessage.setEnabled(true);
        ipLogMessage.setForeground(new Color(-4521982));
        ipLogMessage.setVisible(true);
        panel3.add(ipLogMessage, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        nameField = new JTextField();
        MultiLoginPanel.add(nameField, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        infoField = new JLabel();
        infoField.setText("You are about to join multiplayer WoF  Please give us your name and ip of the host.");
        MultiLoginPanel.add(infoField, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        nameLogMessage = new JTextField();
        nameLogMessage.setEditable(false);
        nameLogMessage.setForeground(new Color(-4521982));
        nameLogMessage.setVisible(true);
        MultiLoginPanel.add(nameLogMessage, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        nameField.setNextFocusableComponent(ipInputField);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainCardLayout;
    }
}

