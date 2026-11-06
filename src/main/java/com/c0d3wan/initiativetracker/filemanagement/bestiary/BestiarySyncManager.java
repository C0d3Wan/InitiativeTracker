package com.c0d3wan.initiativetracker.filemanagement.bestiary;

import com.c0d3wan.initiativetracker.filemanagement.bestiary.data.BestiaryDTO;

import com.google.gson.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Handles application startup synchronisation of bundled official D&D 5e monsters
 * from resource JSON files into the SQLite bestiary database without impacting user homebrew records.
 */
public class BestiarySyncManager {
    private static final Gson GSON = BestiaryDatabase.GSON;
    private static final String OFFICIAL_DATA_PATH = "/data/official_monsters.json";

    /**
     * Executes on application startup to parse bundled official monster records and sync them into the database.
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