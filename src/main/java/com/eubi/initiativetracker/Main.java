package com.eubi.initiativetracker;

import com.eubi.initiativetracker.swingui.MainMenu;

import javax.swing.*;

public class Main {
    static void main() {
        SwingUtilities.invokeLater(() -> new MainMenu().setVisible(true));
    }
}
