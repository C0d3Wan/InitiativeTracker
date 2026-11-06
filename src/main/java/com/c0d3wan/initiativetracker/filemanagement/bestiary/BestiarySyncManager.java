package com.c0d3wan.initiativetracker.filemanagement.bestiary;

import com.c0d3wan.initiativetracker.filemanagement.bestiary.data.BestiaryDTO;

import com.google.gson.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class BestiarySyncManager {
    // Use BestiaryDatabase's GSON instance which has LOWER_CASE_WITH_UNDERSCORES enabled
    private static final Gson GSON = BestiaryDatabase.GSON;
    private static final String OFFICIAL_DATA_PATH = "/data/official_monsters.json";

    /**
     * Runs on application startup to sync official monsters into the database
     * without touching user-created homebrew records.
     */
    public static void syncOfficialMonsters() {
        System.out.println("[INFO] Starting official bestiary synchronization...");

        try (InputStream inputStream = BestiarySyncManager.class.getResourceAsStream(OFFICIAL_DATA_PATH)) {
            if (inputStream == null) {
                System.err.println("[WARN] Could not find bundled official monsters file at: " + OFFICIAL_DATA_PATH);
                return;
            }

            try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                BestiaryDTO dto = GSON.fromJson(reader, BestiaryDTO.class);

                if (dto == null || dto.getMonsters() == null) {
                    System.err.println("[WARN] Official monsters JSON was empty or malformed.");
                    return;
                }

                int count = 0;
                for (BestiaryDTO.Monster monster : dto.getMonsters()) {
                    BestiaryDatabase.saveOfficialMonster(monster);
                    count++;
                }

                System.out.println("[SUCCESS] Successfully synced " + count + " official monsters into the database.");
            }

        } catch (Exception e) {
            System.err.println("[ERROR] Failed to synchronize official monsters: " + e.getMessage());
            e.printStackTrace();
        }
    }
}