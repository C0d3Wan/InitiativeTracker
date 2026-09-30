package com.eubi.initiativetracker.swingui;

import javax.swing.*;

public class AddCharacter extends JFrame {
    private JPanel addCharacterPane;
    private JTextField characterName;
    private JComboBox characterType;
    private JSpinner initiativeSpinner;
    private JSpinner dexteritySpinner;
    private JButton addCharacterButton;

    public AddCharacter() {
        setContentPane(addCharacterPane);

        setTitle("Add Character");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        initiativeSpinner.setModel(new SpinnerNumberModel(1, 1, 20, 1));
        dexteritySpinner.setModel(new SpinnerNumberModel(0, 0, 30, 1));
    }
}
