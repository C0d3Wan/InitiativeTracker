package com.c0d3wan.initiativetracker.ui.util;

import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;

public final class UIUtils {
    private UIUtils() {
        throw new IllegalStateException("Utility Class");
    }

    public static void setFieldHint(JTextField field, String placeholder, String tooltip) {
        field.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        field.setToolTipText(tooltip);
    }
}
