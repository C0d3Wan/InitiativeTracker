package com.c0d3wan.initiativetracker.swingui;

import com.c0d3wan.initiativetracker.characters.Character;
import com.c0d3wan.initiativetracker.characters.DeathStatus;
import com.c0d3wan.initiativetracker.characters.NonePlayerCharacter;
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

    private static final Color ACTIVE_TURN_COLOR = new Color(38, 162, 105);

    /**
     * Constructs a new CharacterCellRenderer and configures subcomponents and layouts.
     */
    public CharacterCellRenderer() {
        setLayout(new MigLayout("insets 0 2 0 2, fillx, aligny center", "[grow, push]12[]12[]12[]12[][]", "[]"));
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));

        setupLabels();
        setupDeathSavePanel();
    }

    /**
     * Instantiates and arranges the character information labels on the renderer panel.
     */
    private void setupLabels() {
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 20f));
        initiativeLabel.setFont(initiativeLabel.getFont().deriveFont(Font.PLAIN, 14));
        characterTypeLabel.setFont(characterTypeLabel.getFont().deriveFont(Font.PLAIN, 14f));
        npcHealth.setFont(npcHealth.getFont().deriveFont(Font.PLAIN, 12f));
        deathStatusLabel.setFont(deathStatusLabel.getFont().deriveFont(Font.PLAIN, 14f));

        add(nameLabel, "growx, aligny center");
        add(initiativeLabel, "aligny center");
        add(characterTypeLabel, "aligny center");
        add(npcHealth, "aligny center");
        add(deathStatusLabel, "aligny center");
        add(deathSavePanel, "hidemode 3, aligny center");
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
        nameLabel.setText(character.getName());
        initiativeLabel.setText(String.valueOf(character.getInitiativeValue()));
        characterTypeLabel.setText(character.getCharacterType().toString());
        deathStatusLabel.setText(character.getDeathStatus().toString());

        setOpaque(true);

        if (!isPlayer(characterData)) {
            NonePlayerCharacter npc = (NonePlayerCharacter) character;
            npcHealth.setText(npc.getHealth() + "/" + npc.getMaxHealth());
        } else {
            npcHealth.setText("N/A");
        }

        if (index == 0) {
            setBackground(ACTIVE_TURN_COLOR);
            setForeground(list.getForeground());
            if (character.getDeathStatus() == DeathStatus.DOWN) {
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

    private boolean isPlayer(Map<String, Object> characterData) {
        return "PLAYER".equals(characterData.get("Type"));
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