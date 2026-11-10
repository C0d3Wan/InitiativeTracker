package com.c0d3wan.initiativetracker.ui.components;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionListener;

public class CharacterOptionsPanel extends JPanel{
    private final JButton addCharacterButton;
    private final JButton addBeastButton;
    private final JButton removeCharacterButton;
    private final JButton editCharacterButton;
    private final JButton editNPCHealthButton;

    public CharacterOptionsPanel() {
        addCharacterButton = new JButton("Add Character");
        editCharacterButton = new JButton("Edit Character");
        editNPCHealthButton = new JButton("Edit NPC Health");
        removeCharacterButton = new JButton("Remove Character");
        addBeastButton = new JButton("Open Bestiary");

        buildUI();
    }

    private void buildUI() {
        setLayout(new MigLayout("fillx", "[][][][][]", "[]"));

        addCharacterButton.setFocusPainted(false);
        addBeastButton.setFocusPainted(false);
        editCharacterButton.setFocusPainted(false);
        editNPCHealthButton.setFocusPainted(false);
        removeCharacterButton.setFocusPainted(false);

        final JLabel addLabel = new JLabel("Add Options");
        final JLabel editLabel = new JLabel("Edit Options");
        final JLabel removeLabel = new JLabel("Remove Options");

        final JSeparator vSeparator1 = new JSeparator(1);
        final JSeparator vSeparator2 = new JSeparator(1);
        final JSeparator hSeparator = new JSeparator(0);

        add(addLabel, "cell 0 0, grow, pushx");
        add(editLabel, "cell 2 0, grow, pushx");
        add(removeLabel, "cell 4 0, grow, pushx");

        add(vSeparator1, "cell 1 0, spany, grow");
        add(vSeparator2, "cell 3 0, spany, grow");
        add(hSeparator, "cell 0 1, spanx, grow");

        add(addCharacterButton, "cell 0 2, grow");
        add(addBeastButton, "cell 1 2, grow");
        add(editCharacterButton, "cell 2 2, grow");
        add(editNPCHealthButton, "cell 2 3, grow");
        add(removeCharacterButton, "cell 4 2, grow");
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
