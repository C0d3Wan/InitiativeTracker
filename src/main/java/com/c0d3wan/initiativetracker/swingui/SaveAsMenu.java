package com.c0d3wan.initiativetracker.swingui;

import com.c0d3wan.initiativetracker.FileManagement.FileManager;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * A front end menu for creating a save file for the current Initiative list
 */
public class SaveAsMenu extends JDialog {
    private final JPanel saveMenuPane = new JPanel();
    private final JTextField saveFileText = new JTextField();

    private final JButton savedToFileButton = new JButton("Save to File");
    private final JLabel savedLabel = new JLabel("Saved to file");

    private boolean confirmed = false;
    private String saveFile;

    /**
     * Constructs a new menu for saving the current initiative list to a new file
     * @param parent The parent frame for creating a modal pop up
     */
    public SaveAsMenu(Frame parent) {
        super(parent, "Save As", true);
        setupMenu();

        setTitle("Save To File");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        saveFileText.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                validateFileName();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                validateFileName();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                validateFileName();
            }
        });
        savedToFileButton.addActionListener(e -> saveList());
    }

    private void setupMenu() {
        Dimension windowSize = new Dimension(250, 107);
        saveMenuPane.setLayout(new MigLayout("insets 2 2 2 2, fillx", "[l][r]", "[][][]"));
        saveMenuPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        setMinimumSize(windowSize);
        setSize(windowSize);

        setContentPane(saveMenuPane);

        final JLabel saveToFileLabel = new JLabel("File to save to:");

        savedLabel.setVisible(false);
        savedToFileButton.setEnabled(false);
        savedToFileButton.setFocusPainted(false);

        saveMenuPane.add(saveToFileLabel, "cell 0 0");
        saveMenuPane.add(saveFileText, "cell 1 0, growx, push");
        saveMenuPane.add(savedToFileButton, "cell 1 1, growx, push");
        saveMenuPane.add(savedLabel, "cell 0 2, span, growx, push, hidemode 3");


    }

    private void validateFileName() {
        String name = saveFileText.getText().trim();
        List<File> files = FileManager.getFiles();
        boolean isValid = !name.isEmpty();
        for (File file: files) {
            String fileName = file.getName().replaceAll(".json", "");
            if (file.getName().equals(name) || fileName.equals(name)) {
                isValid = false;
                break;
            }
        }

        savedToFileButton.setEnabled(isValid);
    }

    private void saveList() {
        saveFile = saveFileText.getText().trim();
        confirmed = true;
        savedLabel.setVisible(true);
        pack();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getSavefile() {
        return saveFile;
    }
}
