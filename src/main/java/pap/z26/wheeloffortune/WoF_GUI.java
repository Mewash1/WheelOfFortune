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
            if(wof.loginAndJoin("localhost")) {
                swap_card(GamePanel);
            } else {
                // toast something went wrong
            }
        });
        MultiPlayerButton.addActionListener(e -> {
            String serverIp = Arrays.toString(ipInput.getPassword());
            if(serverIp.isEmpty()) serverIp = "localhost";
            if(wof.loginAndJoin(serverIp)) {
                swap_card(GamePanel);
            } else {
                // toast something went wrong
            }
        });
    }


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
        mainCardLayout.setPreferredSize(new Dimension(1280, 960));
        mainCardLayout.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        MainMenuPanel = new JPanel();
        MainMenuPanel.setLayout(new GridLayoutManager(2, 1, new Insets(40, 20, 0, 20), -1, -1));
        mainCardLayout.add(MainMenuPanel, "MenuCard");
        MainMenuPanel.setBorder(BorderFactory.createTitledBorder(null, "l", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        titleText = new JTextField();
        titleText.setEditable(false);
        titleText.setEnabled(true);
        Font titleTextFont = this.$$$getFont$$$("Arial Black", Font.BOLD, 48, titleText.getFont());
        if (titleTextFont != null) titleText.setFont(titleTextFont);
        titleText.setText(" Wheel Of Fortune The Game ");
        MainMenuPanel.add(titleText, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        menuButtons = new JPanel();
        menuButtons.setLayout(new GridLayoutManager(5, 2, new Insets(0, 0, 0, 0), -1, -1));
        MainMenuPanel.add(menuButtons, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        SinglePlayerButton = new JButton();
        Font SinglePlayerButtonFont = this.$$$getFont$$$(null, Font.ITALIC, 24, SinglePlayerButton.getFont());
        if (SinglePlayerButtonFont != null) SinglePlayerButton.setFont(SinglePlayerButtonFont);
        SinglePlayerButton.setForeground(new Color(-11184811));
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
        CreditsButton.setForeground(new Color(-11184811));
        CreditsButton.setHorizontalAlignment(2);
        CreditsButton.setText("Credits");
        menuButtons.add(CreditsButton, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(500, -1), null, 0, false));
        MultiPlayerButton = new JButton();
        Font MultiPlayerButtonFont = this.$$$getFont$$$(null, Font.ITALIC, 24, MultiPlayerButton.getFont());
        if (MultiPlayerButtonFont != null) MultiPlayerButton.setFont(MultiPlayerButtonFont);
        MultiPlayerButton.setForeground(new Color(-11184811));
        MultiPlayerButton.setHorizontalAlignment(2);
        MultiPlayerButton.setText("Multi Player");
        menuButtons.add(MultiPlayerButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(500, -1), null, 0, false));
        final Spacer spacer1 = new Spacer();
        menuButtons.add(spacer1, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel1.setForeground(new Color(-11184811));
        menuButtons.add(panel1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(500, -1), null, 0, false));
        ipVisibleCheck = new JCheckBox();
        ipVisibleCheck.setHideActionText(true);
        ipVisibleCheck.setHorizontalAlignment(10);
        ipVisibleCheck.setText("Visible");
        ipVisibleCheck.setVerticalTextPosition(1);
        panel1.add(ipVisibleCheck, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(76, 33), null, 0, false));
        ipInput = new JPasswordField();
        ipInput.setEditable(true);
        Font ipInputFont = this.$$$getFont$$$(null, -1, 22, ipInput.getFont());
        if (ipInputFont != null) ipInput.setFont(ipInputFont);
        ipInput.setText("");
        ipInput.setToolTipText("Multiplayer ip goes here:");
        ipInput.setVerifyInputWhenFocusTarget(true);
        ipInput.setVisible(true);
        panel1.add(ipInput, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(49, 33), null, 0, false));
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
        helpButton = new JButton();
        helpButton.setText("Help");
        topToolBar.add(helpButton);
        addPlayerButton = new JButton();
        addPlayerButton.setText("Add Player");
        topToolBar.add(addPlayerButton);
        addBotButton = new JButton();
        addBotButton.setText("Add Bot");
        topToolBar.add(addBotButton);
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
        visibleCheckBox = new JCheckBox();
        visibleCheckBox.setHideActionText(true);
        visibleCheckBox.setHorizontalAlignment(10);
        visibleCheckBox.setText("Visible");
        visibleCheckBox.setVerticalTextPosition(1);
        bottomPanel.add(visibleCheckBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(76, 33), null, 0, false));
        spinWheelButton = new JButton();
        spinWheelButton.setText("Spin The Wheel!!!");
        bottomPanel.add(spinWheelButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fullGuess = new JButton();
        fullGuess.setText("PHRASE");
        bottomPanel.add(fullGuess, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
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
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.setBackground(new Color(-16174180));
        leftMenu.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
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