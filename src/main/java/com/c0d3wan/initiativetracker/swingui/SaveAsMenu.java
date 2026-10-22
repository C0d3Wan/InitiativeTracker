package com.c0d3wan.initiativetracker.swingui;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class SaveAsMenu extends JDialog {
    private final JPanel saveMenuPane = new JPanel();
    private final JTextField saveFileText = new JTextField();

    private final JButton savedToFileButton = new JButton("Save to File");
    private final JLabel savedLabel = new JLabel("Saved to file");

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
        boolean isValid = !name.isEmpty();

        savedToFileButton.setEnabled(isValid);
    }
}
