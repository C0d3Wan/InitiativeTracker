package com.eubi.initiativetracker.swingui;

import com.eubi.initiativetracker.characters.Character;
import com.eubi.initiativetracker.characters.NonePlayerCharacter;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.util.*;

/**
 * Custom {@link ListCellRenderer} panel responsible for rendering character summary rows
 * and their active death save indicators.
 */
@SuppressWarnings({"SameParameterValue", "MagicConstant"})
public class CharacterCellRenderer extends JPanel implements ListCellRenderer<Character> {
    private final JLabel nameLabel = new JLabel();
    private final JLabel initiativeLabel = new JLabel();
    private final JLabel characterTypeLabel = new JLabel();
    private final JLabel deathStatusLabel = new JLabel();
    private final JRadioButton deathPass1 = new JRadioButton();
    private final JRadioButton deathPass2 = new JRadioButton();
    private final JRadioButton deathPass3 = new JRadioButton();
    private final JRadioButton deathFail1 = new JRadioButton();
    private final JRadioButton deathFail2 = new JRadioButton();
    private final JRadioButton deathFail3 = new JRadioButton();
    private final JPanel deathSavePanel = new JPanel();
    private final JLabel npcHealth = new JLabel();

    private final UIManager uimanager;

    /**
     * Constructs a new CharacterCellRenderer and configures subcomponents and layouts.
     */
    public CharacterCellRenderer(UIManager uiManager) {
        this.uimanager = uiManager;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(2, 2, 2, 12), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));

        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        setupLabels(gbc);

        setupRadioButtons();
    }

    /**
     * Configures the component to render the given character entry inside a {@link JList}.
     *
     * @param list         The JList being painted.
     * @param character    The Character object to render.
     * @param index        The cell index.
     * @param isSelected   {@code true} if the row is selected.
     * @param cellHasFocus {@code true} if the cell has focus.
     * @return This configured {@link Component} instance.
     */
    @Override
    public Component getListCellRendererComponent(
            JList<? extends Character> list,
            Character character,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
        Map<String, Object> characterData = character.getCharacterData();
        nameLabel.setText(characterData.get("Name").toString());
        initiativeLabel.setText(characterData.get("TotalInitiative").toString());
        characterTypeLabel.setText(characterData.get("Type").toString());
        deathStatusLabel.setText(characterData.get("DeathStatus").toString());

        setOpaque(true);

        if (!characterData.get("Type").toString().equals("PLAYER")) {
            NonePlayerCharacter npc = (NonePlayerCharacter) character;
            npcHealth.setText(npc.getHealth() + "/" + npc.getMaxHealth());
        } else {
            npcHealth.setText("N/A");
        }

        if (index == 0) {
            setBackground(Color.green);
            if ("DOWN".equals(characterData.get("DeathStatus").toString())) {
                deathSavePanel.setVisible(true);
                updateDeathSaves(character);
            } else {
                deathSavePanel.setVisible(false);
                clearDeathSaves();
            }
        } else {
            deathSavePanel.setVisible(false);
            clearDeathSaves();

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(index % 2 == 0 ? list.getBackground() : list.getBackground().darker());
                setForeground(list.getForeground());
            }
        }

        return this;
    }

    /**
     * Deselects all pass and fail death save radio buttons.
     */
    private void clearDeathSaves() {
        deathPass1.setSelected(false);
        deathPass2.setSelected(false);
        deathPass3.setSelected(false);
        deathFail1.setSelected(false);
        deathFail2.setSelected(false);
        deathFail3.setSelected(false);
    }

    /**
     * Instantiates and arranges the character information labels on the renderer panel.
     *
     * @param gbc The layout constraints applied to the labels.
     */
    private void setupLabels(GridBagConstraints gbc) {
        Font nameLabelFont = this.$$$getFont$$$("SansSerif", Font.BOLD, 20, nameLabel.getFont());
        if (nameLabelFont != null) nameLabel.setFont(nameLabelFont);
        nameLabel.setText("Label");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 3;
        gbc.weighty = 5.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 2, 0, 4);
        add(nameLabel, gbc);

        final JPanel spacer1 = new JPanel();
        spacer1.setOpaque(false);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(spacer1, gbc);

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

        Font npcHealthFont = this.$$$getFont$$$("SansSerif", Font.BOLD, 12, nameLabel.getFont());
        if (nameLabelFont != null) npcHealth.setFont(npcHealthFont);
        npcHealth.setText("Label");
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.gridheight = 3;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 4, 0, 4);
        add(npcHealth, gbc);

        Font deathStatusLabelFont = this.$$$getFont$$$("SansSerif", Font.PLAIN, 14, deathStatusLabel.getFont());
        if (deathStatusLabelFont != null) deathStatusLabel.setFont(deathStatusLabelFont);
        deathStatusLabel.setText("Label");
        gbc.gridx = 5;
        gbc.gridy = 0;
        gbc.gridheight = 3;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 4, 0, 4);
        add(deathStatusLabel, gbc);

        deathSavePanel.setLayout(new GridBagLayout());
        deathSavePanel.setOpaque(false);
        gbc.gridx = 6;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.gridheight = 3;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 4, 0, 4);
        add(deathSavePanel, gbc);
    }

    /**
     * Initializes and positions the pass and fail death save indicator radio buttons.
     */
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

    /**
     * Updates the checked state of death save radio buttons based on the character's death saves.
     *
     * @param character The character whose saves are being rendered.
     */
    private void updateDeathSaves(Character character) {
        Map<String, Integer> deathSaves = character.getDeathSaves();
        int passes = (deathSaves != null && deathSaves.containsKey("Pass")) ? deathSaves.get("Pass") : 0;
        int fails  = (deathSaves != null && deathSaves.containsKey("Fail")) ? deathSaves.get("Fail") : 0;

        deathPass1.setSelected(passes >= 1);
        deathPass2.setSelected(passes >= 2);
        deathPass3.setSelected(passes >= 3);

        deathFail1.setSelected(fails >= 1);
        deathFail2.setSelected(fails >= 2);
        deathFail3.setSelected(fails >= 3);
    }
}