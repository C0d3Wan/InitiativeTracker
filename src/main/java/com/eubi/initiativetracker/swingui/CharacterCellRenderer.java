package com.eubi.initiativetracker.swingui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.eubi.initiativetracker.characters.Character;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;

public class CharacterCellRenderer extends JPanel implements ListCellRenderer<Character> {
    private JLabel nameLabel = new JLabel();
    private JLabel initiativeLabel = new JLabel();
    private JLabel characterTypeLabel = new JLabel();
    private JLabel deathStatusLabel = new JLabel();
    private JRadioButton deathPass1 = new JRadioButton();
    private JRadioButton deathPass2= new JRadioButton();
    private JRadioButton deathPass3= new JRadioButton();
    private JRadioButton deathFail1= new JRadioButton();
    private JRadioButton deathFail2= new JRadioButton();
    private JRadioButton deathFail3= new JRadioButton();
    private JPanel deathSavePanel = new JPanel();

    public CharacterCellRenderer() {
        setLayout(new GridLayoutManager(1, 8, new Insets(0, 0, 0, 0), 5, 0));
        setBorder(new EmptyBorder(4, 12, 4, 12));

        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        initiativeLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
        characterTypeLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        deathStatusLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        add(nameLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        add(initiativeLabel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        add(characterTypeLabel, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        add(deathStatusLabel, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));

        setupRadioButtons();
    }

    @Override
    public Component getListCellRendererComponent(
            JList<? extends Character> list,
            Character character,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
        Map<String, Object> characterData = character.getCharacterData();
        nameLabel.setText(characterData.get("Name").toString());
        initiativeLabel.setText(characterData.get("Initiative").toString());
        characterTypeLabel.setText(characterData.get("Type").toString());
        deathStatusLabel.setText(characterData.get("DeathStatus").toString());

        setOpaque(true);

        if (index == 0) {
            setBackground(Color.green);
            if (characterData.get("DeathStatus").toString().equals("DOWN")) {
                deathSavePanel.setVisible(true);
            } else {
                deathSavePanel.setVisible(false);
            }
        } else if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(index % 2 == 0 ? list.getBackground() : list.getBackground().darker());
            deathSavePanel.setVisible(false);
        }

        return this;
    }

    private void setupRadioButtons() {
        deathSavePanel.setLayout(new GridLayoutManager(2, 4, new Insets(0, 0, 0, 0), -1, -1, true, true));
        deathSavePanel.setOpaque(false);
        add(deathSavePanel, new GridConstraints(0, 4, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        deathPass1.setText("");
        deathSavePanel.add(deathPass1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deathPass1.setOpaque(false);

        deathPass2.setText("");
        deathSavePanel.add(deathPass2, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deathPass2.setOpaque(false);

        deathPass3.setText("");
        deathSavePanel.add(deathPass3, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deathPass3.setOpaque(false);

        deathFail1.setText("");
        deathSavePanel.add(deathFail1, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deathFail1.setOpaque(false);

        deathFail2.setText("");
        deathSavePanel.add(deathFail2, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deathFail2.setOpaque(false);

        deathFail3.setText("");
        deathSavePanel.add(deathFail3, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deathFail3.setOpaque(false);

        final JLabel label1 = new JLabel();
        label1.setText("Pass");
        deathSavePanel.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Fail");
        deathSavePanel.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }
}