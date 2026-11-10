package com.c0d3wan.initiativetracker.ui.dialogs.bestiary;

import com.c0d3wan.initiativetracker.filemanagement.bestiary.BestiaryDatabase;
import com.c0d3wan.initiativetracker.filemanagement.bestiary.data.BestiaryDTO;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Modal {@link JDialog} interface allowing users to create, configure,
 * and save custom homebrew monster records into the bestiary database,
 * supporting the full extended D&D 5e data schema.
 */
public class AddHomebrewMonsterDialog extends JDialog {
    private final JPanel pane = new JPanel();

    private final JTextField nameField = new JTextField(12);
    private final JTextField groupField = new JTextField(12);
    private final JTextField subgroupField = new JTextField(12);
    private final JTextField typeField = new JTextField(12);
    private final JComboBox<String> sizeCombo = new JComboBox<>(new String[]{"Tiny", "Small", "Medium", "Large", "Huge", "Gargantuan"});
    private final JTextField alignmentField = new JTextField(12);

    private final JSpinner acSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 30, 1));
    private final JSpinner hpAvgSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 2000, 1));
    private final JTextField hpFormulaField = new JTextField("2d8 + 2", 10);
    private final JTextField crField = new JTextField("1", 6);
    private final JSpinner xpSpinner = new JSpinner(new SpinnerNumberModel(200, 0, 100000, 10));

    private final JTextField speedField = new JTextField("walk 30", 12);
    private final JTextField sensesField = new JTextField("passive Perception 10", 12);
    private final JTextField languagesField = new JTextField("Common", 12);

    private final JTextField skillsField = new JTextField(12);
    private final JTextField savesField = new JTextField(12);
    private final JTextField resistancesField = new JTextField(12);
    private final JTextField immunitiesField = new JTextField(12);
    private final JTextField conditionImmunitiesField = new JTextField(12);

    private final JSpinner strSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 30, 1));
    private final JSpinner dexSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 30, 1));
    private final JSpinner conSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 30, 1));
    private final JSpinner intSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 30, 1));
    private final JSpinner wisSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 30, 1));
    private final JSpinner chaSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 30, 1));

    private final JTextArea traitsArea = new JTextArea(3, 20);
    private final JTextArea actionsArea = new JTextArea(3, 20);
    private final JTextArea reactionsArea = new JTextArea(3, 20);
    private final JTextArea legendaryArea = new JTextArea(3, 20);
    private final JTextArea lairArea = new JTextArea(3, 20);

    private final JButton saveButton = new JButton("Save Homebrew");
    private boolean confirmed = false;

    /**
     * Constructs a new AddHomebrewMonsterMenu dialogue.
     *
     * @param parent the parent frame window
     */
    public AddHomebrewMonsterDialog(Frame parent) {
        super(parent, "Create Homebrew Monster", true);
        setupMenu();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);

        saveButton.addActionListener(e -> saveMonster());
    }

    /**
     * Lays out all form inputs, extended stats, defensive attributes, and action areas.
     */
    private void setupMenu() {
        pane.setLayout(new MigLayout("insets 4, fill", "[grow]", "[grow][]"));
        setMinimumSize(new Dimension(850, 920));
        setSize(850, 920);

        JPanel container = new JPanel(new MigLayout("insets 8, fill", "[grow]", "[][][][grow, fill][]"));
        container.setOpaque(false);

        JPanel formPanel = new JPanel(new MigLayout("insets 4, fillx, gap 6 8", "[l][grow, fill][l][grow, fill]", ""));
        formPanel.setOpaque(false);

        formPanel.add(new JLabel("Name:"), "cell 0 0");
        formPanel.add(nameField, "cell 1 0");
        formPanel.add(new JLabel("Group:"), "cell 2 0");
        formPanel.add(groupField, "cell 3 0");

        formPanel.add(new JLabel("Subgroup:"), "cell 0 1");
        formPanel.add(subgroupField, "cell 1 1");
        formPanel.add(new JLabel("Type:"), "cell 2 1");
        formPanel.add(typeField, "cell 3 1");

        formPanel.add(new JLabel("Size:"), "cell 0 2");
        formPanel.add(sizeCombo, "cell 1 2");
        formPanel.add(new JLabel("Alignment:"), "cell 2 2");
        formPanel.add(alignmentField, "cell 3 2");

        formPanel.add(new JLabel("Armor Class:"), "cell 0 3");
        formPanel.add(acSpinner, "cell 1 3");
        formPanel.add(new JLabel("Avg HP:"), "cell 2 3");
        formPanel.add(hpAvgSpinner, "cell 3 3");

        formPanel.add(new JLabel("HP Formula:"), "cell 0 4");
        formPanel.add(hpFormulaField, "cell 1 4");
        formPanel.add(new JLabel("Challenge (CR):"), "cell 2 4");
        formPanel.add(crField, "cell 3 4");

        formPanel.add(new JLabel("XP:"), "cell 0 5");
        formPanel.add(xpSpinner, "cell 1 5");
        formPanel.add(new JLabel("Speed:"), "cell 2 5");
        formPanel.add(speedField, "cell 3 5");

        formPanel.add(new JLabel("Senses:"), "cell 0 6");
        formPanel.add(sensesField, "cell 1 6");
        formPanel.add(new JLabel("Languages:"), "cell 2 6");
        formPanel.add(languagesField, "cell 3 6");

        formPanel.add(new JLabel("Skills (e.g. Perception +5):"), "cell 0 7");
        formPanel.add(skillsField, "cell 1 7");
        formPanel.add(new JLabel("Saving Throws:"), "cell 2 7");
        formPanel.add(savesField, "cell 3 7");

        formPanel.add(new JLabel("Resistances:"), "cell 0 8");
        formPanel.add(resistancesField, "cell 1 8");
        formPanel.add(new JLabel("Immunities:"), "cell 2 8");
        formPanel.add(immunitiesField, "cell 3 8");

        formPanel.add(new JLabel("Condition Immunities:"), "cell 0 9");
        formPanel.add(conditionImmunitiesField, "cell 1 9, span 3, growx");

        JPanel statsPanel = new JPanel(new MigLayout("insets 6, fillx, gap 4 2", "[][50lp, fill][][50lp, fill][][50lp, fill][][50lp, fill][][50lp, fill][][50lp, fill]", "[50lp!]"));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Ability Scores"));
        statsPanel.setOpaque(false);

        statsPanel.add(new JLabel("STR:"), "cell 0 0, center");
        statsPanel.add(strSpinner, "cell 1 0, center");
        statsPanel.add(new JLabel("DEX:"), "cell 2 0, center");
        statsPanel.add(dexSpinner, "cell 3 0, center");
        statsPanel.add(new JLabel("CON:"), "cell 4 0, center");
        statsPanel.add(conSpinner, "cell 5 0, center");
        statsPanel.add(new JLabel("INT:"), "cell 6 0, center");
        statsPanel.add(intSpinner, "cell 7 0, center");
        statsPanel.add(new JLabel("WIS:"), "cell 8 0, center");
        statsPanel.add(wisSpinner, "cell 9 0, center");
        statsPanel.add(new JLabel("CHA:"), "cell 10 0, center");
        statsPanel.add(chaSpinner, "cell 11 0, center");

        JPanel actionsPanel = new JPanel(new MigLayout("insets 4, fill", "[grow][grow]", "[grow][grow][grow]"));
        actionsPanel.setOpaque(false);

        traitsArea.setLineWrap(true);
        traitsArea.setWrapStyleWord(true);
        JScrollPane traitsScroll = new JScrollPane(traitsArea);
        traitsScroll.setBorder(BorderFactory.createTitledBorder("Traits (Name: Description)"));

        actionsArea.setLineWrap(true);
        actionsArea.setWrapStyleWord(true);
        JScrollPane actionsScroll = new JScrollPane(actionsArea);
        actionsScroll.setBorder(BorderFactory.createTitledBorder("Actions (Name: Description)"));

        reactionsArea.setLineWrap(true);
        reactionsArea.setWrapStyleWord(true);
        JScrollPane reactionsScroll = new JScrollPane(reactionsArea);
        reactionsScroll.setBorder(BorderFactory.createTitledBorder("Reactions (Name: Description)"));

        legendaryArea.setLineWrap(true);
        legendaryArea.setWrapStyleWord(true);
        JScrollPane legendaryScroll = new JScrollPane(legendaryArea);
        legendaryScroll.setBorder(BorderFactory.createTitledBorder("Legendary Actions"));

        lairArea.setLineWrap(true);
        lairArea.setWrapStyleWord(true);
        JScrollPane lairScroll = new JScrollPane(lairArea);
        lairScroll.setBorder(BorderFactory.createTitledBorder("Lair Actions"));

        actionsPanel.add(traitsScroll, "cell 0 0, grow, wrap");
        actionsPanel.add(actionsScroll, "cell 0 1, grow, wrap");
        actionsPanel.add(reactionsScroll, "cell 0 2, grow, wrap");
        actionsPanel.add(legendaryScroll, "cell 1 0, grow, wrap");
        actionsPanel.add(lairScroll, "cell 1 1, grow, span 1 2");

        container.add(formPanel, "growx, wrap");
        container.add(statsPanel, "growx, wrap");
        container.add(actionsPanel, "grow, wrap");

        saveButton.setFocusPainted(false);
        container.add(saveButton, "cell 0 7 2 1, growx, height 38!");

        JScrollPane scrollPane = new JScrollPane(container);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        pane.add(scrollPane, "cell 0 0, grow");

        setContentPane(pane);

        pack();
    }

    /**
     * Validates input fields, packages extended attributes into a Monster DTO, and saves it to the database.
     */
    private void saveMonster() {
        String name = nameField.getText().trim();
        if (name.isBlank()) {
            JOptionPane.showMessageDialog(this, "Monster name cannot be blank.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        BestiaryDTO.Monster monster = new BestiaryDTO.Monster();
        monster.setName(name);
        monster.setGroupName(groupField.getText().trim().isBlank() ? "Homebrew" : groupField.getText().trim());
        monster.setOfficial(false);

        String sub = subgroupField.getText().trim();
        if (!sub.isBlank()) {
            monster.setSubgroups(List.of(sub.split("\\s*,\\s*")));
        }

        monster.setType(typeField.getText().trim());
        monster.setSize((String) sizeCombo.getSelectedItem());
        monster.setAlignment(alignmentField.getText().trim());
        monster.setArmorClass(((Number) acSpinner.getValue()).intValue());
        monster.setChallengeRating(crField.getText().trim());
        monster.setExperiencePoints(((Number) xpSpinner.getValue()).intValue());

        BestiaryDTO.Monster.HitPoints hp = new BestiaryDTO.Monster.HitPoints();
        hp.setAverage(((Number) hpAvgSpinner.getValue()).intValue());
        hp.setFormula(hpFormulaField.getText().trim());
        monster.setHitPoints(hp);

        Map<String, Integer> stats = new HashMap<>();
        stats.put("strength", ((Number) strSpinner.getValue()).intValue());
        stats.put("dexterity", ((Number) dexSpinner.getValue()).intValue());
        stats.put("constitution", ((Number) conSpinner.getValue()).intValue());
        stats.put("intelligence", ((Number) intSpinner.getValue()).intValue());
        stats.put("wisdom", ((Number) wisSpinner.getValue()).intValue());
        stats.put("charisma", ((Number) chaSpinner.getValue()).intValue());
        monster.setStats(stats);

        String skillsText = skillsField.getText().trim();
        if (!skillsText.isBlank()) {
            Map<String, Integer> skillsMap = new HashMap<>();
            for (String part : skillsText.split(",")) {
                String[] kv = part.trim().split("\\s+");
                if (kv.length >= 2) {
                    try {
                        skillsMap.put(kv[0], Integer.parseInt(kv[1].replaceAll("[^0-9+-]", "")));
                    } catch (Exception ignored) {}
                }
            }
            if (!skillsMap.isEmpty()) monster.setSkills(skillsMap);
        }

        String savesText = savesField.getText().trim();
        if (!savesText.isBlank()) {
            Map<String, Integer> savesMap = new HashMap<>();
            for (String part : savesText.split(",")) {
                String[] kv = part.trim().split("\\s+");
                if (kv.length >= 2) {
                    try {
                        savesMap.put(kv[0], Integer.parseInt(kv[1].replaceAll("[^0-9+-]", "")));
                    } catch (Exception ignored) {}
                }
            }
            if (!savesMap.isEmpty()) monster.setSavingThrows(savesMap);
        }

        if (!resistancesField.getText().isBlank()) {
            monster.setDamageResistances(List.of(resistancesField.getText().trim().split("\\s*,\\s*")));
        }
        if (!immunitiesField.getText().isBlank()) {
            monster.setDamageImmunities(List.of(immunitiesField.getText().trim().split("\\s*,\\s*")));
        }
        if (!conditionImmunitiesField.getText().isBlank()) {
            monster.setConditionImmunities(List.of(conditionImmunitiesField.getText().trim().split("\\s*,\\s*")));
        }

        Map<String, Integer> speed = new HashMap<>();
        String speedText = speedField.getText().trim();
        if (!speedText.isBlank()) {
            for (String entry : speedText.split(",")) {
                String[] parts = entry.trim().split("\\s+");
                if (parts.length >= 2) {
                    try {
                        int val = Integer.parseInt(parts[1].replaceAll("[^0-9]", ""));
                        speed.put(parts[0].toLowerCase(), val);
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
        if (speed.isEmpty()) speed.put("walk", 30);
        monster.setSpeed(speed);

        List<String> senses = new ArrayList<>();
        String sensesText = sensesField.getText().trim();
        if (!sensesText.isBlank()) {
            for (String s : sensesText.split(",")) {
                if (!s.isBlank()) senses.add(s.trim());
            }
        }
        monster.setSenses(senses);

        List<String> langs = new ArrayList<>();
        String langsText = languagesField.getText().trim();
        if (!langsText.isBlank()) {
            for (String l : langsText.split(",")) {
                if (!l.isBlank()) langs.add(l.trim());
            }
        }
        monster.setLanguages(langs);

        List<BestiaryDTO.Monster.Feature> traits = parseFeatures(traitsArea.getText());
        if (!traits.isEmpty()) monster.setTraits(traits);

        List<BestiaryDTO.Monster.Feature> reactions = parseFeatures(reactionsArea.getText());
        if (!reactions.isEmpty()) monster.setReactions(reactions);

        List<BestiaryDTO.Monster.Feature> actions = parseFeatures(actionsArea.getText());
        if (!actions.isEmpty()) monster.setActions(actions);

        String legText = legendaryArea.getText().trim();
        if (!legText.isBlank()) {
            BestiaryDTO.Monster.ActionGroup leg = new BestiaryDTO.Monster.ActionGroup();
            leg.setActions(parseFeatures(legText));
            monster.setLegendaryActions(leg);
        }

        String lairText = lairArea.getText().trim();
        if (!lairText.isBlank()) {
            BestiaryDTO.Monster.ActionGroup lair = new BestiaryDTO.Monster.ActionGroup();
            lair.setActions(parseFeatures(lairText));
            monster.setLairActions(lair);
        }

        BestiaryDatabase.saveUserMonster(monster);
        confirmed = true;
        dispose();
    }

    private List<BestiaryDTO.Monster.Feature> parseFeatures(String text) {
        List<BestiaryDTO.Monster.Feature> features = new ArrayList<>();
        if (text == null || text.isBlank()) return features;

        for (String line : text.split("\n")) {
            if (line.isBlank()) continue;
            String[] parts = line.split(":", 2);
            BestiaryDTO.Monster.Feature feature = new BestiaryDTO.Monster.Feature();
            if (parts.length == 2) {
                feature.setName(parts[0].trim());
                feature.setDescription(parts[1].trim());
            } else {
                feature.setName("Feature");
                feature.setDescription(line.trim());
            }
            features.add(feature);
        }
        return features;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}