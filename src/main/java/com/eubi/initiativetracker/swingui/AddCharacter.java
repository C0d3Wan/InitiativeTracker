package com.eubi.initiativetracker.swingui;

import javax.swing.*;

public class AddCharacter extends JFrame {
    private JPanel addCharacterPane;
    private JButton button1;

    public AddCharacter() {
        setContentPane(addCharacterPane);

        setTitle("Add Character");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }
}
