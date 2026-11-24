package com.c0d3wan.initiativetracker.ui.dialogs.bestiary;

import com.c0d3wan.initiativetracker.characters.CharacterManager;
import com.c0d3wan.initiativetracker.filemanagement.FileManager;
import com.c0d3wan.initiativetracker.filemanagement.bestiary.BestiaryDatabase;
import com.c0d3wan.initiativetracker.filemanagement.bestiary.BestiaryTrackerIntegration;
import com.c0d3wan.initiativetracker.filemanagement.bestiary.data.BestiaryDTO;
import com.c0d3wan.initiativetracker.filemanagement.bestiary.data.HPMode;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Modal {@link JDialog} interface for browsing, searching, viewing full extended stat details,
 * managing homebrew, and adding monsters from the bestiary database into the initiative tracker.
 */
public class BestiaryDialog extends JDialog {
    private final JPanel pane = new JPanel();
    private final CharacterManager characterManager;

    private final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Bestiary");
    private final DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
    private final JTree monsterTree = new JTree(treeModel);

    private final JTextField searchField = new JTextField(15);
    private final JButton addHomebrewButton = new JButton("+ Add Custom Monster");
    private final JButton deleteMonsterButton = new JButton("Delete Monster");

    private final JLabel nameLabel = new JLabel("Select a monster");
    private final JLabel typeSizeAlignmentLabel = new JLabel("");
    private final JLabel acLabel = new JLabel("");
    private final JLabel hpLabel = new JLabel("");
    private final JLabel speedLabel = new JLabel("");
    private final JLabel sensesLanguagesLabel = new JLabel("");
    private final JLabel defenseLabel = new JLabel("");
    private final JLabel crLabel = new JLabel("");
    private final JTextArea statsArea = new JTextArea();
    private final JTextArea abilitiesArea = new JTextArea();

    private final JTextField customNameField = new JTextField(12);
    private final JSpinner countSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));

    private final JRadioButton averageHpButton = new JRadioButton("Average", true);
    private final JRadioButton rollHpButton = new JRadioButton("Roll");
    private final JRadioButton manualHpButton = new JRadioButton("Manual:");
    private final JSpinner manualHpSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 10000, 1));

    private final JRadioButton autoInitButton = new JRadioButton("Auto (d20+Dex)", true);
    private final JRadioButton manualInitButton = new JRadioButton("Manual:");
    private final JSpinner manualInitSpinner = new JSpinner(new SpinnerNumberModel(10, -30, 200, 1));

    private final JButton addToTrackerButton = new JButton("Add to Tracker");
    private final JLabel successLabel = new JLabel("Added!");

    private List<BestiaryDTO.Monster> allMonsters;
    private String selectedMonsterName = null;

    private record DbAbility(String category, String name, String description, String recharge) {}

    private static class ExtendedDbDetails {
        int passivePerception = 10;
        int proficiencyBonus = 2;
        String description = "";
        Map<String, Integer> savingThrows = new LinkedHashMap<>();
        Map<String, Integer> skills = new LinkedHashMap<>();
        List<String> vulnerabilities = new ArrayList<>();
        List<String> resistances = new ArrayList<>();
        List<String> immunities = new ArrayList<>();
        List<String> conditionImmunities = new ArrayList<>();
        List<String> languages = new ArrayList<>();
        List<String> senses = new ArrayList<>();
        Map<String, Integer> speeds = new LinkedHashMap<>();
        List<DbAbility> abilities = new ArrayList<>();
    }

    public BestiaryDialog(Frame parent, CharacterManager characterManager) {
        super(parent, "Bestiary Database", true);
        this.characterManager = characterManager;
        setupMenu();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);

        loadMonsters();

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterMonsters(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterMonsters(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterMonsters(); }
        });

        monsterTree.addTreeSelectionListener(e -> {
            TreePath path = monsterTree.getSelectionPath();
            if (path != null) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                if (node.isLeaf() && node.getUserObject() instanceof String) {
                    selectedMonsterName = (String) node.getUserObject();
                    displaySelectedMonsterDetails(selectedMonsterName);
                    addToTrackerButton.setEnabled(true);
                    deleteMonsterButton.setEnabled(true);
                }
            }
        });

        addHomebrewButton.addActionListener(e -> {
            AddHomebrewMonsterDialog homebrewDialog = new AddHomebrewMonsterDialog((Frame) getOwner());
            homebrewDialog.setVisible(true);
            if (homebrewDialog.isConfirmed()) {
                loadMonsters();
            }
        });

        deleteMonsterButton.addActionListener(e -> {
            if (selectedMonsterName == null || selectedMonsterName.isBlank()) return;

            BestiaryDatabase.findMonsterByName(selectedMonsterName).ifPresent(monster -> {
                int option = JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to delete '" + monster.getName() + "' from the bestiary?\nThis action cannot be undone.",
                        "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (option == JOptionPane.YES_OPTION) {
                    boolean deleted = BestiaryDatabase.deleteMonster(selectedMonsterName);
                    if (deleted) {
                        selectedMonsterName = null;
                        clearMonsterDetails();
                        loadMonsters();
                        deleteMonsterButton.setEnabled(false);
                    } else {
                        JOptionPane.showMessageDialog(
                                this,
                                "Failed to delete the monster. Only user added monsters can be deleted.",
                                "Deletion Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            });
        });

        averageHpButton.addActionListener(e -> manualHpSpinner.setEnabled(false));
        rollHpButton.addActionListener(e -> manualHpSpinner.setEnabled(false));
        manualHpButton.addActionListener(e -> manualHpSpinner.setEnabled(true));

        autoInitButton.addActionListener(e -> manualInitSpinner.setEnabled(false));
        manualInitButton.addActionListener(e -> manualInitSpinner.setEnabled(true));
        addToTrackerButton.addActionListener(e -> addCurrentMonsterToTracker());
    }

    private void setupMenu() {
        pane.setLayout(new MigLayout("insets 6, fill", "[320px::320px, fill][grow, fill]", "[][grow]"));
        setContentPane(pane);
        setMinimumSize(new Dimension(1080, 800));
        setSize(1080, 800);

        JPanel leftPanel = new JPanel(new MigLayout("insets 0, fill", "[grow]", "[][grow][][]"));
        leftPanel.setOpaque(false);

        JPanel searchPanel = new JPanel(new MigLayout("insets 0, fillx", "[][grow]", "[]"));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Search:"), "cell 0 0");
        searchPanel.add(searchField, "cell 1 0, growx");

        monsterTree.setRootVisible(false);
        JScrollPane treeScroll = new JScrollPane(monsterTree);

        addHomebrewButton.setFocusPainted(false);
        deleteMonsterButton.setFocusPainted(false);
        deleteMonsterButton.setEnabled(false);
        deleteMonsterButton.setForeground(new Color(200, 60, 60));

        leftPanel.add(searchPanel, "cell 0 0, growx, wrap");
        leftPanel.add(treeScroll, "cell 0 1, grow, wrap");
        leftPanel.add(addHomebrewButton, "cell 0 2, growx, gaptop 4, wrap");
        leftPanel.add(deleteMonsterButton, "cell 0 3, growx, gaptop 2");

        JPanel rightPanel = new JPanel(new MigLayout("insets 4 10 4 4, fill", "[grow]", "[][][][][][][][grow, fill][pref!]"));
        rightPanel.setOpaque(false);

        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 18f));
        typeSizeAlignmentLabel.setFont(typeSizeAlignmentLabel.getFont().deriveFont(Font.ITALIC, 12f));

        statsArea.setEditable(false);
        statsArea.setOpaque(false);
        statsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        abilitiesArea.setEditable(false);
        abilitiesArea.setOpaque(false);
        abilitiesArea.setLineWrap(true);
        abilitiesArea.setWrapStyleWord(true);

        JScrollPane abilitiesScroll = new JScrollPane(abilitiesArea);
        abilitiesScroll.setBorder(BorderFactory.createTitledBorder("Traits, Actions & Abilities"));

        rightPanel.add(nameLabel, "cell 0 0, wrap");
        rightPanel.add(typeSizeAlignmentLabel, "cell 0 1, wrap");

        JPanel statsRow = new JPanel(new MigLayout("insets 0, gap 15 0", "[][][]", "[]"));
        statsRow.setOpaque(false);
        statsRow.add(acLabel);
        statsRow.add(hpLabel);
        statsRow.add(crLabel);
        rightPanel.add(statsRow, "cell 0 2, wrap");

        rightPanel.add(speedLabel, "cell 0 3, growx, wrap");
        rightPanel.add(sensesLanguagesLabel, "cell 0 4, growx, wrap");
        rightPanel.add(defenseLabel, "cell 0 5, growx, wrap");
        rightPanel.add(statsArea, "cell 0 6, wrap, growx");
        rightPanel.add(abilitiesScroll, "cell 0 7, grow, wrap");

        JPanel trackerControlPanel = new JPanel(new MigLayout("insets 8, fillx, gap 8 6", "[l][grow, 120lp!][l][60lp!][grow, push]", "[][][]"));
        trackerControlPanel.setBorder(BorderFactory.createTitledBorder("Add to Initiative Tracker"));
        trackerControlPanel.setOpaque(false);

        ButtonGroup hpGroup = new ButtonGroup();
        hpGroup.add(averageHpButton);
        hpGroup.add(rollHpButton);
        hpGroup.add(manualHpButton);
        averageHpButton.setOpaque(false);
        rollHpButton.setOpaque(false);
        manualHpButton.setOpaque(false);
        manualHpSpinner.setEnabled(false);

        ButtonGroup initGroup = new ButtonGroup();
        initGroup.add(autoInitButton);
        initGroup.add(manualInitButton);
        autoInitButton.setOpaque(false);
        manualInitButton.setOpaque(false);
        manualInitSpinner.setEnabled(false);
        addToTrackerButton.setEnabled(false);
        successLabel.setVisible(false);

        trackerControlPanel.add(new JLabel("Custom Name:"), "cell 0 0");
        trackerControlPanel.add(customNameField, "cell 1 0, growx");
        trackerControlPanel.add(new JLabel("Count:"), "cell 2 0, align right");
        trackerControlPanel.add(countSpinner, "cell 3 0, growx, span 2, wrap");

        JPanel hpModePanel = new JPanel(new MigLayout("insets 0, gap 10 0", "[][][][]", "[]"));
        hpModePanel.setOpaque(false);
        hpModePanel.add(averageHpButton);
        hpModePanel.add(rollHpButton);
        hpModePanel.add(manualHpButton);
        hpModePanel.add(manualHpSpinner, "width 70!");

        trackerControlPanel.add(new JLabel("HP Mode:"), "cell 0 1");
        trackerControlPanel.add(hpModePanel, "cell 1 1, span 4, wrap");

        JPanel initModePanel = new JPanel(new MigLayout("insets 0, gap 10 0", "[][][]", "[]"));
        initModePanel.setOpaque(false);
        initModePanel.add(autoInitButton);
        initModePanel.add(manualInitButton);
        initModePanel.add(manualInitSpinner, "width 60!");

        trackerControlPanel.add(new JLabel("Init Mode:"), "cell 0 2");
        trackerControlPanel.add(initModePanel, "cell 1 2, span 3");

        JPanel actionPanel = new JPanel(new MigLayout("insets 0, gap 10 0", "[][grow]", "[]"));
        actionPanel.setOpaque(false);
        actionPanel.add(addToTrackerButton);
        actionPanel.add(successLabel);

        trackerControlPanel.add(actionPanel, "cell 4 2, align right");

        rightPanel.add(trackerControlPanel, "cell 0 8, growx");

        pane.add(leftPanel, "cell 0 0 1 2, grow");
        pane.add(rightPanel, "cell 1 0 1 2, grow");

        pack();
    }

    private void loadMonsters() {
        allMonsters = BestiaryDatabase.loadAllMonsters();
        filterMonsters();
    }

    private void filterMonsters() {
        String filter = searchField.getText().trim().toLowerCase();
        rootNode.removeAllChildren();

        if (allMonsters != null) {
            List<BestiaryDTO.Monster> filteredList = allMonsters.stream()
                    .filter(m -> {
                        if (filter.isEmpty()) return true;
                        boolean matchesName = m.getName() != null && m.getName().toLowerCase().contains(filter);
                        boolean matchesGroup = m.getGroupName() != null && m.getGroupName().toLowerCase().contains(filter);
                        boolean matchesType = m.getType() != null && m.getType().toLowerCase().contains(filter);
                        boolean matchesTag = m.getTags() != null && m.getTags().stream()
                                .anyMatch(tag -> tag.toLowerCase().contains(filter));
                        return matchesName || matchesGroup || matchesType || matchesTag;
                    })
                    .toList();

            Map<String, List<BestiaryDTO.Monster>> groupedByMain = filteredList.stream()
                    .collect(Collectors.groupingBy(m -> m.getGroupName() != null ? m.getGroupName() : "Uncategorized"));

            groupedByMain.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(mainEntry -> {
                        String mainGroupName = mainEntry.getKey();
                        DefaultMutableTreeNode mainGroupNode = new DefaultMutableTreeNode(mainGroupName);

                        boolean isDragonGroup = mainGroupName.equalsIgnoreCase("Dragon");

                        Map<String, List<BestiaryDTO.Monster>> groupedByFirstTag = new TreeMap<>();
                        List<BestiaryDTO.Monster> untaggedMonsters = new ArrayList<>();

                        for (BestiaryDTO.Monster m : mainEntry.getValue()) {
                            List<String> tags = m.getTags();
                            if ((tags == null || tags.isEmpty()) && m.getSubgroups() != null && !m.getSubgroups().isEmpty()) {
                                tags = m.getSubgroups();
                            }
                            if (tags == null || tags.isEmpty()) {
                                untaggedMonsters.add(m);
                            } else {
                                String firstTag = tags.getFirst();
                                groupedByFirstTag.computeIfAbsent(firstTag, k -> new ArrayList<>()).add(m);
                            }
                        }

                        groupedByFirstTag.forEach((firstTagKey, firstTagList) -> {
                            DefaultMutableTreeNode firstTagNode = new DefaultMutableTreeNode(firstTagKey);

                            if (isDragonGroup) {
                                Map<String, List<BestiaryDTO.Monster>> groupedBySecondTag = new TreeMap<>();
                                List<BestiaryDTO.Monster> unnestedInFirst = new ArrayList<>();

                                for (BestiaryDTO.Monster m : firstTagList) {
                                    List<String> tags = m.getTags();
                                    if (tags != null && tags.size() >= 2) {
                                        String secondTag = tags.get(1);
                                        groupedBySecondTag.computeIfAbsent(secondTag, k -> new ArrayList<>()).add(m);
                                    } else {
                                        unnestedInFirst.add(m);
                                    }
                                }

                                groupedBySecondTag.forEach((secondTagKey, secondTagList) -> {
                                    DefaultMutableTreeNode secondTagNode = new DefaultMutableTreeNode(secondTagKey);
                                    secondTagList.stream()
                                            .map(BestiaryDTO.Monster::getName)
                                            .sorted()
                                            .distinct()
                                            .forEach(monsterName -> secondTagNode.add(new DefaultMutableTreeNode(monsterName)));
                                    firstTagNode.add(secondTagNode);
                                });

                                unnestedInFirst.stream()
                                        .map(BestiaryDTO.Monster::getName)
                                        .sorted()
                                        .distinct()
                                        .forEach(monsterName -> firstTagNode.add(new DefaultMutableTreeNode(monsterName)));

                            } else {
                                firstTagList.stream()
                                        .map(BestiaryDTO.Monster::getName)
                                        .sorted()
                                        .distinct()
                                        .forEach(monsterName -> firstTagNode.add(new DefaultMutableTreeNode(monsterName)));
                            }

                            mainGroupNode.add(firstTagNode);
                        });

                        untaggedMonsters.stream()
                                .map(BestiaryDTO.Monster::getName)
                                .sorted()
                                .distinct()
                                .forEach(monsterName -> mainGroupNode.add(new DefaultMutableTreeNode(monsterName)));

                        rootNode.add(mainGroupNode);
                    });
        }

        treeModel.reload();
        for (int i = 0; i < monsterTree.getRowCount(); i++) {
            monsterTree.expandRow(i);
        }
    }

    /**
     * Queries the new SQLite tables in bestiary.db for extended monster details
     * (savingThrows, skills, damageModifiers, conditionImmunities, languages,
     * senses, speeds, and abilities including Bonus Actions and recharge tags).
     */
    private ExtendedDbDetails loadExtendedDetailsFromDb(String monsterName) {
        ExtendedDbDetails details = new ExtendedDbDetails();
        File dbFile = new File(FileManager.getDirectory(), "bestiary.db");
        if (!dbFile.exists()) return details;

        String dbUrl = "jdbc:sqlite:" + dbFile.getAbsolutePath();
        try (Connection conn = DriverManager.getConnection(dbUrl)) {
            long monsterId = -1;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT id, passive_perception, proficiency_bonus, description FROM Monster WHERE name = ?")) {
                ps.setString(1, monsterName);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        monsterId = rs.getLong("id");
                        details.passivePerception = rs.getInt("passive_perception");
                        details.proficiencyBonus = rs.getInt("proficiency_bonus");
                        details.description = rs.getString("description") != null ? rs.getString("description") : "";
                    }
                }
            }
            if (monsterId == -1) return details;

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT ability_name, bonus FROM MonsterSavingThrow WHERE monster_id = ?")) {
                ps.setLong(1, monsterId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        details.savingThrows.put(rs.getString("ability_name"), rs.getInt("bonus"));
                    }
                }
            } catch (SQLException ignored) {}

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT s.name, ms.bonus FROM MonsterSkill ms JOIN Skill s ON ms.skill_id = s.id WHERE ms.monster_id = ?")) {
                ps.setLong(1, monsterId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        details.skills.put(rs.getString("name"), rs.getInt("bonus"));
                    }
                }
            } catch (SQLException ignored) {}

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT mdm.modifier_type, dt.name FROM MonsterDamageModifier mdm JOIN DamageType dt ON mdm.damage_type_id = dt.id WHERE mdm.monster_id = ?")) {
                ps.setLong(1, monsterId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String mod = rs.getString("modifier_type");
                        String dt = rs.getString("name");
                        if ("Vulnerability".equalsIgnoreCase(mod)) details.vulnerabilities.add(dt);
                        else if ("Resistance".equalsIgnoreCase(mod)) details.resistances.add(dt);
                        else if ("Immunity".equalsIgnoreCase(mod)) details.immunities.add(dt);
                    }
                }
            } catch (SQLException ignored) {}

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT c.name FROM MonsterConditionImmunity mci JOIN Condition c ON mci.condition_id = c.id WHERE mci.monster_id = ?")) {
                ps.setLong(1, monsterId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        details.conditionImmunities.add(rs.getString("name"));
                    }
                }
            } catch (SQLException ignored) {}

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT l.name FROM MonsterLanguage ml JOIN Language l ON ml.language_id = l.id WHERE ml.monster_id = ?")) {
                ps.setLong(1, monsterId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        details.languages.add(rs.getString("name"));
                    }
                }
            } catch (SQLException ignored) {}

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT st.name, ms.range_ft FROM MonsterSense ms JOIN SenseType st ON ms.sense_type_id = st.id WHERE ms.monster_id = ?")) {
                ps.setLong(1, monsterId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        details.senses.add(rs.getString("name") + " " + rs.getInt("range_ft") + " ft.");
                    }
                }
            } catch (SQLException ignored) {}

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT st.name, ms.speed_ft FROM MonsterSpeed ms JOIN SpeedType st ON ms.speed_type_id = st.id WHERE ms.monster_id = ?")) {
                ps.setLong(1, monsterId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        details.speeds.put(rs.getString("name"), rs.getInt("speed_ft"));
                    }
                }
            } catch (SQLException ignored) {}

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT ability_type, name, description, recharge FROM MonsterAbility WHERE monster_id = ? ORDER BY display_order")) {
                ps.setLong(1, monsterId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        details.abilities.add(new DbAbility(
                                rs.getString("ability_type"),
                                rs.getString("name"),
                                rs.getString("description"),
                                rs.getString("recharge")
                        ));
                    }
                }
            } catch (SQLException ignored) {}

        } catch (SQLException ignored) {}
        return details;
    }

    private void displaySelectedMonsterDetails(String selectedName) {
        successLabel.setVisible(false);
        BestiaryDatabase.findMonsterByName(selectedName).ifPresent(m -> {
            ExtendedDbDetails db = loadExtendedDetailsFromDb(selectedName);

            nameLabel.setText(m.getName());
            typeSizeAlignmentLabel.setText(String.format("%s %s, %s",
                    m.getSize() != null ? m.getSize() : "",
                    m.getType() != null ? m.getType() : "",
                    m.getAlignment() != null ? m.getAlignment() : ""
            ));

            acLabel.setText("🛡 AC: " + m.getArmorClass());

            if (m.getHitPoints() != null) {
                hpLabel.setText(String.format("❤️ HP: %d (%s)", m.getHitPoints().getAverage(), m.getHitPoints().getFormula()));
            } else {
                hpLabel.setText("❤️ HP: N/A");
            }

            crLabel.setText(String.format("⭐ CR: %s (%d XP) | PB: +%d",
                    m.getChallengeRating() != null ? m.getChallengeRating() : "0",
                    m.getExperiencePoints(),
                    db.proficiencyBonus
            ));

            Map<String, Integer> speeds = !db.speeds.isEmpty() ? db.speeds : m.getSpeed();
            if (speeds != null && !speeds.isEmpty()) {
                String speedStr = speeds.entrySet().stream()
                        .map(e -> e.getKey() + " " + e.getValue() + " ft.")
                        .collect(Collectors.joining(", "));
                speedLabel.setText("🏃 Speed: " + speedStr);
            } else {
                speedLabel.setText("🏃 Speed: N/A");
            }

            List<String> sensesList = new ArrayList<>(!db.senses.isEmpty()
                    ? db.senses
                    : (m.getSenses() != null ? m.getSenses() : List.of()));
            boolean hasPassive = sensesList.stream().anyMatch(s -> s.toLowerCase().contains("passive perception"));
            if (!hasPassive && db.passivePerception > 0) {
                sensesList.add("passive Perception " + db.passivePerception);
            }

            List<String> langsList = !db.languages.isEmpty()
                    ? db.languages
                    : (m.getLanguages() != null ? m.getLanguages() : List.of());

            StringBuilder sensesLangsHtml = new StringBuilder("<html><body style='width: 620px;'>");
            if (!sensesList.isEmpty()) {
                sensesLangsHtml.append("<b>👁 Senses:</b> ").append(String.join(", ", sensesList));
            }
            if (!langsList.isEmpty()) {
                if (!sensesList.isEmpty()) sensesLangsHtml.append(" &nbsp;|&nbsp; ");
                sensesLangsHtml.append("<b>🗣 Languages:</b> ").append(String.join(", ", langsList));
            }
            sensesLangsHtml.append("</body></html>");
            sensesLanguagesLabel.setText(sensesLangsHtml.toString());

            Map<String, Integer> savesMap = !db.savingThrows.isEmpty()
                    ? db.savingThrows
                    : (m.getSavingThrows() != null ? m.getSavingThrows() : Map.of());
            Map<String, Integer> skillsMap = !db.skills.isEmpty()
                    ? db.skills
                    : (m.getSkills() != null ? m.getSkills() : Map.of());
            List<String> vulnsList = db.vulnerabilities;
            List<String> resistsList = !db.resistances.isEmpty()
                    ? db.resistances
                    : (m.getDamageResistances() != null ? m.getDamageResistances() : List.of());
            List<String> immunesList = !db.immunities.isEmpty()
                    ? db.immunities
                    : (m.getDamageImmunities() != null ? m.getDamageImmunities() : List.of());
            List<String> condImmList = !db.conditionImmunities.isEmpty()
                    ? db.conditionImmunities
                    : (m.getConditionImmunities() != null ? m.getConditionImmunities() : List.of());

            List<String> defParts = new ArrayList<>();
            if (!savesMap.isEmpty()) {
                String sv = savesMap.entrySet().stream()
                        .map(e -> e.getKey() + " " + (e.getValue() >= 0 ? "+" + e.getValue() : e.getValue()))
                        .collect(Collectors.joining(", "));
                defParts.add("<b>🛡️ Saves:</b> " + sv);
            }
            if (!skillsMap.isEmpty()) {
                String sk = skillsMap.entrySet().stream()
                        .map(e -> e.getKey() + " " + (e.getValue() >= 0 ? "+" + e.getValue() : e.getValue()))
                        .collect(Collectors.joining(", "));
                defParts.add("<b>🎯 Skills:</b> " + sk);
            }
            if (!vulnsList.isEmpty()) {
                defParts.add("<b>💥 Vulnerabilities:</b> " + String.join(", ", vulnsList));
            }
            if (!resistsList.isEmpty()) {
                defParts.add("<b>⚡ Resistances:</b> " + String.join(", ", resistsList));
            }
            if (!immunesList.isEmpty()) {
                defParts.add("<b>🛡 Damage Immunities:</b> " + String.join(", ", immunesList));
            }
            if (!condImmList.isEmpty()) {
                defParts.add("<b>🚫 Condition Immunities:</b> " + String.join(", ", condImmList));
            }

            if (!defParts.isEmpty()) {
                defenseLabel.setText("<html><body style='width: 620px;'>" + String.join(" &nbsp;|&nbsp; ", defParts) + "</body></html>");
            } else {
                defenseLabel.setText("");
            }

            if (m.getStats() != null) {
                StringBuilder sb = new StringBuilder();
                String[] statsKeys = {"strength", "dexterity", "constitution", "intelligence", "wisdom", "charisma"};
                for (String key : statsKeys) {
                    Number valNum = m.getStats().get(key);
                    if (valNum != null) {
                        int val = valNum.intValue();
                        int mod = Math.floorDiv(val - 10, 2);
                        sb.append(String.format(" %3.3s: %2d (%+d) |", key.toUpperCase(), val, mod));
                    }
                }
                statsArea.setText(sb.toString());
            } else {
                statsArea.setText("");
            }

            StringBuilder abilities = new StringBuilder();
            if (db.description != null && !db.description.isBlank()) {
                abilities.append(db.description).append("\n\n");
            }

            if (!db.abilities.isEmpty()) {
                appendCategorySection(abilities, "TRAITS", db.abilities, "Trait");
                appendCategorySection(abilities, "ACTIONS", db.abilities, "Action");
                appendCategorySection(abilities, "BONUS ACTIONS", db.abilities, "Bonus Action");
                appendCategorySection(abilities, "REACTIONS", db.abilities, "Reaction");
                appendCategorySection(abilities, "LEGENDARY ACTIONS", db.abilities, "Legendary Action");
                appendCategorySection(abilities, "LAIR ACTIONS", db.abilities, "Lair Action");
            } else {
                if (m.getTraits() != null && !m.getTraits().isEmpty()) {
                    abilities.append("--- TRAITS ---\n");
                    for (BestiaryDTO.Monster.Feature t : m.getTraits()) {
                        abilities.append("• ").append(t.getName()).append(": ").append(t.getDescription()).append("\n\n");
                    }
                }
                if (m.getActions() != null && !m.getActions().isEmpty()) {
                    abilities.append("--- ACTIONS ---\n");
                    for (BestiaryDTO.Monster.Feature a : m.getActions()) {
                        abilities.append("• ").append(a.getName()).append(": ").append(a.getDescription()).append("\n\n");
                    }
                }
                if (m.getReactions() != null && !m.getReactions().isEmpty()) {
                    abilities.append("--- REACTIONS ---\n");
                    for (BestiaryDTO.Monster.Feature r : m.getReactions()) {
                        abilities.append("• ").append(r.getName()).append(": ").append(r.getDescription()).append("\n\n");
                    }
                }
                if (m.getLegendaryActions() != null && m.getLegendaryActions().getActions() != null) {
                    abilities.append("--- LEGENDARY ACTIONS ---\n");
                    for (BestiaryDTO.Monster.Feature la : m.getLegendaryActions().getActions()) {
                        abilities.append("• ").append(la.getName()).append(": ").append(la.getDescription()).append("\n\n");
                    }
                }
                if (m.getLairActions() != null && m.getLairActions().getActions() != null) {
                    abilities.append("--- LAIR ACTIONS ---\n");
                    for (BestiaryDTO.Monster.Feature lra : m.getLairActions().getActions()) {
                        abilities.append("• ").append(lra.getName()).append(": ").append(lra.getDescription()).append("\n\n");
                    }
                }
            }

            abilitiesArea.setText(abilities.toString());
            abilitiesArea.setCaretPosition(0);
        });
    }

    private void appendCategorySection(StringBuilder sb, String header, List<DbAbility> allAbilities, String categoryFilter) {
        List<DbAbility> matching = allAbilities.stream()
                .filter(a -> categoryFilter.equalsIgnoreCase(a.category()))
                .toList();
        if (matching.isEmpty()) return;

        sb.append("--- ").append(header).append(" ---\n");
        for (DbAbility ab : matching) {
            sb.append("• ").append(ab.name());
            if (ab.recharge() != null && !ab.recharge().isBlank() && !ab.name().contains("(")) {
                String r = ab.recharge().trim();
                if (r.matches("^\\d+(-\\d+)?$")) {
                    sb.append(" (Recharge ").append(r).append(")");
                } else {
                    sb.append(" (").append(r).append(")");
                }
            }
            sb.append(": ").append(ab.description()).append("\n\n");
        }
    }

    private void clearMonsterDetails() {
        nameLabel.setText("Select a monster");
        typeSizeAlignmentLabel.setText("");
        acLabel.setText("");
        hpLabel.setText("");
        speedLabel.setText("");
        sensesLanguagesLabel.setText("");
        defenseLabel.setText("");
        crLabel.setText("");
        statsArea.setText("");
        abilitiesArea.setText("");
        addToTrackerButton.setEnabled(false);
    }

    private void addCurrentMonsterToTracker() {
        if (selectedMonsterName == null || selectedMonsterName.isBlank()) return;

        String customName = customNameField.getText().trim();
        int count = (int) countSpinner.getValue();

        HPMode hpMode;
        if (averageHpButton.isSelected()) {
            hpMode = HPMode.AVERAGE;
        } else if (rollHpButton.isSelected()) {
            hpMode = HPMode.ROLL;
        } else {
            hpMode = HPMode.CUSTOM;
        }

        int manualHp = (int) manualHpSpinner.getValue();

        boolean success;
        if (autoInitButton.isSelected()) {
            success = BestiaryTrackerIntegration.addMonsterToTracker(
                    characterManager, selectedMonsterName, customName, count, hpMode, manualHp
            );
        } else {
            int manualInit = (int) manualInitSpinner.getValue();
            success = BestiaryTrackerIntegration.addMonsterToTracker(
                    characterManager, selectedMonsterName, customName, count, hpMode, manualHp, manualInit
            );
        }

        if (success) {
            successLabel.setVisible(true);
            customNameField.setText("");
            countSpinner.setValue(1);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add monster to tracker.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}