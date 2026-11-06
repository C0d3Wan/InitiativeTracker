package com.c0d3wan.initiativetracker.ui.components;

import com.c0d3wan.initiativetracker.characters.Character;
import com.c0d3wan.initiativetracker.ui.renderers.CharacterCellRenderer;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.List;

public class InitiativeListPanel extends JPanel {

    private final DefaultListModel<Character> listModel;
    private final JList<Character> initiativeList;
    private final JScrollPane scrollPane;

    public InitiativeListPanel() {
        listModel = new DefaultListModel<>();
        initiativeList = new JList<>(listModel);
        scrollPane = new JScrollPane(initiativeList);

        buildUI();
    }

    private void buildUI() {
        setLayout(new MigLayout("fill", "[grow]", "[grow]"));

        initiativeList.setCellRenderer(new CharacterCellRenderer());
        add(scrollPane, "grow");
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
}
