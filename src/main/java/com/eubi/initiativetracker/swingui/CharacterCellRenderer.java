package com.eubi.initiativetracker.swingui;

import com.eubi.initiativetracker.characters.Character;
import com.eubi.initiativetracker.characters.NonePlayerCharacter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.*;

/**
 * Custom {@link ListCellRenderer} panel responsible for rendering character summary rows
 * and their active death save indicators.
 */
@SuppressWarnings({"SameParameterValue"})
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

    /**
     * Constructs a new CharacterCellRenderer and configures subcomponents and layouts.
     */
    public CharacterCellRenderer() {
        setLayout(new MigLayout("insets 2 2 2 2, fillx, aligny center", "[grow, push]12[]12[]12[]12[][]", "[]"));
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(2, 2, 2, 12), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));

        setupLabels();
        setupDeathSavePanel();
    }

    /**
     * Instantiates and arranges the character information labels on the renderer panel.
     */
    private void setupLabels() {
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        initiativeLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        characterTypeLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        npcHealth.setFont(new Font("SansSerif", Font.PLAIN, 12));
        deathStatusLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        add(nameLabel, "growx");
        add(initiativeLabel);
        add(characterTypeLabel);
        add(npcHealth);
        add(deathStatusLabel);
        add(deathSavePanel, "hidemode 3");
    }

    /**
     * Configures and positions the pass and fail death save indicator radio buttons.
     */
    private void setupDeathSavePanel() {
        deathSavePanel.setLayout(new MigLayout("insets 0, gap 2 2", "[right][][][]", "[][]"));
        deathSavePanel.setOpaque(false);

        JRadioButton[] passes = {deathPass1, deathPass2, deathPass3};
        JRadioButton[] fails = {deathFail1, deathFail2, deathFail3};

        for (JRadioButton pass : passes) {
            pass.setOpaque(false);
            pass.setText("");
        }
        for (JRadioButton fail : fails) {
            fail.setOpaque(false);
            fail.setText("");
        }

        JLabel passLabel = new JLabel("Pass");
        deathSavePanel.add(passLabel);
        deathSavePanel.add(deathPass1);
        deathSavePanel.add(deathPass2);
        deathSavePanel.add(deathPass3, "wrap");

        JLabel failLabel = new JLabel("Fail");
        deathSavePanel.add(failLabel, "gapright 4");
        deathSavePanel.add(deathFail1);
        deathSavePanel.add(deathFail2);
        deathSavePanel.add(deathFail3);
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
            setBackground(new Color(38, 162, 105));
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