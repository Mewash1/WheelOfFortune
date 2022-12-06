package pap.z26.wheeloffortune;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class GameWindowGUI extends JFrame {
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

    public GameWindowGUI(Game game, HumanPlayer ourPlayer) {
        setContentPane(mainPannel);
        setTitle("WheelOfFortune");
        setSize(650, 400);
        roundProgress.setMaximum(5);
        setDefaultCloseOperation((WindowConstants.EXIT_ON_CLOSE));
        setVisible(true);

        this.game = game;

        guessLetter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String toGuess = playerInput.getText();
                char letterToGuess = toGuess.charAt(0);
                System.out.println("Guessing " + letterToGuess);
                game.guessLetter(ourPlayer, letterToGuess);
            }
        });
        fullGuess.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String toGuess = playerInput.getText();
                System.out.println("Guessing " + toGuess);
                game.guessPhrase(ourPlayer, toGuess);
            }
        });
        visibleCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBox c = (JCheckBox) e.getSource();
                playerInput.setEchoChar(c.isSelected() ? '\u0000' : '•');
            }
        });
        spinWheelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean result = game.spinTheWheel(ourPlayer);
                System.out.println(result ? "Spinning" : "You can't spin now");
            }
        });
        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.startGame();
                roundProgress.setValue(3);
                System.out.println("Starting a new game");
            }
        });
        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("There's no help");
            }
        });
        addPlayerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.joinGame(new BotPlayer("RandomBot"));
                System.out.println("Adding a bot...");
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
        mainPannel = new JPanel();
        mainPannel.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPannel.setPreferredSize(new Dimension(1000, 500));
        mainPannel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Wheel Of Fortune! The game", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        topToolBar = new JToolBar();
        topToolBar.setFloatable(false);
        mainPannel.add(topToolBar, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(475, 20), null, 0, false));
        newGameButton = new JButton();
        newGameButton.setText("New Game");
        topToolBar.add(newGameButton);
        helpButton = new JButton();
        helpButton.setText("Help");
        topToolBar.add(helpButton);
        addPlayerButton = new JButton();
        addPlayerButton.setText("Add Player");
        topToolBar.add(addPlayerButton);
        roundProgress = new JProgressBar();
        roundProgress.setString("75%");
        mainPannel.add(roundProgress, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(475, 4), null, 0, false));
        mainDivider = new JSplitPane();
        mainDivider.setContinuousLayout(true);
        mainDivider.setEnabled(true);
        mainDivider.setResizeWeight(0.8);
        mainPannel.add(mainDivider, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        leftMenu = new JPanel();
        leftMenu.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainDivider.setLeftComponent(leftMenu);
        topPanel = new JPanel();
        topPanel.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        leftMenu.add(topPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        topPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        pricePool = new JLabel();
        pricePool.setEnabled(true);
        pricePool.setText("");
        pricePool.setToolTipText("Price for guessing a letter");
        topPanel.add(pricePool, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        currentPlayer = new JLabel();
        currentPlayer.setEnabled(true);
        currentPlayer.setText("Category");
        currentPlayer.setToolTipText("Category");
        topPanel.add(currentPlayer, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        roundNr3Label = new JLabel();
        roundNr3Label.setHorizontalAlignment(0);
        roundNr3Label.setHorizontalTextPosition(0);
        roundNr3Label.setText("Round");
        roundNr3Label.setToolTipText("Round number");
        topPanel.add(roundNr3Label, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, -1), null, 0, false));
        spinWheelButton = new JButton();
        spinWheelButton.setText("Spin The Wheel!!!");
        topPanel.add(spinWheelButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        leftMenu.add(bottomPanel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        guessLetter = new JButton();
        guessLetter.setText("SINGLE LETTER");
        bottomPanel.add(guessLetter, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        fullGuess = new JButton();
        fullGuess.setText("ALL IN! (GUESS THE WORD)");
        bottomPanel.add(fullGuess, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel1.setVisible(true);
        leftMenu.add(panel1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        visibleCheckBox = new JCheckBox();
        visibleCheckBox.setHideActionText(true);
        visibleCheckBox.setHorizontalAlignment(10);
        visibleCheckBox.setText("Visible");
        visibleCheckBox.setVerticalTextPosition(1);
        panel1.add(visibleCheckBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(76, 33), null, 0, false));
        playerInput = new JPasswordField();
        playerInput.setEditable(true);
        playerInput.setText("aasdsad");
        playerInput.setToolTipText("Your guess goes here:");
        playerInput.setVerifyInputWhenFocusTarget(true);
        playerInput.setVisible(true);
        panel1.add(playerInput, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(49, 33), null, 0, false));
        roundSollution = new JTextField();
        roundSollution.setEditable(false);
        roundSollution.setFocusable(false);
        roundSollution.setText("Start New Round");
        roundSollution.setToolTipText("");
        leftMenu.add(roundSollution, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        rightMenu = new JPanel();
        rightMenu.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        rightMenu.setFocusable(false);
        mainDivider.setRightComponent(rightMenu);
        leftMenuVertDivider = new JSplitPane();
        leftMenuVertDivider.setOrientation(0);
        leftMenuVertDivider.setResizeWeight(0.4);
        rightMenu.add(leftMenuVertDivider, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        playersScores = new JTable();
        playersScores.setShowHorizontalLines(true);
        playersScores.setShowVerticalLines(false);
        playersScores.setToolTipText("players");
        leftMenuVertDivider.setLeftComponent(playersScores);
        guessLog = new JScrollPane();
        leftMenuVertDivider.setRightComponent(guessLog);
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
    public JComponent $$$getRootComponent$$$() {
        return mainPannel;
    }

}
