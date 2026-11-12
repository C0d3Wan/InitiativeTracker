package com.c0d3wan.initiativetracker.ui.util;

import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public final class UIUtils {
    private UIUtils() {
        throw new IllegalStateException("Utility Class");
    }

    public static void setPlaceholderTooltip(JTextField field, String placeholder, String tooltip) {
        field.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        field.setToolTipText(tooltip);
    }

    public static void setPlaceholderTooltip(String PLACEHOLDER_KEY, JTextArea area, String placeholder, String tooltip) {
        area.setToolTipText(tooltip);
        Color normalFg = UIManager.getColor("TextArea.foreground") != null ? UIManager.getColor("TextArea.foreground") : Color.LIGHT_GRAY;
        Color hintFg = UIManager.getColor("Component.placeholderForeground") != null ? UIManager.getColor("Component.placeholderForeground") : Color.GRAY;
        Font normalFont = area.getFont();
        Font italicFont = normalFont.deriveFont(Font.ITALIC);

        area.setText(placeholder);
        area.setForeground(hintFg);
        area.setFont(italicFont);
        area.putClientProperty(PLACEHOLDER_KEY, Boolean.TRUE);

        area.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (Boolean.TRUE.equals(area.getClientProperty(PLACEHOLDER_KEY))) {
                    area.setText("");
                    area.setForeground(normalFg);
                    area.setFont(normalFont);
                    area.putClientProperty(PLACEHOLDER_KEY, Boolean.FALSE);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (area.getText().trim().isEmpty()) {
                    area.setText(placeholder);
                    area.setForeground(hintFg);
                    area.setFont(italicFont);
                    area.putClientProperty(PLACEHOLDER_KEY, Boolean.TRUE);
                }
            }
        });
    }
}
