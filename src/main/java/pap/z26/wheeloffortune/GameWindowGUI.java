package pap.z26.wheeloffortune;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
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

    public GameWindowGUI(Game game, HumanPlayer ourPlayer) {
        setContentPane(mainPannel);
        setTitle("WheelOfFortune");
        setSize(650, 400);
        setDefaultCloseOperation((WindowConstants.EXIT_ON_CLOSE));
        setVisible(true);
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
                System.out.println(result?"Spinning":"You can't spin now");
            }
        });
        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.startGame();
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
                game.joinGame(new BotPlayer(game));
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
        mainPannel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPannel.setPreferredSize(new Dimension(1000, 500));
        mainPannel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Wheel Of Fortune! The game", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        topToolBar = new JToolBar();
        topToolBar.setFloatable(false);
        mainPannel.add(topToolBar, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(475, 20), null, 0, false));
        newGameButton = new JButton();
        newGameButton.setText("New Game");
        topToolBar.add(newGameButton);
        helpButton = new JButton();
        helpButton.setText("Help");
        topToolBar.add(helpButton);
        addPlayerButton = new JButton();
        addPlayerButton.setText("Add Player");
        topToolBar.add(addPlayerButton);
        RoundProgress = new JProgressBar();
        RoundProgress.setString("75%");
        mainPannel.add(RoundProgress, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(475, 4), null, 0, false));
        mainDivider = new JSplitPane();
        mainDivider.setContinuousLayout(true);
        mainDivider.setEnabled(true);
        mainDivider.setResizeWeight(0.8);
        mainPannel.add(mainDivider, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        leftMenu = new JPanel();
        leftMenu.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainDivider.setLeftComponent(leftMenu);
        topPanel = new JPanel();
        topPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        leftMenu.add(topPanel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        topPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        pricePool = new JLabel();
        pricePool.setEnabled(true);
        pricePool.setText("5000$");
        pricePool.setToolTipText("random price:");
        topPanel.add(pricePool, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTHWEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        currentPlayer = new JLabel();
        currentPlayer.setEnabled(true);
        currentPlayer.setText("Player1");
        currentPlayer.setToolTipText("currentplayer:");
        topPanel.add(currentPlayer, new com.intellij.uiDesigner.core.GridConstraints(0, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTHEAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        roundNr3Label = new JLabel();
        roundNr3Label.setHorizontalAlignment(0);
        roundNr3Label.setHorizontalTextPosition(0);
        roundNr3Label.setText("Round nr 3");
        topPanel.add(roundNr3Label, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, -1), null, 0, false));
        spinWheelButton = new JButton();
        spinWheelButton.setText("Spin The Wheel!!!");
        topPanel.add(spinWheelButton, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        leftMenu.add(bottomPanel, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        guessLetter = new JButton();
        guessLetter.setText("SINGLE LETTER");
        bottomPanel.add(guessLetter, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        fullGuess = new JButton();
        fullGuess.setText("ALL IN! (GUESS THE WORD)");
        bottomPanel.add(fullGuess, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel1.setVisible(true);
        leftMenu.add(panel1, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        visibleCheckBox = new JCheckBox();
        visibleCheckBox.setHideActionText(true);
        visibleCheckBox.setHorizontalAlignment(10);
        visibleCheckBox.setText("Visible");
        visibleCheckBox.setVerticalTextPosition(1);
        panel1.add(visibleCheckBox, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(76, 33), null, 0, false));
        playerInput = new JPasswordField();
        playerInput.setEditable(true);
        playerInput.setText("aasdsad");
        playerInput.setToolTipText("Your guess goes here:");
        playerInput.setVerifyInputWhenFocusTarget(true);
        playerInput.setVisible(true);
        panel1.add(playerInput, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_SOUTHWEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(49, 33), null, 0, false));
        roundSollution = new JTextField();
        roundSollution.setEditable(false);
        roundSollution.setFocusable(false);
        roundSollution.setText("example _____ion");
        roundSollution.setToolTipText("solution goes here:");
        leftMenu.add(roundSollution, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        rightMenu = new JPanel();
        rightMenu.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        rightMenu.setFocusable(false);
        mainDivider.setRightComponent(rightMenu);
        leftMenuVertDivider = new JSplitPane();
        leftMenuVertDivider.setOrientation(0);
        leftMenuVertDivider.setResizeWeight(0.4);
        rightMenu.add(leftMenuVertDivider, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
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
        defaultListModel1.addElement("temp");
        defaultListModel1.addElement("temp2");
        defaultListModel1.addElement("temp3");
        defaultListModel1.addElement("temp4");
        guessesHistory.setModel(defaultListModel1);
        guessLog.setViewportView(guessesHistory);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPannel;
    }
}
