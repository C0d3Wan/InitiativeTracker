package com.c0d3wan.initiativetracker.swingui;

import com.c0d3wan.initiativetracker.FileManagement.FileManager;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * Modal {@link JDialog} interface for loading Inistiative list from file
 */
public class LoadFileMenu extends JDialog {
    private final JPanel loadMenuPane = new JPanel();
    private final JComboBox<String> fileSelection = new JComboBox<>();
    private final JButton loadButton = new JButton("Load File");

    private boolean itemSelected = false;
    private String selectedFile;

    /**
     * Constructs a modal dialogue to load character list from a file
     * @param parent The parent {@link Frame} window binding this dialogue.
     */
    public LoadFileMenu(Frame parent) {
        super(parent, "Load File", true);
        setupMenu();

        setTitle("Load File");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);

        setupCombo();

        pack();
        setMinimumSize(new Dimension(250, 107));

        loadButton.addActionListener(e -> loadFile());
    }

    /**
     * Sets up the Layout manager, layout, widgets and border for the {@link Frame}
     */
    private void setupMenu() {
        loadMenuPane.setLayout(new MigLayout("insets 2 2 2 2, fillx", "[l][r, grow, push]", "[][][]"));
        loadMenuPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));

        setContentPane(loadMenuPane);

        final JLabel fileToLoadLabel = new JLabel("Choose File to Load");

        loadButton.setEnabled(false);
        loadButton.setFocusPainted(false);

        loadMenuPane.add(fileToLoadLabel, "cell 0 0");
        loadMenuPane.add(fileSelection, "cell 1 0, growx, push");
        loadMenuPane.add(loadButton, "cell 1 1, growx, push");
    }

    /**
     * Populates the {@link JComboBox} to contain all the save file names
     */
    private void setupCombo() {
        List<File> files = FileManager.getFiles();
        if (files == null || files.isEmpty()) {
            fileSelection.addItem("No files");
        } else {
            loadButton.setEnabled(true);
            for (File file: files) {
                fileSelection.addItem(file.getName());
            }
        }
    }

    /**
     * Saves the file name to be returned later
     */
    private void loadFile() {
        itemSelected = true;
        Object selectedItem = fileSelection.getSelectedItem();
        if (selectedItem == null) {
            return;
        }
        selectedFile = selectedItem.toString();

        dispose();
    }

    /**
     * Gets the selected file name
     * @return {@code true} if confirmed; {@code false} if cancelled or closed.
     */
    public String getFileToLoad() {
        return selectedFile;
    }

    /**
     * Checks to see if a file was selected
     * @return {@link itemSelected}
     */
    public boolean isItemSelected() {
        return itemSelected;
    }
}
