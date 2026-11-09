package com.c0d3wan.initiativetracker.ui.components;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionListener;

public class CharacterOptionsPanel extends JPanel{
    private final JButton addCharacterButton;
    private final JButton removeCharacterButton;
    private final JButton editCharacterButton;
    private final JButton editNPCHealthButton;

    public CharacterOptionsPanel() {
        addCharacterButton = new JButton("Add Character");
        editCharacterButton = new JButton("Edit Character");
        editNPCHealthButton = new JButton("Edit NPC Health");
        removeCharacterButton = new JButton("Remove Character");

        buildUI();
    }

    private void buildUI() {
        setLayout(new MigLayout("fill", "[]", "[]"));

        addCharacterButton.setFocusPainted(false);
        editCharacterButton.setFocusPainted(false);
        editNPCHealthButton.setFocusPainted(false);
        removeCharacterButton.setFocusPainted(false);

        add(addCharacterButton);
        add(editCharacterButton);
        add(editNPCHealthButton);
        add(removeCharacterButton);
    }

    public void addAddCharacterListener(ActionListener listener) {
        addCharacterButton.addActionListener(listener);
    }

    public void addEditCharacterListener(ActionListener listener) {
        editCharacterButton.addActionListener(listener);
    }

    public void addEditNpcListener(ActionListener listener) {
        editNPCHealthButton.addActionListener(listener);
    }

    public void addRemoveCharacterListener(ActionListener listener) {
        removeCharacterButton.addActionListener(listener);
    }
}
