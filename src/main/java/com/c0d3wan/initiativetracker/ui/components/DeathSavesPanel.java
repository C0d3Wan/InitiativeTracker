package com.c0d3wan.initiativetracker.ui.components;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionListener;

public class DeathSavesPanel extends JPanel {
    private final JButton passButton;
    private final JButton failButton;

    public DeathSavesPanel() {
        passButton = new JButton("Pass");
        failButton = new JButton("Fail");

        buildUI();
    }

    private void buildUI() {
        setLayout(new MigLayout("fill", "[]", "[]"));

        passButton.setFocusPainted(false);
        failButton.setFocusPainted(false);

        final JLabel deathSaveLabel = new JLabel("Death Saves:");

        add(deathSaveLabel);
        add(passButton);
        add(failButton);
    }

    public void addPassListener(ActionListener listener) {
        passButton.addActionListener(listener);
    }

    public void addFailListener(ActionListener listener) {
        failButton.addActionListener(listener);
    }
}
