package com.c0d3wan.initiativetracker.FileManagement;

import java.io.File;
import java.util.*;

/**
 * Manages platform-specific directory routing, file paths, and file discovery
 * for configuration and save data across Linux, Windows, and development environments.
 */
public class FileManager {

    private static final boolean PACKAGED = detectPackaged();

    /**
     * Determines whether the application is running from a packaged JAR file or directly in an IDE/class path.
     *
     * @return {@code true} if running inside a JAR archive; {@code false} otherwise.
     */
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

    /**
     * Resolves and creates the platform-dependent base configuration directory.
     * <p>
     * When packaged as a JAR, uses standard OS conventions (%APPDATA% on Windows,
     * {@code ~/Library/Application Support} on macOS, and {@code $XDG_CONFIG_HOME} or {@code ~/.config} on Linux).
     * In development mode, falls back to a local {@code conf} folder.
     *
     * @return The canonical {@link File} directory for configuration files.
     */
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

    /**
     * Resolves and creates the directory designated for combat save files.
     *
     * @return The {@link File} representing the saves directory.
     */
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

    /**
     * Lists all files located in the save directory.
     *
     * @return A {@link List} of all files found in the saves folder.
     */
    public static List<File> getFiles() {
        return getFiles(null);
    }

    /**
     * Lists files located in the save directory filtered by file extension.
     *
     * @param extension The required file suffix (e.g., {@code ".json"}), or {@code null} to return all files.
     * @return A {@link List} of matching {@link File} objects.
     */
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
