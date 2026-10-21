package com.c0d3wan.initiativetracker.FileManagement;

import java.io.File;

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
     * Resolves and returns the appropriate directory for saving and loading encounter files.
     * <p>
     * Returns a local {@code "./saves"} folder when running in an IDE, or
     * {@code "~/InitiativeTrackerSaves"} in the user's home directory when packaged as a JAR.
     * Automatically creates the directory if it does not already exist.
     *
     * @return A {@link File} representing the target save directory.
     */
    public static File getSaveDirectory() {
        File saveDir;

        if (isPackaged()) {
            saveDir = new File(System.getProperty("user.home"), "InitiativeTrackerSaves");
        } else {
            saveDir = new File("saves");
        }

        if (!saveDir.exists() && !saveDir.mkdirs()) {
            System.err.println("Warning: Could not create save directory: " + saveDir.getAbsolutePath());
        }

        return saveDir;
    }

}
