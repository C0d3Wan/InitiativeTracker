package com.c0d3wan.initiativetracker.FileManagement;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Manages platform-specific directory routing, file paths, and file discovery
 * for configuration and save data across Linux, Windows, and development environments.
 */
public class FileManager {

    /**
     * Detects whether the application is currently running from a packaged JAR file
     * or unpacked classes inside an IDE environment.
     *
     * @return {@code true} if running from a packaged JAR; {@code false} otherwise.
     */
    public static boolean isPackaged() {
        try {
            String path = SaveLoadManager.class
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
     * Resolves and returns the appropriate platform-specific configuration/data directory.
     * <p>
     * - **Linux:** ~/.config/InitiativeTracker/
     * - **Windows:** %APPDATA%/InitiativeTracker/
     * - **Development (IntelliJ):** ./Conf
     *
     * @return A {@link File} representing the target save directory.
     */
    public static File getDirectory() {
        File Dir;

        if (isPackaged()) {
            String os = System.getProperty("os.name").toLowerCase();
            String userHome = System.getProperty("user.home");

            if (os.contains("win")) {
                String appData = System.getenv("APPDATA");
                if (appData != null && !appData.isEmpty()) {
                    Dir = new File(appData, "InitiativeTracker/");
                } else {
                    Dir = new File(userHome, "AppData/Roaming/InitiativeTracker/");
                }
            } else if (os.contains("mac")) {
                Dir = new File(userHome, "Library/Application Support/InitiativeTracker/");
            } else {
                String xdgConfig = System.getenv("XDG_CONFIG_HOME");
                if (xdgConfig != null && !xdgConfig.isEmpty()) {
                    Dir = new File(xdgConfig, "InitiativeTracker/");
                } else {
                    Dir = new File(userHome, ".config/InitiativeTracker/");
                }
            }
        } else {
            Dir = new File("conf");
        }

        if (!Dir.exists() && !Dir.mkdirs()) {
            System.err.println("Warning: Could not create directory: " + Dir.getAbsolutePath());
        }

        return Dir;
    }

    /**
     * Resolves and returns the appropriate platform-specific configuration/data directory
     * for saving encounters.
     * <p>
     * - **Linux:** ~/.config/InitiativeTracker/saves/
     * - **Windows:** %APPDATA%/InitiativeTracker/saves/
     * - **Development (IntelliJ):** ./saves/
     *
     * @return A {@link File} representing the target save directory.
     */
    public static File getSaveDirectory() {
        File saveDir;

        if (isPackaged()) {
            saveDir = new File(getDirectory(), "/saves/");
        } else {
            saveDir = new File("saves");
        }

        if (!saveDir.exists() && !saveDir.mkdirs()) {
            System.err.println("Warning: Could not create save directory: " + saveDir.getAbsolutePath());
        }

        return saveDir;
    }

    /**
     * Retrieves a list of all files located within the save directory.
     *
     * @return A list of files, or an empty list if none are found.
     */
    public static List<File> getFiles() {
        return getFiles(null);
    }

    /**
     * Retrieves a list of files located within the save directory, filtered by an optional file extension.
     *
     * @param extension Optional file extension filter (e.g. {@code ".json"}), or {@code null} to return all files.
     * @return A list of matching files, or an empty list if none are found.
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

        return files != null ? Arrays.asList(files) : Collections.emptyList();
    }

}
