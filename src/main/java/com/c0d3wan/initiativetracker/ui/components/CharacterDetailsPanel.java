package com.c0d3wan.initiativetracker.ui.components;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class CharacterDetailsPanel extends JPanel {

    public CharacterDetailsPanel() {


        buildUI();
    }

    private void buildUI() {
        setLayout(new MigLayout("fill", "[]", "[]"));
    }
}
