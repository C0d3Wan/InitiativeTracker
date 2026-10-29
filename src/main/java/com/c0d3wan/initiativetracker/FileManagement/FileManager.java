package com.c0d3wan.initiativetracker.FileManagement;

import java.io.File;
import java.util.*;

/**
 * Manages platform-specific directory routing, file paths, and file discovery
 * for configuration and save data across Linux, Windows, and development environments.
 */
public class FileManager {

    private static final boolean PACKAGED = detectPackaged();

    public static boolean detectPackaged() {
        try {
            String path = FileManager.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
                    .getPath();
            return path != null && path.endsWith(".jar");
        } catch (Exception e) {
            return false;
        }
    }

    public static File getDirectory() {
        File directory;

        if (PACKAGED) {
            String os = System.getProperty("os.name").toLowerCase();
            String userHome = System.getProperty("user.home");

            if (os.contains("win")) {
                String appData = System.getenv("APPDATA");
                if (appData != null && !appData.isEmpty()) {
                    directory = new File(appData, "InitiativeTracker/");
                } else {
                    directory = new File(userHome, "AppData/Roaming/InitiativeTracker/");
                }
            } else if (os.contains("mac")) {
                directory = new File(userHome, "Library/Application Support/InitiativeTracker/");
            } else {
                String xdgConfig = System.getenv("XDG_CONFIG_HOME");
                if (xdgConfig != null && !xdgConfig.isEmpty()) {
                    directory = new File(xdgConfig, "InitiativeTracker/");
                } else {
                    directory = new File(userHome, ".config/InitiativeTracker/");
                }
            }
        } else {
            directory = new File("conf");
        }

        if (!directory.exists() && !directory.mkdirs()) {
            System.err.println("Warning: Could not create directory: " + directory.getAbsolutePath());
        }

        return directory;
    }

    public static File getSaveDirectory() {
        File saveDir;

        if (PACKAGED) {
            saveDir = new File(getDirectory(), "saves");
        } else {
            saveDir = new File("saves");
        }

        if (!saveDir.exists() && !saveDir.mkdirs()) {
            System.err.println("Warning: Could not create save directory: " + saveDir.getAbsolutePath());
        }

        return saveDir;
    }

    public static List<File> getFiles() {
        return getFiles(null);
    }

    public static List<File> getFiles(String extension) {
        File dir = getSaveDirectory();
        if (!dir.exists() || !dir.isDirectory()) {
            return Collections.emptyList();
        }

        File[] files;
        if (extension != null && !extension.isEmpty()) {
            final String ext = extension.toLowerCase();
            files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(ext) && new File(d, name).isFile());
        } else {
            files = dir.listFiles(File::isFile);
        }

        return files != null ? new ArrayList<>(Arrays.asList(files)) : Collections.emptyList();
    }

}
