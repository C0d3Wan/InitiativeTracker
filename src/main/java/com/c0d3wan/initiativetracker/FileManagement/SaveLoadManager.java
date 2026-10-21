package com.c0d3wan.initiativetracker.FileManagement;

import com.c0d3wan.initiativetracker.characters.CharacterManager;
import com.c0d3wan.initiativetracker.characters.NonePlayerCharacter;
import com.c0d3wan.initiativetracker.characters.Character;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

/**
 * Handles serializing, deserializing, encrypting, and decrypting combat encounters
 * to and from secure JSON files.
 * <p>
 * Manages dynamic directory routing depending on whether the application is running
 * inside an IDE or from a packaged JAR release.
 */
public class SaveLoadManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final String ALGORITHM = "AES";

    private static String currentFileName = null;

    /**
     * Initializes and retrieves the AES secret key used for encryption and decryption.
     * <p>
     * Attempts to read a build-time property injected via Maven,
     * falling back to a default key if running locally within an IDE.
     *
     * @return A {@link SecretKeySpec} configured for AES encryption.
     * @throws RuntimeException if key decoding fails.
     */
    private static SecretKeySpec getSecretKey() {
        try {
            String buildKey = System.getProperty("save.encryption.key");
            if (buildKey == null || buildKey.isEmpty() || buildKey.startsWith("${")) {
                // Fallback key for local testing inside IntelliJ IDE
                buildKey = "JHkuRJ/KC7IXEye5BflQCLyGbdP32F9twRwEJn8WPlM=";
            }
            byte[] decodedKey = Base64.getDecoder().decode(buildKey);
            return new SecretKeySpec(decodedKey, ALGORITHM);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize AES encryption key: " + e.getMessage(), e);
        }
    }

    /**
     * Encrypts a plain-text JSON string using AES and encodes the result into a Base64 string.
     *
     * @param data The plain-text JSON string to encrypt.
     * @return The Base64-encoded encrypted ciphertext.
     * @throws Exception if encryption fails.
     */
    private static String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * Decodes a Base64-encoded ciphertext string and decrypts it back to plain-text JSON using AES.
     *
     * @param encryptedData The Base64-encoded encrypted string.
     * @return The decrypted plain-text JSON string.
     * @throws Exception if decryption fails.
     */
    private static String decrypt(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey());
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }


    /**
     * Data Transfer Object (DTO) representing the entire saved combat session state,
     * including active turn ownership and the collection of combatants.
     */
    private static class CombatSessionDTO {
        String activeCharacterName;
        List<CharacterRecordDTO> combatants = new ArrayList<>();
    }

    /**
     * Flat Data Transfer Object (DTO) representing individual character or NPC attributes
     * for serialization into JSON format.
     */
    private static class CharacterRecordDTO {
        String name;
        int initiative;
        int dexterity;
        String type;
        String deathStatus;
        int passSaves;
        int failSaves;
        boolean isNPC;
        int currentHealth;
        int maxHealth;
        String enemyType;
    }

    /**
     * Saves the current combat encounter state to the currently active file.
     * If no file is currently active (e.g. a new session), it falls back to saving with a default name.
     *
     * @param manager The active {@link CharacterManager} instance containing combatants.
     */
    public static void save(CharacterManager manager) {
        if (currentFileName == null || currentFileName.isEmpty()) {
            saveAs(manager, "save.json");
        } else {
            saveAs(manager, currentFileName);
        }
    }

    /**
     * Saves the current combat encounter state from a {@link CharacterManager} into an encrypted file.
     *
     * @param manager  The active {@link CharacterManager} instance containing combatants.
     * @param fileName The name of the target save file (the {@code .json} extension is appended automatically if omitted).
     */
    public static void saveAs(CharacterManager manager, String fileName) {
        if (!fileName.endsWith(".json")) {
            fileName += ".json";
        }
        currentFileName = fileName;
        File targetFile = new File(FileManager.getSaveDirectory(), fileName);
        try{
            saveToFile(manager, targetFile);
        } catch (IOException e) {
            System.err.println("Warning: Could not create save");
        }
    }

    /**
     * Loads a combat encounter state from an encrypted file into a {@link CharacterManager}.
     *
     * @param manager       The active {@link CharacterManager} instance to populate.
     * @param fileName      The name of the save file to load from.
     * @param clearExisting If {@code true}, clears existing combatants before loading; if {@code false}, merges records.
     */
    public static void load(CharacterManager manager, String fileName, boolean clearExisting) {
        if (!fileName.endsWith(".json")) {
            fileName += ".json";
        }
        currentFileName = fileName;
        File targetFile = new File(FileManager.getSaveDirectory(), fileName);
        try {
            loadFromFile(manager, targetFile, clearExisting);
        } catch (IOException e) {
            System.err.println("Warning: Could not load file");
        }
    }


    /**
     * Internal helper to serialize combat state into DTOs, encrypt the JSON payload, and write it to disk.
     *
     * @param manager The active {@link CharacterManager} instance.
     * @param file    The destination {@link File} target.
     * @throws IOException if writing or encryption fails.
     */
    private static void saveToFile(CharacterManager manager, File file) throws IOException {
        System.out.println("Saving " + file.getAbsolutePath());

        CombatSessionDTO session = new CombatSessionDTO();

        List<Character> activeOrder = manager.getInitiativeOrder();
        if (!activeOrder.isEmpty()) {
            session.activeCharacterName = activeOrder.getFirst().getCharacterData().get("Name").toString();
        }

        for (Character character : manager.getCharacterList()){
            Map<String, Object> data = character.getCharacterData();
            Map<String, Integer> saves = character.getDeathSaves();

            CharacterRecordDTO dto = new CharacterRecordDTO();
            dto.name = data.get("Name").toString();
            dto.initiative = (int) data.get("Initiative");
            dto.dexterity = (int) data.get("Dexterity");
            dto.type = data.get("Type").toString();
            dto.deathStatus = data.get("DeathStatus").toString();
            dto.passSaves = saves.getOrDefault("Pass", 0);
            dto.failSaves = saves.getOrDefault("Fail", 0);

            if (character instanceof NonePlayerCharacter npc) {
                dto.isNPC = true;
                dto.currentHealth = npc.getHealth();
                dto.maxHealth = npc.getMaxHealth();
                dto.enemyType = npc.getEnemyType();
            } else {
                dto.isNPC = false;
                dto.enemyType = "";
            }

            session.combatants.add(dto);
        }

        try {
            String jsonString = GSON.toJson(session);
            String encryptedString = encrypt(jsonString);
            Files.writeString(file.toPath(), encryptedString, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IOException("Failed to encrypt and save file", e);
        }
    }


    /**
     * Internal helper to read an encrypted save file from disk, decrypt it, parse the JSON,
     * and populate the {@link CharacterManager}.
     *
     * @param manager       The active {@link CharacterManager} instance to populate.
     * @param file          The source {@link File} to read from.
     * @param clearExisting If {@code true}, clears existing combatants prior to loading.
     * @throws IOException if reading or decryption fails.
     */
    private static void loadFromFile(CharacterManager manager, File file, boolean clearExisting) throws IOException {
        try {
            String encryptedString = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            String jsonString = decrypt(encryptedString);
            CombatSessionDTO session = GSON.fromJson(jsonString, CombatSessionDTO.class);
            if (session == null || session.combatants == null) {
                System.out.println("Nothing to load");
                return;
            }
            if (clearExisting) { manager.clearCharacters(); }

            List<String> existingNames = manager.getCharacterList().stream()
                    .map(c -> c.getCharacterData().get("Name").toString().toLowerCase())
                    .toList();

            for (CharacterRecordDTO dto : session.combatants) {
                if (existingNames.contains(dto.name.toLowerCase(Locale.ROOT))){
                    continue;
                }

                Character character = getCharacter(dto);
                manager.addCharacter(character);
            }
            if (clearExisting && session.activeCharacterName != null) {
                manager.setCurrentCharacterByName(session.activeCharacterName);
            }

        } catch (Exception e) {
            throw new IOException("Failed to decrypt and load file", e);
        }
    }

    /**
     * Reconstructs a full {@link Character} or {@link NonePlayerCharacter} instance from a loaded record DTO.
     *
     * @param dto The {@link CharacterRecordDTO} containing serialized attributes.
     * @return The instantiated and populated {@link Character} object.
     */
    private static Character getCharacter(CharacterRecordDTO dto) {
        Character character;
        if (dto.isNPC){
            NonePlayerCharacter npc = new NonePlayerCharacter(
                    dto.name,
                    dto.initiative,
                    dto.dexterity,
                    dto.maxHealth,
                    dto.type,
                    dto.deathStatus
            );
            npc.setCurrentHealth(dto.currentHealth);
            npc.editDeathStatus(dto.deathStatus); // Preserves DOWN vs ALIVE/DEAD status
            npc.editEnemyType(dto.enemyType != null ? dto.enemyType : "");
            character = npc;
        } else {
            character = new Character(
                    dto.name,
                    dto.initiative,
                    dto.dexterity,
                    dto.type,
                    dto.deathStatus
            );
        }
        character.setDeathSaves(dto.passSaves, dto.failSaves);

        return character;
    }
}