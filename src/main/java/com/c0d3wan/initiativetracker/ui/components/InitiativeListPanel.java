package com.c0d3wan.initiativetracker.ui.components;

import com.c0d3wan.initiativetracker.characters.Character;
import com.c0d3wan.initiativetracker.ui.renderers.CharacterCellRenderer;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class InitiativeListPanel extends JPanel {

    private final DefaultListModel<Character> listModel;
    private final JList<Character> initiativeList;
    private final JScrollPane scrollPane;
    private final DeathSavesPanel deathSavesPanel;
    private final JButton nextTurnButton;

    public InitiativeListPanel() {
        listModel = new DefaultListModel<>();
        initiativeList = new JList<>(listModel);
        scrollPane = new JScrollPane(initiativeList);
        deathSavesPanel = new DeathSavesPanel();
        nextTurnButton = new JButton("Next Turn");

        buildUI();
    }

    private void buildUI() {
        setLayout(new MigLayout("fill", "[]", "[]"));
        scrollPane.setBorder(null);
        deathSavesPanel.setVisible(false);
        nextTurnButton.setFocusPainted(false);

        initiativeList.setCellRenderer(new CharacterCellRenderer());

        add(nextTurnButton, "cell 0 0");
        add(deathSavesPanel, "cell 1 0, align right");
        add(scrollPane, "cell 0 1, grow, pushy, span");
    }

    public void refreshCharacters(List<Character> characters) {
        listModel.clear();
        characters.forEach(listModel::addElement);
    }

    public Character getCurrentCharacter() {
        if (listModel.isEmpty()) {
            return null;
        } else {
            return listModel.get(0);
        }
    }

    public void addSelectionListener(ListSelectionListener listener) {
        initiativeList.addListSelectionListener(listener);
    }

    public void addNextTurnListener(ActionListener listener) {
        nextTurnButton.addActionListener(listener);
    }

    public Character getSelectedCharacter() {
        return initiativeList.getSelectedValue();
    }

    public DeathSavesPanel getDeathSavesPanel() {
        return deathSavesPanel;
    }
}
