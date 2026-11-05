package com.c0d3wan.initiativetracker.swingui.bestiary;

import com.c0d3wan.initiativetracker.characters.CharacterManager;
import com.c0d3wan.initiativetracker.filemanagement.bestiary.BestiaryDatabase;
import com.c0d3wan.initiativetracker.filemanagement.bestiary.BestiaryTrackerIntegration;
import com.c0d3wan.initiativetracker.filemanagement.bestiary.data.BestiaryDTO;
import com.c0d3wan.initiativetracker.filemanagement.bestiary.data.HPMode;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Modal {@link JDialog} interface for browsing, viewing, and adding monsters from the bestiary database.
 */
public class BestiaryMenu extends JDialog {
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

    public BestiaryMenu(Frame parent, CharacterManager characterManager) {
        super(parent, "Bestiary Database", true);
        this.characterManager = characterManager;
        setupMenu();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);

        setMinimumSize(new Dimension(1000, 720));
        setSize(1000, 720);
        pack();

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
            AddHomebrewMonsterMenu homebrewMenu = new AddHomebrewMonsterMenu((Frame) getOwner());
            homebrewMenu.setVisible(true);
            if (homebrewMenu.isConfirmed()) {
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
        pane.setLayout(new MigLayout("insets 6 6 6 6, fill", "[320px::320px, fill][grow, fill]", "[][grow]"));
        pane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        setContentPane(pane);

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

        JPanel rightPanel = new JPanel(new MigLayout("insets 4 10 4 4, fill", "[grow]", "[][][][][][][grow, fill][pref!]"));
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
        abilitiesScroll.setBorder(BorderFactory.createTitledBorder("Traits & Actions"));

        rightPanel.add(nameLabel, "cell 0 0, wrap");
        rightPanel.add(typeSizeAlignmentLabel, "cell 0 1, wrap");

        JPanel statsRow = new JPanel(new MigLayout("insets 0, gap 15 0", "[][][]", "[]"));
        statsRow.setOpaque(false);
        statsRow.add(acLabel);
        statsRow.add(hpLabel);
        statsRow.add(crLabel);
        rightPanel.add(statsRow, "cell 0 2, wrap");

        rightPanel.add(speedLabel, "cell 0 3, wrap");
        rightPanel.add(sensesLanguagesLabel, "cell 0 4, wrap");
        rightPanel.add(statsArea, "cell 0 5, wrap, growx");
        rightPanel.add(abilitiesScroll, "cell 0 6, grow, wrap");

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

        rightPanel.add(trackerControlPanel, "cell 0 7, growx");

        pane.add(leftPanel, "cell 0 0 1 2, grow");
        pane.add(rightPanel, "cell 1 0 1 2, grow");
    }

    private void loadMonsters() {
        allMonsters = BestiaryDatabase.loadAllMonsters();
        filterMonsters();
    }

    private void filterMonsters() {
        String filter = searchField.getText().trim().toLowerCase();
        rootNode.removeAllChildren();

        if (allMonsters != null) {
            Map<String, List<BestiaryDTO.Monster>> groupedMonsters = allMonsters.stream()
                    .filter(m -> filter.isEmpty() || m.getName().toLowerCase().contains(filter) ||
                            (m.getGroupName() != null && m.getGroupName().toLowerCase().contains(filter)))
                    .collect(Collectors.groupingBy(m -> m.getGroupName() != null ? m.getGroupName() : "Uncategorized"));

            groupedMonsters.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode(entry.getKey());
                        entry.getValue().stream()
                                .map(BestiaryDTO.Monster::getName)
                                .sorted()
                                .forEach(monsterName -> groupNode.add(new DefaultMutableTreeNode(monsterName)));
                        rootNode.add(groupNode);
                    });
        }

        treeModel.reload();
        for (int i = 0; i < monsterTree.getRowCount(); i++) {
            monsterTree.expandRow(i);
        }
    }

    private void displaySelectedMonsterDetails(String selectedName) {
        successLabel.setVisible(false);
        BestiaryDatabase.findMonsterByName(selectedName).ifPresent(m -> {
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

            crLabel.setText("⭐ CR: " + (m.getChallengeRating() != null ? m.getChallengeRating() : "0")
                    + " (" + m.getExperiencePoints() + " XP)");

            if (m.getSpeed() != null && !m.getSpeed().isEmpty()) {
                String speedStr = m.getSpeed().entrySet().stream()
                        .map(e -> e.getKey() + " " + e.getValue() + " ft.")
                        .collect(Collectors.joining(", "));
                speedLabel.setText("🏃 Speed: " + speedStr);
            } else {
                speedLabel.setText("🏃 Speed: N/A");
            }

            StringBuilder sensesLangs = new StringBuilder();
            if (m.getSenses() != null && !m.getSenses().isEmpty()) {
                sensesLangs.append("👁 Senses: ").append(String.join(", ", m.getSenses()));
            }
            if (m.getLanguages() != null && !m.getLanguages().isEmpty()) {
                if (sensesLangs.length() > 0) sensesLangs.append(" | ");
                sensesLangs.append("🗣 Languages: ").append(String.join(", ", m.getLanguages()));
            }
            sensesLanguagesLabel.setText(sensesLangs.toString());

            if (m.getStats() != null) {
                StringBuilder sb = new StringBuilder();
                String[] statsKeys = {"strength", "dexterity", "constitution", "intelligence", "wisdom", "charisma"};
                for (String key : statsKeys) {
                    Number valNum = m.getStats().get(key);
                    if (valNum != null) {
                        int val = valNum.intValue();
                        int mod = (val - 10) / 2;
                        sb.append(String.format(" %3.3s: %d (%+d) |", key.toUpperCase(), val, mod));
                    }
                }
                statsArea.setText(sb.toString());
            } else {
                statsArea.setText("");
            }

            StringBuilder abilities = new StringBuilder();
            if (m.getTraits() != null && !m.getTraits().isEmpty()) {
                abilities.append("--- TRAITS ---\n");
                for (BestiaryDTO.Feature t : m.getTraits()) {
                    abilities.append("• ").append(t.getName()).append(": ").append(t.getDescription()).append("\n\n");
                }
            }
            if (m.getActions() != null && !m.getActions().isEmpty()) {
                abilities.append("--- ACTIONS ---\n");
                for (BestiaryDTO.Feature a : m.getActions()) {
                    abilities.append("• ").append(a.getName()).append(": ").append(a.getDescription()).append("\n\n");
                }
            }
            abilitiesArea.setText(abilities.toString());
            abilitiesArea.setCaretPosition(0);
        });
    }

    private void clearMonsterDetails() {
        nameLabel.setText("Select a monster");
        typeSizeAlignmentLabel.setText("");
        acLabel.setText("");
        hpLabel.setText("");
        speedLabel.setText("");
        sensesLanguagesLabel.setText("");
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
            hpMode = HPMode.MANUAL;
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