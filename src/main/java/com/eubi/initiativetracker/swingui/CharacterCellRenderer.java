package com.eubi.initiativetracker.swingui;

import com.eubi.initiativetracker.characters.Character;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.util.*;

public class CharacterCellRenderer extends JPanel implements ListCellRenderer<Character> {
    private JLabel nameLabel = new JLabel();
    private JLabel initiativeLabel = new JLabel();
    private JLabel characterTypeLabel = new JLabel();
    private JLabel deathStatusLabel = new JLabel();
    private JRadioButton deathPass1 = new JRadioButton();
    private JRadioButton deathPass2 = new JRadioButton();
    private JRadioButton deathPass3 = new JRadioButton();
    private JRadioButton deathFail1 = new JRadioButton();
    private JRadioButton deathFail2 = new JRadioButton();
    private JRadioButton deathFail3 = new JRadioButton();
    private JPanel deathSavePanel = new JPanel();

    public CharacterCellRenderer() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(2, 2, 2, 12), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));

        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        setupLabels(gbc);

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
        deathSavePanel.setVisible(false);
        System.out.println("Test");
        if (index == 0) {
            setBackground(Color.green);
            if (characterData.get("DeathStatus").toString().equals("DOWN")) {
                deathSavePanel.setVisible(true);
                updateDeathSaves(character);
            }
        } else if (isSelected) {

            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(index % 2 == 0 ? list.getBackground() : list.getBackground().darker());
        }

        return this;
    }

    private void setupLabels(GridBagConstraints gbc) {
        Font nameLabelFont = this.$$$getFont$$$("SansSerif", Font.BOLD, 20, nameLabel.getFont());
        if (nameLabelFont != null) nameLabel.setFont(nameLabelFont);
        nameLabel.setText("Label");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 3;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 2, 0, 4);
        add(nameLabel, gbc);

        Font initiativeLabelFont = this.$$$getFont$$$("SansSerif", Font.PLAIN, 14, initiativeLabel.getFont());
        if (initiativeLabelFont != null) initiativeLabel.setFont(initiativeLabelFont);
        initiativeLabel.setText("Label");
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 3;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 4, 0, 4);
        add(initiativeLabel, gbc);

        Font characterTypeLabelFont = this.$$$getFont$$$("SansSerif", Font.PLAIN, 14, characterTypeLabel.getFont());
        if (characterTypeLabelFont != null) characterTypeLabel.setFont(characterTypeLabelFont);
        characterTypeLabel.setText("Label");
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.gridheight = 3;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 4, 0, 4);
        add(characterTypeLabel, gbc);

        Font deathStatusLabelFont = this.$$$getFont$$$("SansSerif", Font.PLAIN, 14, deathStatusLabel.getFont());
        if (deathStatusLabelFont != null) deathStatusLabel.setFont(deathStatusLabelFont);
        deathStatusLabel.setText("Label");
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.gridheight = 3;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 4, 0, 4);
        add(deathStatusLabel, gbc);

        final JPanel spacer1 = new JPanel();
        spacer1.setOpaque(false);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(spacer1, gbc);

        deathSavePanel.setLayout(new GridBagLayout());
        deathSavePanel.setOpaque(false);
        gbc.gridx = 5;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.gridheight = 3;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 4, 0, 4);
        add(deathSavePanel, gbc);
    }

    private void setupRadioButtons() {
        GridBagConstraints gbc = new GridBagConstraints();
        deathPass1.setEnabled(true);
        deathPass1.setOpaque(false);
        deathPass1.setText("");
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        deathSavePanel.add(deathPass1, gbc);

        deathPass2.setOpaque(false);
        deathPass2.setText("");
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        deathSavePanel.add(deathPass2, gbc);

        deathPass3.setOpaque(false);
        deathPass3.setText("");
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        deathSavePanel.add(deathPass3, gbc);

        deathFail1.setOpaque(false);
        deathFail1.setText("");
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        deathSavePanel.add(deathFail1, gbc);

        deathFail2.setOpaque(false);
        deathFail2.setText("");
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        deathSavePanel.add(deathFail2, gbc);

        deathFail3.setOpaque(false);
        deathFail3.setText("");
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        deathSavePanel.add(deathFail3, gbc);

        final JLabel label1 = new JLabel();
        label1.setText("Fail");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 4);
        deathSavePanel.add(label1, gbc);

        final JLabel label2 = new JLabel();
        label2.setText("Pass");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 4);
        deathSavePanel.add(label2, gbc);
    }

    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    private void updateDeathSaves(Character character) {
        Map<String, Integer> deathSaves = character.getDeathSaves();
        System.out.println(deathSaves);
        switch (deathSaves.get("Pass")) {
            case 1 -> deathPass1.doClick();
            case 2 -> {
                deathPass1.doClick();
                deathPass2.doClick();
            }
        }
    }
}