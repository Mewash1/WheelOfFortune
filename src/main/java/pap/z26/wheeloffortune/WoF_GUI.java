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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class WoF_GUI extends JFrame {
    private JPanel mainCardLayout;
    private JPanel MainMenuPanel;
    private JButton SinglePlayerButton;
    private JButton ExitButton;
    private JButton CreditsButton;
    private JButton MultiPlayerButton;
    private JButton HighScoresButton;
    private JPanel GamePanel;
    private JButton newGameButton;
    private JButton LeaveGameButton;
    private JButton guessLetter;
    private JPasswordField playerInput;
    private JButton fullGuess;
    private JCheckBox visibleCheckBox;
    private JLabel currentPlayer;
    private JLabel roundNrLabel;
    private JLabel pricePool;
    private JButton spinWheelButton;
    private JTextField roundSollution;
    private JTable playersScores;
    private JList guessesHistory;
    private JPanel HighScoresPanel;
    private JTable HighScoresTable;
    private JButton backButton;
    private JPanel MultiLoginPanel;
    private JButton joinGameButton;
    private JButton mainMenuButton;
    private JCheckBox ipVisibityCheckBox;
    private JPasswordField ipInputField;
    private JTextField ipLogMessage;
    private JTextField nameField;
    private JTextField nameLogMessage;
    private JButton backButton1;
    private JPanel CreditsPanel;
    private JPanel mainPanel;
    private JTextField titleText;
    private JPanel menuButtons;
    private JToolBar topToolBar;
    private JSplitPane mainDivider;
    private JPanel leftMenu;
    private JPanel bottomPanel;
    private JPanel topPanel;
    private JScrollPane guessLog;
    private JLabel namePrompt;
    private JPanel ipPannel;
    private JLabel serverIpPrompt;
    private JPanel serverJoinPanel;
    private JPanel ipInputPannel;
    private JLabel infoField;
    private JLabel HighScoresLabel;
    private JPanel creditsMainPanel;
    private JLabel CreditsLabel;
    private JPanel creditsContentPanel;
    private JList creditsList;
    private JPanel creditsContent;
    private JPasswordField portInputField;
    private JLabel portPrompt;
    private JPanel portInputPannel;
    private JPanel inputPanel;
    private JPanel ipAndPortPanel;


    private final DefaultListModel<String> listModel = new DefaultListModel<>();

    private final Game game;

    public void writeToGameLog(String content) {
        listModel.add(0, content);
        //noinspection unchecked
        guessesHistory.setModel(listModel);
    }

    public void updateGUI() {
        if (game.getState() != GameState.ENDED) {
            String phrase = game.getPhrase();
            if (!game.hasNotGuessedConsonants()) {
                phrase += " [NO CONSONANTS LEFT]"; //checks if the phrase is filled with constants
            }
            roundSollution.setText(phrase); //updetes the phrase with guess and new random phrase
        } else {
            roundSollution.setText(game.getWinner().getName() + " wins!"); //display the winner
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
        roundNrLabel.setText(String.valueOf(game.getState()));

        String wheel_name = String.format("src/main/resources/%d.gif",game.getState());
        System.out.println(wheel_name);
        spinWheelButton.setIcon(new ImageIcon(wheel_name));//set spin button icon
        currentPlayer.setText(game.getCategory());
        pricePool.setText(game.getLastRolled());
        if (game.getState() == GameState.FINAL) {
            if (game.getMoveState() == Game.MoveState.HAS_TO_GUESS_CONSONANT) {
                fullGuess.setText("Reveal 4 letters");
            } else {
                fullGuess.setText("Guess the final phrase!");
            }
        } else {
            fullGuess.setText("PHRASE");
        }
    }

    private void swap_card(JPanel card) { //method used to swap cards in view
        mainCardLayout.removeAll();
        mainCardLayout.add(card);
        mainCardLayout.repaint();
        mainCardLayout.revalidate();
    }

    public void setJoinMessage(String message) {
        ipLogMessage.setText(message);
    } //add log message after login

    public void switchToGameCard() { //clear the prompts and show the game card
        ipLogMessage.setText(" ");
        currentPlayer.setText(" ");
        pricePool.setText(" ");
        swap_card(GamePanel);
    }

    public WoF_GUI(WheelOfFortune wof) { //window constructor
        setContentPane(mainCardLayout);
        setTitle("WheelOfFortune");
        setSize(1280, 960);
        setDefaultCloseOperation((WindowConstants.EXIT_ON_CLOSE));
        DefaultListCellRenderer renderer = (DefaultListCellRenderer) creditsList.getCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);//centers new window
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
            if (toGuess.isEmpty()) return;
            game.guessPhrase(wof.ourPlayer, toGuess);
        });
        visibleCheckBox.addActionListener(e -> {
            JCheckBox c = (JCheckBox) e.getSource();
            playerInput.setEchoChar(c.isSelected() ? '\u0000' : '•');
        });
        spinWheelButton.addActionListener(e -> game.spinTheWheel(wof.ourPlayer));
        newGameButton.addActionListener(e -> wof.startGame());
        ExitButton.addActionListener(e -> {
            wof.logout();
            System.exit(0);
        });
        SinglePlayerButton.addActionListener(e -> {
            if (wof.join("localhost", 26969).equals("Success")) {
                swap_card(GamePanel);
                wof.startGame();
            }
        });
        MultiPlayerButton.addActionListener(e -> swap_card(MultiLoginPanel));
        backButton.addActionListener(e -> swap_card(MainMenuPanel));
        backButton1.addActionListener(e -> swap_card(MainMenuPanel));
        LeaveGameButton.addActionListener(e -> {
            wof.leaveGame();
            swap_card(MainMenuPanel);
            listModel.clear();
        });
        HighScoresButton.addActionListener(e -> {
            Database database = Database.getInstance();
            ArrayList<LeaderboardRecord> records = database.getHighScores(10);
            if (!records.isEmpty()) {
                DefaultTableModel tableModel = new DefaultTableModel();
                tableModel.setColumnCount(3);
                Object[] header = {"#", "Player", "Score"};
                tableModel.addRow(header);
                for (LeaderboardRecord record : records) {
                    Object[] row = {record.position(), record.playerName(), record.score()};
                    tableModel.addRow(row);
                }
                HighScoresTable.setModel(tableModel);
            }
            swap_card(HighScoresPanel);
        });
        ipVisibityCheckBox.addActionListener(e -> {
            JCheckBox c = (JCheckBox) e.getSource();
            ipInputField.setEchoChar(c.isSelected() ? '\u0000' : '•');
            portInputField.setEchoChar(c.isSelected() ? '\u0000' : '•');
        });
        joinGameButton.addActionListener(e -> {
            String serverIp = ipInputField.getText();
            String serverPort = portInputField.getText();
            String userName = nameField.getText();

            if (userName.isEmpty() || userName.equals("SYSTEM")) {
                nameLogMessage.setText("Invalid user name");
            } else {
                wof.updatePlayerName(userName);
                nameLogMessage.setText("");
                if (serverIp.isEmpty()) serverIp = "localhost";
                if (serverPort.isEmpty()) serverPort = "26969";
                wof.requestGameJoin(serverIp, serverPort);
            }
            //todo user name to login
        });
        mainMenuButton.addActionListener(e -> {
            ipLogMessage.setText("");
            swap_card(MainMenuPanel);
        });
        playerInput.addKeyListener(new KeyAdapter() {
            boolean shifted = false;
            boolean controled = false;

            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_ENTER) {
                    if (shifted)
                        fullGuess.doClick();
                    else if (controled)
                        spinWheelButton.doClick();
                    else
                        guessLetter.doClick();
                }
                if (key == KeyEvent.VK_SHIFT) {
                    shifted = true;
                }
                if (key == KeyEvent.VK_CONTROL) {
                    controled = true;
                }
            }

            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    shifted = false;
                } else if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    controled = false;
                }
            }
        });
        CreditsButton.addActionListener(e -> swap_card(CreditsPanel));
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
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainCardLayout = new JPanel();
        mainCardLayout.setLayout(new CardLayout(0, 0));
        mainCardLayout.setEnabled(true);
        mainCardLayout.setPreferredSize(new Dimension(1000, 750));
        mainPanel.add(mainCardLayout, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
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
        CreditsButton.setForeground(new Color(-10777925));
        CreditsButton.setHorizontalAlignment(2);
        CreditsButton.setText("Credits");
        menuButtons.add(CreditsButton, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(500, -1), null, 0, false));
        MultiPlayerButton = new JButton();
        Font MultiPlayerButtonFont = this.$$$getFont$$$(null, Font.ITALIC, 24, MultiPlayerButton.getFont());
        if (MultiPlayerButtonFont != null) MultiPlayerButton.setFont(MultiPlayerButtonFont);
        MultiPlayerButton.setForeground(new Color(-10777925));
        MultiPlayerButton.setHorizontalAlignment(2);
        MultiPlayerButton.setText("Multi Player");
        menuButtons.add(MultiPlayerButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(500, -1), null, 0, false));
        HighScoresButton = new JButton();
        Font HighScoresButtonFont = this.$$$getFont$$$(null, Font.ITALIC, 24, HighScoresButton.getFont());
        if (HighScoresButtonFont != null) HighScoresButton.setFont(HighScoresButtonFont);
        HighScoresButton.setForeground(new Color(-10777925));
        HighScoresButton.setHorizontalAlignment(2);
        HighScoresButton.setText("High Scores");
        menuButtons.add(HighScoresButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(500, -1), null, 0, false));
        SinglePlayerButton = new JButton();
        Font SinglePlayerButtonFont = this.$$$getFont$$$(null, Font.ITALIC, 24, SinglePlayerButton.getFont());
        if (SinglePlayerButtonFont != null) SinglePlayerButton.setFont(SinglePlayerButtonFont);
        SinglePlayerButton.setForeground(new Color(-10777925));
        SinglePlayerButton.setHorizontalAlignment(2);
        SinglePlayerButton.setText("Single Player");
        menuButtons.add(SinglePlayerButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(500, -1), null, 0, false));
        GamePanel = new JPanel();
        GamePanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        GamePanel.setEnabled(true);
        GamePanel.setOpaque(false);
        GamePanel.setPreferredSize(new Dimension(1000, 750));
        mainCardLayout.add(GamePanel, "GameCard");
        topToolBar = new JToolBar();
        topToolBar.setFloatable(false);
        GamePanel.add(topToolBar, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(475, 20), null, 0, false));
        newGameButton = new JButton();
        newGameButton.setText("New Game");
        topToolBar.add(newGameButton);
        LeaveGameButton = new JButton();
        LeaveGameButton.setText("Leave Game");
        topToolBar.add(LeaveGameButton);
        mainDivider = new JSplitPane();
        mainDivider.setContinuousLayout(true);
        mainDivider.setEnabled(true);
        mainDivider.setResizeWeight(0.8);
        GamePanel.add(mainDivider, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        leftMenu = new JPanel();
        leftMenu.setLayout(new GridLayoutManager(5, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainDivider.setLeftComponent(leftMenu);
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        leftMenu.add(bottomPanel, new GridConstraints(4, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
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
        leftMenu.add(topPanel, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        topPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        currentPlayer = new JLabel();
        currentPlayer.setEnabled(true);
        currentPlayer.setText("Category");
        currentPlayer.setToolTipText("Category");
        topPanel.add(currentPlayer, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        roundNrLabel = new JLabel();
        roundNrLabel.setHorizontalAlignment(0);
        roundNrLabel.setHorizontalTextPosition(0);
        roundNrLabel.setText(" ");
        roundNrLabel.setToolTipText("Round number");
        topPanel.add(roundNrLabel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, -1), null, 0, false));
        pricePool = new JLabel();
        pricePool.setEnabled(true);
        pricePool.setText("");
        pricePool.setToolTipText("Price for guessing a letter");
        topPanel.add(pricePool, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.setBackground(new Color(-16777216));
        leftMenu.add(panel1, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        spinWheelButton = new JButton();
        spinWheelButton.setEnabled(true);
        spinWheelButton.setInheritsPopupMenu(false);
        spinWheelButton.setLabel("");
        spinWheelButton.setText("");
        spinWheelButton.putClientProperty("hideActionText", Boolean.FALSE);
        panel1.add(spinWheelButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        roundSollution = new JTextField();
        roundSollution.setEditable(false);
        roundSollution.setFocusable(false);
        Font roundSollutionFont = this.$$$getFont$$$(null, -1, 22, roundSollution.getFont());
        if (roundSollutionFont != null) roundSollution.setFont(roundSollutionFont);
        roundSollution.setText("PHRASE ");
        roundSollution.setToolTipText("");
        leftMenu.add(roundSollution, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        playersScores = new JTable();
        playersScores.setEditingColumn(-1);
        playersScores.setEditingRow(-1);
        playersScores.setEnabled(false);
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
        MultiLoginPanel = new JPanel();
        MultiLoginPanel.setLayout(new GridLayoutManager(6, 1, new Insets(0, 0, 0, 0), -1, -1));
        MultiLoginPanel.setPreferredSize(new Dimension(1000, 750));
        mainCardLayout.add(MultiLoginPanel, "Card2");
        namePrompt = new JLabel();
        namePrompt.setText("Name:");
        MultiLoginPanel.add(namePrompt, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ipPannel = new JPanel();
        ipPannel.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        MultiLoginPanel.add(ipPannel, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        ipPannel.add(spacer1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        serverJoinPanel = new JPanel();
        serverJoinPanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        ipPannel.add(serverJoinPanel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        joinGameButton = new JButton();
        joinGameButton.setText("Join Game");
        serverJoinPanel.add(joinGameButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mainMenuButton = new JButton();
        mainMenuButton.setText("Main Menu");
        serverJoinPanel.add(mainMenuButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ipLogMessage = new JTextField();
        ipLogMessage.setEditable(false);
        ipLogMessage.setEnabled(true);
        ipLogMessage.setForeground(new Color(-4521982));
        ipLogMessage.setVisible(true);
        ipPannel.add(ipLogMessage, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        infoField = new JLabel();
        infoField.setText("You are about to join multiplayer WoF  Please give us your name and ip of the host.");
        MultiLoginPanel.add(infoField, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        nameLogMessage = new JTextField();
        nameLogMessage.setEditable(false);
        nameLogMessage.setForeground(new Color(-4521982));
        nameLogMessage.setVisible(true);
        MultiLoginPanel.add(nameLogMessage, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        nameField = new JTextField();
        MultiLoginPanel.add(nameField, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        MultiLoginPanel.add(inputPanel, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        ipAndPortPanel = new JPanel();
        ipAndPortPanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        inputPanel.add(ipAndPortPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        ipInputPannel = new JPanel();
        ipInputPannel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        ipAndPortPanel.add(ipInputPannel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        serverIpPrompt = new JLabel();
        serverIpPrompt.setText("Server IP:");
        ipInputPannel.add(serverIpPrompt, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ipInputField = new JPasswordField();
        ipInputField.setEditable(true);
        Font ipInputFieldFont = this.$$$getFont$$$(null, -1, 22, ipInputField.getFont());
        if (ipInputFieldFont != null) ipInputField.setFont(ipInputFieldFont);
        ipInputField.setText("");
        ipInputField.setToolTipText("Your guess goes here:");
        ipInputField.setVerifyInputWhenFocusTarget(true);
        ipInputField.setVisible(true);
        ipInputPannel.add(ipInputField, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        portInputPannel = new JPanel();
        portInputPannel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        ipAndPortPanel.add(portInputPannel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        portPrompt = new JLabel();
        portPrompt.setText("Server Port:");
        portInputPannel.add(portPrompt, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        portInputField = new JPasswordField();
        portInputField.setEditable(true);
        Font portInputFieldFont = this.$$$getFont$$$(null, -1, 22, portInputField.getFont());
        if (portInputFieldFont != null) portInputField.setFont(portInputFieldFont);
        portInputField.setText("");
        portInputField.setToolTipText("Your guess goes here:");
        portInputField.setVerifyInputWhenFocusTarget(true);
        portInputField.setVisible(true);
        portInputPannel.add(portInputField, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ipVisibityCheckBox = new JCheckBox();
        ipVisibityCheckBox.setHideActionText(true);
        ipVisibityCheckBox.setHorizontalAlignment(10);
        ipVisibityCheckBox.setText("Visible");
        ipVisibityCheckBox.setVerticalTextPosition(1);
        inputPanel.add(ipVisibityCheckBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        HighScoresPanel = new JPanel();
        HighScoresPanel.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        HighScoresPanel.setPreferredSize(new Dimension(1000, 750));
        mainCardLayout.add(HighScoresPanel, "Card1");
        HighScoresPanel.setBorder(BorderFactory.createTitledBorder(null, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        HighScoresLabel = new JLabel();
        Font HighScoresLabelFont = this.$$$getFont$$$("Impact", -1, 22, HighScoresLabel.getFont());
        if (HighScoresLabelFont != null) HighScoresLabel.setFont(HighScoresLabelFont);
        HighScoresLabel.setText("High Scores!!!");
        HighScoresPanel.add(HighScoresLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        backButton = new JButton();
        backButton.setText("Back");
        HighScoresPanel.add(backButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(50, 20), null, null, 1, false));
        HighScoresTable = new JTable();
        HighScoresTable.setEnabled(false);
        Font HighScoresTableFont = this.$$$getFont$$$(null, -1, 30, HighScoresTable.getFont());
        if (HighScoresTableFont != null) HighScoresTable.setFont(HighScoresTableFont);
        HighScoresTable.setRowHeight(35);
        HighScoresPanel.add(HighScoresTable, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        CreditsPanel = new JPanel();
        CreditsPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainCardLayout.add(CreditsPanel, "Card3");
        creditsMainPanel = new JPanel();
        creditsMainPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        creditsMainPanel.setPreferredSize(new Dimension(1000, 750));
        CreditsPanel.add(creditsMainPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        creditsMainPanel.setBorder(BorderFactory.createTitledBorder(null, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        CreditsLabel = new JLabel();
        CreditsLabel.setEnabled(true);
        Font CreditsLabelFont = this.$$$getFont$$$("Impact", -1, 22, CreditsLabel.getFont());
        if (CreditsLabelFont != null) CreditsLabel.setFont(CreditsLabelFont);
        CreditsLabel.setText("Credits:");
        creditsMainPanel.add(CreditsLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        creditsContentPanel = new JPanel();
        creditsContentPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        creditsMainPanel.add(creditsContentPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        creditsContent = new JPanel();
        creditsContent.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        creditsContentPanel.add(creditsContent, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        creditsList = new JList();
        Font creditsListFont = this.$$$getFont$$$(null, -1, 30, creditsList.getFont());
        if (creditsListFont != null) creditsList.setFont(creditsListFont);
        final DefaultListModel defaultListModel2 = new DefaultListModel();
        defaultListModel2.addElement("Bartłomiej Pełka: Core Project Manager \"nyantastic job guys\"");
        defaultListModel2.addElement("Wiktor Topolski: Artificial (not) Intelligence Developer");
        defaultListModel2.addElement("Miłosz Mizak: Data Base Maintainer \"Impressive. Very based\"");
        defaultListModel2.addElement("Milan Wróblewski: Graphical User Interface Creator");
        creditsList.setModel(defaultListModel2);
        creditsContent.add(creditsList, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        backButton1 = new JButton();
        backButton1.setText("Back");
        creditsContentPanel.add(backButton1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
        return mainPanel;
    }

}

