package pap.z26.wheeloffortune;

import javax.swing.*;

public class GetNamePopup {
    private JButton joinGameButton;
    private JTextField textField2;
    private JCheckBox ipVisibleCheck;
    private JPasswordField ipInput;
    private JTextField textField1;
    private JLabel youAreAboutToLabel;

    public GetNamePopup() {
    ipVisibleCheck.addActionListener(e -> {
        JCheckBox c = (JCheckBox) e.getSource();
        ipInput.setEchoChar(c.isSelected() ? '\u0000' : '•');
    });
}
}
