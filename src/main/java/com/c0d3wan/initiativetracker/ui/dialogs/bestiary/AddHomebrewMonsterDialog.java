package com.c0d3wan.initiativetracker.ui.dialogs.bestiary;

import com.c0d3wan.initiativetracker.filemanagement.FileManager;
import com.c0d3wan.initiativetracker.filemanagement.bestiary.BestiaryDatabase;
import com.c0d3wan.initiativetracker.filemanagement.bestiary.data.BestiaryDTO;
import com.c0d3wan.initiativetracker.ui.util.UIUtils;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;
import java.sql.*;
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

    private final JRadioButton autoXpRadio = new JRadioButton("Auto (CR)", true);
    private final JRadioButton manualXpRadio = new JRadioButton("Manual:");
    private final JSpinner xpSpinner = new JSpinner(new SpinnerNumberModel(200, 0, 1000000, 10));

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

    private final JTextArea traitsArea = new JTextArea(8, 28);
    private final JTextArea actionsArea = new JTextArea(8, 28);
    private final JTextArea reactionsArea = new JTextArea(4, 20);
    private final JTextArea legendaryArea = new JTextArea(4, 20);
    private final JTextArea lairArea = new JTextArea(4, 20);

    private static final String PLACEHOLDER_KEY = "isPlaceholderActive";

    private final JButton saveButton = new JButton("Save Homebrew");
    private boolean confirmed = false;

    /**
     * Constructs a new AddHomebrewMonsterDialog.
     *
     * @param parent the parent frame window
     */
    public AddHomebrewMonsterDialog(Frame parent) {
        super(parent, "Create Homebrew Monster", true);
        setupPlaceholdersAndTooltips();
        setupMenu();
        setupXpListeners();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);

        saveButton.addActionListener(e -> saveMonster());
    }

    /**
     * Wires up the Auto (CR) vs. Manual XP radio buttons and live CR document listener.
     */
    private void setupXpListeners() {
        xpSpinner.setEnabled(false);
        updateAutoXpFromCr();

        autoXpRadio.addActionListener(e -> {
            xpSpinner.setEnabled(false);
            updateAutoXpFromCr();
        });

        manualXpRadio.addActionListener(e -> xpSpinner.setEnabled(true));

        crField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (autoXpRadio.isSelected()) updateAutoXpFromCr();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (autoXpRadio.isSelected()) updateAutoXpFromCr();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (autoXpRadio.isSelected()) updateAutoXpFromCr();
            }
        });
    }

    /**
     * Updates the XP spinner value automatically based on the current CR text field value.
     */
    private void updateAutoXpFromCr() {
        int calculatedXp = calculateXpByCr(crField.getText());
        xpSpinner.setValue(calculatedXp);
    }

    /**
     * Converts a D&D 5e Challenge Rating string (e.g. "1/8", "0.25", "5", "24")
     * into its official Monster Manual Experience Points (XP) value.
     *
     * @param crText the Challenge Rating string
     * @return the corresponding XP amount
     */
    public static int calculateXpByCr(String crText) {
        if (crText == null || crText.isBlank()) return 0;
        String clean = crText.trim();

        return switch (clean) {
            case "0", "0.0" -> 10;
            case "1/8", "0.125" -> 25;
            case "1/4", "0.25" -> 50;
            case "1/2", "0.5" -> 100;
            case "1", "1.0" -> 200;
            case "2", "2.0" -> 450;
            case "3", "3.0" -> 700;
            case "4", "4.0" -> 1100;
            case "5", "5.0" -> 1800;
            case "6", "6.0" -> 2300;
            case "7", "7.0" -> 2900;
            case "8", "8.0" -> 3900;
            case "9", "9.0" -> 5000;
            case "10", "10.0" -> 5900;
            case "11", "11.0" -> 7200;
            case "12", "12.0" -> 8400;
            case "13", "13.0" -> 10000;
            case "14", "14.0" -> 11500;
            case "15", "15.0" -> 13000;
            case "16", "16.0" -> 15000;
            case "17", "17.0" -> 18000;
            case "18", "18.0" -> 20000;
            case "19", "19.0" -> 22000;
            case "20", "20.0" -> 25000;
            case "21", "21.0" -> 33000;
            case "22", "22.0" -> 41000;
            case "23", "23.0" -> 50000;
            case "24", "24.0" -> 62000;
            case "25", "25.0" -> 75000;
            case "26", "26.0" -> 90000;
            case "27", "27.0" -> 105000;
            case "28", "28.0" -> 120000;
            case "29", "29.0" -> 135000;
            case "30", "30.0" -> 155000;
            default -> {
                try {
                    double val = Double.parseDouble(clean);
                    if (val <= 0) yield 10;
                    if (Math.abs(val - 0.125) < 0.01) yield 25;
                    if (Math.abs(val - 0.25) < 0.01) yield 50;
                    if (Math.abs(val - 0.5) < 0.01) yield 100;
                    int rounded = (int) Math.round(val);
                    if (rounded >= 1 && rounded <= 30) {
                        yield calculateXpByCr(String.valueOf(rounded));
                    }
                } catch (NumberFormatException ignored) {}
                yield 0;
            }
        };
    }

    private void setupPlaceholdersAndTooltips() {
        UIUtils.setPlaceholderTooltip(nameField, "e.g. Dire Goblin (Required)", "Required: The unique name of the monster.");
        UIUtils.setPlaceholderTooltip(groupField, "e.g. Goblinoids (blank = Homebrew)", "Main category folder in the Bestiary tree.");
        UIUtils.setPlaceholderTooltip(subgroupField, "Comma-separated: e.g. Forest, Elite", "Optional tags/subfolders separated by commas.");
        UIUtils.setPlaceholderTooltip(typeField, "e.g. Humanoid, Fiend, Undead", "Creature type (Aberration, Beast, Dragon, Fiend, etc.).");
        UIUtils.setPlaceholderTooltip(alignmentField, "e.g. Chaotic Evil, Neutral", "Creature alignment.");

        UIUtils.setPlaceholderTooltip(hpFormulaField, "e.g. 5d8 + 10", "Dice formula used when rolling HP.");
        UIUtils.setPlaceholderTooltip(crField, "e.g. 1/4, 0.5, 2, 10", "Challenge Rating (supports fractions like 1/8, 1/4, 1/2 or decimals).");
        UIUtils.setPlaceholderTooltip(speedField, "Type Dist, ... (e.g. walk 30, fly 60, swim 30)", "Comma-separated pairs of '<mode> <feet>'.");
        UIUtils.setPlaceholderTooltip(sensesField, "Comma-separated (e.g. darkvision 60 ft., passive Perception 12)", "Comma-separated list of senses.");
        UIUtils.setPlaceholderTooltip(languagesField, "Comma-separated (e.g. Common, Draconic)", "Comma-separated list of spoken/understood languages.");

        UIUtils.setPlaceholderTooltip(skillsField, "Name +Mod, ... (e.g. Perception +5, Stealth +4)", "Comma-separated pairs of '<SkillName> <+Bonus>'.");
        UIUtils.setPlaceholderTooltip(savesField, "Stat +Mod, ... (e.g. Dex +4, Wis +2)", "Comma-separated pairs of '<Stat> <+Bonus>'.");
        UIUtils.setPlaceholderTooltip(resistancesField, "Comma-separated (e.g. cold, fire, nonmagical slashing)", "Comma-separated list of damage resistances.");
        UIUtils.setPlaceholderTooltip(immunitiesField, "Comma-separated (e.g. poison, psychic)", "Comma-separated list of damage immunities.");
        UIUtils.setPlaceholderTooltip(conditionImmunitiesField, "Comma-separated (e.g. charmed, frightened, poisoned)", "Comma-separated list of condition immunities.");

        String featureTooltip = "Enter one ability per line using 'Name: Description'. Press Enter for a new ability.";

        UIUtils.setPlaceholderTooltip(PLACEHOLDER_KEY, traitsArea, "Format: Name: Description (one trait per line)\nExample:\nPack Tactics: Advantage on attack rolls if an ally is within 5 ft.", featureTooltip);
        UIUtils.setPlaceholderTooltip(PLACEHOLDER_KEY, actionsArea, "Format: Name: Description (one action per line)\nExample:\nMultiattack: Makes two scimitar attacks.", featureTooltip);
        UIUtils.setPlaceholderTooltip(PLACEHOLDER_KEY, reactionsArea, "Format: Name: Description (1 per line)\nExample:\nParry: Adds 2 to AC against one melee attack.", featureTooltip);
        UIUtils.setPlaceholderTooltip(PLACEHOLDER_KEY, legendaryArea, "Format: Name: Description (1 per line)\nExample:\nDetect: Makes a Wisdom (Perception) check.", featureTooltip);
        UIUtils.setPlaceholderTooltip(PLACEHOLDER_KEY, lairArea, "Format: Name: Description (1 per line)\nExample:\nTremor: Ground shakes within a 60-ft. radius...", featureTooltip);
    }



    private String getCleanAreaText(JTextArea area) {
        if (Boolean.TRUE.equals(area.getClientProperty(PLACEHOLDER_KEY))) {
            return "";
        }
        return area.getText().trim();
    }

    private void setupMenu() {
        pane.setLayout(new MigLayout("insets 4, fill", "[grow]", "[grow]"));
        setMinimumSize(new Dimension(920, 960));
        setSize(950, 980);

        JPanel container = new JPanel(new MigLayout("insets 8, fill", "[grow]", "[][][][grow, fill][]"));
        container.setOpaque(false);

        JPanel formPanel = new JPanel(new MigLayout("insets 4, fillx, gap 6 6", "[l][grow, fill][l][grow, fill]", ""));
        formPanel.setOpaque(false);

        formPanel.add(new JLabel("Name *:"), "cell 0 0");
        formPanel.add(nameField, "cell 1 0");
        formPanel.add(new JLabel("Group:"), "cell 2 0");
        formPanel.add(groupField, "cell 3 0");

        formPanel.add(new JLabel("Subgroups (comma-sep):"), "cell 0 1");
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

        formPanel.add(new JLabel("HP Formula (e.g. 2d8+2):"), "cell 0 4");
        formPanel.add(hpFormulaField, "cell 1 4");
        formPanel.add(new JLabel("Challenge (CR):"), "cell 2 4");
        formPanel.add(crField, "cell 3 4");

        ButtonGroup xpGroup = new ButtonGroup();
        xpGroup.add(autoXpRadio);
        xpGroup.add(manualXpRadio);
        autoXpRadio.setOpaque(false);
        manualXpRadio.setOpaque(false);

        JPanel xpPanel = new JPanel(new MigLayout("insets 0, fillx, gap 6 0", "[][][grow, fill]", "[]"));
        xpPanel.setOpaque(false);
        xpPanel.add(autoXpRadio, "cell 0 0");
        xpPanel.add(manualXpRadio, "cell 1 0");
        xpPanel.add(xpSpinner, "cell 2 0, growx");

        formPanel.add(new JLabel("XP:"), "cell 0 5");
        formPanel.add(xpPanel, "cell 1 5, growx");
        formPanel.add(new JLabel("Speed (type ft, ...):"), "cell 2 5");
        formPanel.add(speedField, "cell 3 5");

        formPanel.add(new JLabel("Senses (comma-sep):"), "cell 0 6");
        formPanel.add(sensesField, "cell 1 6");
        formPanel.add(new JLabel("Languages (comma-sep):"), "cell 2 6");
        formPanel.add(languagesField, "cell 3 6");

        formPanel.add(new JLabel("Skills (Skill +Mod, ...):"), "cell 0 7");
        formPanel.add(skillsField, "cell 1 7");
        formPanel.add(new JLabel("Saves (Stat +Mod, ...):"), "cell 2 7");
        formPanel.add(savesField, "cell 3 7");

        formPanel.add(new JLabel("Resistances (comma-sep):"), "cell 0 8");
        formPanel.add(resistancesField, "cell 1 8");
        formPanel.add(new JLabel("Immunities (comma-sep):"), "cell 2 8");
        formPanel.add(immunitiesField, "cell 3 8");

        formPanel.add(new JLabel("Condition Immunities (comma-sep):"), "cell 0 9");
        formPanel.add(conditionImmunitiesField, "cell 1 9, span 3, growx");

        JPanel statsPanel = new JPanel(new MigLayout("insets 6, fillx, gap 4 2", "[][50lp, fill][][50lp, fill][][50lp, fill][][50lp, fill][][50lp, fill][][50lp, fill]", "[42lp!]"));
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

        JLabel formatHintLabel = new JLabel("Ability Formatting: Enter one entry per line as 'Name: Description' (e.g. Keen Smell: Advantage on Perception checks that rely on smell.)");
        formatHintLabel.setFont(formatHintLabel.getFont().deriveFont(Font.ITALIC, 12f));

        JPanel actionsPanel = new JPanel(new MigLayout(
                "insets 2, fill, gap 8 8",
                "[grow, fill][grow, fill][grow, fill][grow, fill][grow, fill][grow, fill]",
                "[180lp::, grow 60][115lp::, grow 40]"
        ));
        actionsPanel.setOpaque(false);

        traitsArea.setLineWrap(true);
        traitsArea.setWrapStyleWord(true);
        JScrollPane traitsScroll = new JScrollPane(traitsArea);
        traitsScroll.setBorder(BorderFactory.createTitledBorder("Traits (1 per line — Name: Description)"));

        actionsArea.setLineWrap(true);
        actionsArea.setWrapStyleWord(true);
        JScrollPane actionsScroll = new JScrollPane(actionsArea);
        actionsScroll.setBorder(BorderFactory.createTitledBorder("Actions (1 per line — Name: Description)"));

        reactionsArea.setLineWrap(true);
        reactionsArea.setWrapStyleWord(true);
        JScrollPane reactionsScroll = new JScrollPane(reactionsArea);
        reactionsScroll.setBorder(BorderFactory.createTitledBorder("Reactions (Name: Description)"));

        legendaryArea.setLineWrap(true);
        legendaryArea.setWrapStyleWord(true);
        JScrollPane legendaryScroll = new JScrollPane(legendaryArea);
        legendaryScroll.setBorder(BorderFactory.createTitledBorder("Legendary Actions (Name: Description)"));

        lairArea.setLineWrap(true);
        lairArea.setWrapStyleWord(true);
        JScrollPane lairScroll = new JScrollPane(lairArea);
        lairScroll.setBorder(BorderFactory.createTitledBorder("Lair Actions (Name: Description)"));

        actionsPanel.add(traitsScroll, "cell 0 0 3 1, grow");
        actionsPanel.add(actionsScroll, "cell 3 0 3 1, grow");
        actionsPanel.add(reactionsScroll, "cell 0 1 2 1, grow");
        actionsPanel.add(legendaryScroll, "cell 2 1 2 1, grow");
        actionsPanel.add(lairScroll, "cell 4 1 2 1, grow");

        container.add(formPanel, "cell 0 0, growx");
        container.add(statsPanel, "cell 0 1, growx");
        container.add(formatHintLabel, "cell 0 2, growx, gaptop 4, gapbottom 2");
        container.add(actionsPanel, "cell 0 3, grow, pushy");

        saveButton.setFocusPainted(false);
        container.add(saveButton, "cell 0 4, growx, height 38!, gaptop 6");

        JScrollPane scrollPane = new JScrollPane(container);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        pane.add(scrollPane, "cell 0 0, grow");

        setContentPane(pane);

        pack();
    }

    /**
     * Checks the database for an existing monster with the given name and determines if it is Official or Homebrew.
     */
    private Integer checkExistingMonsterType(String name) {
        File dbFile = new File(FileManager.getDirectory(), "bestiary.db");
        if (!dbFile.exists()) return null;

        String dbUrl = "jdbc:sqlite:" + dbFile.getAbsolutePath();
        String query = "SELECT is_official FROM Monster WHERE name = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("is_official"); // 1 = Official, 0 = Homebrew
                }
            }
        } catch (SQLException ignored) {}
        return null;
    }

    /**
     * Validates input fields, checks for name conflicts, and saves the homebrew record.
     */
    private void saveMonster() {
        String name = nameField.getText().trim();
        if (name.isBlank()) {
            JOptionPane.showMessageDialog(this, "Monster name cannot be blank.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check for duplicate name conflicts in the database
        Integer existingStatus = checkExistingMonsterType(name);
        if (existingStatus != null) {
            String originType = (existingStatus == 1) ? "an Official Monster Manual" : "an existing Homebrew";
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "A monster named '" + name + "' already exists in the database as " + originType + " record.\n\nDo you want to overwrite it?",
                    "Name Conflict Warning",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
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

        int finalXp = autoXpRadio.isSelected()
                ? calculateXpByCr(crField.getText())
                : ((Number) xpSpinner.getValue()).intValue();
        monster.setExperiencePoints(finalXp);

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

        List<BestiaryDTO.Monster.Feature> traits = parseFeatures(getCleanAreaText(traitsArea));
        if (!traits.isEmpty()) monster.setTraits(traits);

        List<BestiaryDTO.Monster.Feature> reactions = parseFeatures(getCleanAreaText(reactionsArea));
        if (!reactions.isEmpty()) monster.setReactions(reactions);

        List<BestiaryDTO.Monster.Feature> actions = parseFeatures(getCleanAreaText(actionsArea));
        if (!actions.isEmpty()) monster.setActions(actions);

        String legText = getCleanAreaText(legendaryArea);
        if (!legText.isBlank()) {
            BestiaryDTO.Monster.ActionGroup leg = new BestiaryDTO.Monster.ActionGroup();
            leg.setActions(parseFeatures(legText));
            monster.setLegendaryActions(leg);
        }

        String lairText = getCleanAreaText(lairArea);
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