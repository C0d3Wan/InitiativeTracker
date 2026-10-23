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
    private final JLabel fileLoadedLabel = new JLabel("File Loaded!!");

    private boolean itemSelected = false;
    private String selectedFile;

    /**
     * Constructs a modal dialog to load character list from a file
     * @param parent The parent {@link Frame} window binding this dialog.
     */
    public LoadFileMenu(Frame parent) {
        super(parent, "Load File", true);
        setupMenu();

        setTitle("Load File");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setupCombo();

        loadButton.addActionListener(e -> loadFile());
    }

    /**
     * Sets up the Layout manager, layout, widgets and border for the {@link Frame}
     */
    private void setupMenu() {
        Dimension windowSize = new Dimension(250, 107);
        loadMenuPane.setLayout(new MigLayout("insets 2 2 2 2, fillx", "[l][r, grow, push]", "[][][]"));
        loadMenuPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        setMinimumSize(windowSize);
        setSize(windowSize);

        setContentPane(loadMenuPane);

        final JLabel fileToLoadLabel = new JLabel("Choose File to Load");

        fileLoadedLabel.setVisible(false);
        loadButton.setEnabled(false);
        loadButton.setFocusPainted(false);

        loadMenuPane.add(fileToLoadLabel, "cell 0 0");
        loadMenuPane.add(fileSelection, "cell 1 0, growx, push");
        loadMenuPane.add(loadButton, "cell 1 1, growx, push");
        loadMenuPane.add(fileLoadedLabel, "cell 0 2, span, hidemode 3, align center");
    }

    /**
     * Populates the {@link JComboBox} to contain all the save file names
     */
    private void setupCombo() {
        List<File> files = FileManager.getFiles();
        if (files == null || files.isEmpty()) {
            fileSelection.addItem("No files");
        } else {
            for (File file: files) {
                fileSelection.addItem(file.getName());
                loadButton.setEnabled(true);
            }
        }
        pack();
    }

    /**
     * Saves the file name to be returned later
     */
    private void loadFile() {
        itemSelected = true;
        Object selectedItem = fileSelection.getSelectedItem();
        assert selectedItem != null;
        selectedFile = selectedItem.toString();
        fileLoadedLabel.setVisible(true);
        loadButton.setEnabled(false);
        pack();
    }

    /**
     * Gets the selected file name
     * @return {@code true} if confirmed; {@code false} if canceled or closed.
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
