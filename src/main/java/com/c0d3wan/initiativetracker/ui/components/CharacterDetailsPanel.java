package com.c0d3wan.initiativetracker.ui.components;

import com.c0d3wan.initiativetracker.characters.Character;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class CharacterDetailsPanel extends JPanel {
    private final JLabel characterName = new JLabel();

    public CharacterDetailsPanel() {


        buildUI();
    }

    private void buildUI() {
        setLayout(new MigLayout("fill", "[]", "[]"));
        add(characterName);
    }

    public void displayCharacter(Character character) {
        characterName.setText(character.getName());
    }
}
