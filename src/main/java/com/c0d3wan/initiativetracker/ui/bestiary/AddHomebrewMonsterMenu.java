package com.c0d3wan.initiativetracker.ui.bestiary;

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
 * and save custom homebrew monster records into the bestiary database.
 */
public class AddHomebrewMonsterMenu extends JDialog {
    private final JPanel pane = new JPanel();

    private final JTextField nameField = new JTextField(12);
    private final JTextField groupField = new JTextField(12);
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

    private final JSpinner strSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 30, 1));
    private final JSpinner dexSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 30, 1));
    private final JSpinner conSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 30, 1));
    private final JSpinner intSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 30, 1));
    private final JSpinner wisSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 30, 1));
    private final JSpinner chaSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 30, 1));

    private final JTextArea traitsArea = new JTextArea(4, 20);
    private final JTextArea reactionsArea = new JTextArea(4, 20);
    private final JTextArea actionsArea = new JTextArea(4, 20);

    private final JButton saveButton = new JButton("Save Homebrew");
    private boolean confirmed = false;

    /**
     * Constructs a new AddHomebrewMonsterMenu dialogue.
     *
     * @param parent the parent frame window
     */
    public AddHomebrewMonsterMenu(Frame parent) {
        super(parent, "Create Homebrew Monster", true);
        setupMenu();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);

        setMinimumSize(new Dimension(750, 880));
        setSize(750, 880);
        pack();

        saveButton.addActionListener(e -> saveMonster());
    }

    /**
     * Lays out all form inputs, ability score spinners, and action areas.
     */
    private void setupMenu() {
        pane.setLayout(new MigLayout("insets 4, fill", "[grow]", "[grow][]"));

        JPanel container = new JPanel(new MigLayout("insets 8, fill", "[grow]", "[][][grow, fill][grow, fill][grow, fill][]"));
        container.setOpaque(false);

        JPanel formPanel = new JPanel(new MigLayout("insets 4, fillx, gap 6 8", "[l][grow, fill][l][grow, fill]", ""));
        formPanel.setOpaque(false);

        formPanel.add(new JLabel("Name:"), "cell 0 0");
        formPanel.add(nameField, "cell 1 0");
        formPanel.add(new JLabel("Group:"), "cell 2 0");
        formPanel.add(groupField, "cell 3 0");

        formPanel.add(new JLabel("Type:"), "cell 0 1");
        formPanel.add(typeField, "cell 1 1");
        formPanel.add(new JLabel("Size:"), "cell 2 1");
        formPanel.add(sizeCombo, "cell 3 1");

        formPanel.add(new JLabel("Alignment:"), "cell 0 2");
        formPanel.add(alignmentField, "cell 1 2");
        formPanel.add(new JLabel("Armor Class:"), "cell 2 2");
        formPanel.add(acSpinner, "cell 3 2");

        formPanel.add(new JLabel("Avg HP:"), "cell 0 3");
        formPanel.add(hpAvgSpinner, "cell 1 3");
        formPanel.add(new JLabel("HP Formula:"), "cell 2 3");
        formPanel.add(hpFormulaField, "cell 3 3");

        formPanel.add(new JLabel("Challenge (CR):"), "cell 0 4");
        formPanel.add(crField, "cell 1 4");
        formPanel.add(new JLabel("XP:"), "cell 2 4");
        formPanel.add(xpSpinner, "cell 3 4");

        formPanel.add(new JLabel("Speed (e.g. walk 30, fly 60):"), "cell 0 5");
        formPanel.add(speedField, "cell 1 5");
        formPanel.add(new JLabel("Senses (comma separated):"), "cell 2 5");
        formPanel.add(sensesField, "cell 3 5");

        formPanel.add(new JLabel("Languages (comma separated):"), "cell 0 6");
        formPanel.add(languagesField, "cell 1 6, span 3, growx");

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

        traitsArea.setLineWrap(true);
        traitsArea.setWrapStyleWord(true);
        JScrollPane traitsScroll = new JScrollPane(traitsArea);
        traitsScroll.setBorder(BorderFactory.createTitledBorder("Traits (Format: Name: Description)"));

        reactionsArea.setLineWrap(true);
        reactionsArea.setWrapStyleWord(true);
        JScrollPane reactionsScroll = new JScrollPane(reactionsArea);
        reactionsScroll.setBorder(BorderFactory.createTitledBorder("Reactions (Format: Name: Description)"));

        actionsArea.setLineWrap(true);
        actionsArea.setWrapStyleWord(true);
        JScrollPane actionsScroll = new JScrollPane(actionsArea);
        actionsScroll.setBorder(BorderFactory.createTitledBorder("Actions (Format: Name: Description)"));

        container.add(formPanel, "growx, wrap");
        container.add(statsPanel, "growx, wrap");
        container.add(traitsScroll, "grow, wrap");
        container.add(reactionsScroll, "grow, wrap");
        container.add(actionsScroll, "grow, wrap");

        saveButton.setFocusPainted(false);
        container.add(saveButton, "align right, gaptop 8");

        JScrollPane scrollPane = new JScrollPane(container);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        pane.add(scrollPane, "cell 0 0, grow");

        setContentPane(pane);
    }

    /**
     * Validates input fields, packages the data into a Monster DTO, and saves it to the database as user homebrew.
     */
    private void saveMonster() {
        String name = nameField.getText().trim();
        if (name.isBlank()) {
            JOptionPane.showMessageDialog(this, "Monster name cannot be blank.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        BestiaryDTO.Monster monster = new BestiaryDTO.Monster();
        monster.setName(name);
        monster.setGroup(groupField.getText().trim().isBlank() ? "Homebrew" : groupField.getText().trim());
        monster.setType(typeField.getText().trim());
        monster.setSize((String) sizeCombo.getSelectedItem());
        monster.setAlignment(alignmentField.getText().trim());
        monster.setArmorClass(((Number) acSpinner.getValue()).intValue());
        monster.setChallengeRating(crField.getText().trim());
        monster.setExperiencePoints(((Number) xpSpinner.getValue()).intValue());

        BestiaryDTO.HitPoints hp = new BestiaryDTO.HitPoints();
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

        Map<String, Integer> speed = new HashMap<>();
        String speedText = speedField.getText().trim();
        if (!speedText.isBlank()) {
            String[] speedEntries = speedText.split(",");
            for (String entry : speedEntries) {
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

        List<BestiaryDTO.Feature> traits = parseFeatures(traitsArea.getText());
        if (!traits.isEmpty()) monster.setTraits(traits);

        List<BestiaryDTO.Feature> reactions = parseFeatures(reactionsArea.getText());
        if (!reactions.isEmpty()) monster.setReactions(reactions);

        List<BestiaryDTO.Feature> actions = parseFeatures(actionsArea.getText());
        if (!actions.isEmpty()) monster.setActions(actions);

        BestiaryDatabase.saveUserMonster(monster);
        confirmed = true;
        dispose();
    }

    /**
     * Parses raw text input lines into structured Feature DTO objects.
     *
     * @param text raw multi-line text input
     * @return a list of parsed Feature objects
     */
    private List<BestiaryDTO.Feature> parseFeatures(String text) {
        List<BestiaryDTO.Feature> features = new ArrayList<>();
        if (text == null || text.isBlank()) return features;

        String[] lines = text.split("\n");
        for (String line : lines) {
            if (line.isBlank()) continue;
            String[] parts = line.split(":", 2);
            BestiaryDTO.Feature feature = new BestiaryDTO.Feature();
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

    /**
     * Checks whether the user successfully saved a new homebrew monster.
     *
     * @return true if confirmed and saved, false otherwise
     */
    public boolean isConfirmed() {
        return confirmed;
    }
}