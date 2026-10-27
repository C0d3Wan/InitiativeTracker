package com.c0d3wan.initiativetracker.FileManagement;

import com.c0d3wan.initiativetracker.characters.*;
import com.c0d3wan.initiativetracker.characters.Character;
import com.google.gson.*;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

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

    private static String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    private static String decrypt(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey());
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    private static class CombatSessionDTO {
        String activeCharacterName;
        List<CharacterRecordDTO> combatants = new ArrayList<>();
    }

    private static class CharacterRecordDTO {
        String name;
        int initiative;
        int dexterity;
        String type;
        String deathStatus;
        int passSaves;
        int failSaves;
        boolean isNPC;
        boolean isHorde;
        List<NPCRecordDTO> npcs = new ArrayList<>();
        int currentHealth;
        int maxHealth;
        String enemyType;
    }

    private static class NPCRecordDTO {
        String name;
        int initiative;
        int dexterity;
        String type;
        String deathStatus;

        int currentHealth;
        int maxHealth;
        String enemyType;
    }

    private static NPCRecordDTO toNPCDTO(NonePlayerCharacter npc) {
        NPCRecordDTO dto = new NPCRecordDTO();

        CharacterData data = npc.getCharacterData();

        dto.name = data.name();
        dto.initiative = data.initiative();
        dto.dexterity = data.dexterity();
        dto.type = data.type().name();
        dto.deathStatus = data.deathStatus().name();

        dto.currentHealth = npc.getHealth();
        dto.maxHealth = npc.getMaxHealth();
        dto.enemyType = npc.getEnemyType();

        return dto;
    }

    private static NonePlayerCharacter fromNPCDTO(NPCRecordDTO dto) {
        NonePlayerCharacter npc =
                new NonePlayerCharacter(
                        dto.name,
                        dto.initiative,
                        dto.dexterity,
                        dto.maxHealth,
                        dto.type,
                        dto.deathStatus
                );

        npc.setCurrentHealth(dto.currentHealth, false);
        npc.setDeathStatus(dto.deathStatus);
        npc.setEnemyType(dto.enemyType != null ? dto.enemyType : "");
        return npc;
    }

    public static void save(CharacterManager manager) {
        if (currentFileName == null || currentFileName.isEmpty()) {
            saveAs(manager, "save.json");
        } else {
            saveAs(manager, currentFileName);
        }
    }

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

    private static void saveToFile(CharacterManager manager, File file) throws IOException {
        System.out.println("Saving " + file.getAbsolutePath());

        CombatSessionDTO session = new CombatSessionDTO();

        List<Character> activeOrder = manager.getInitiativeOrder();
        if (!activeOrder.isEmpty()) {
            session.activeCharacterName = activeOrder.getFirst().getCharacterData().name();
        }

        for (Character character : manager.getCharacterList()){
            CharacterData data = character.getCharacterData();
            Map<String, Integer> saves = character.getDeathSaves();

            CharacterRecordDTO dto = new CharacterRecordDTO();
            dto.name = data.name();
            dto.initiative = data.initiative();
            dto.dexterity = data.dexterity();
            dto.type = data.type().name();
            dto.deathStatus = data.deathStatus().name();

            dto.passSaves = saves.getOrDefault("Pass", 0);
            dto.failSaves = saves.getOrDefault("Fail", 0);

            if (character instanceof NonePlayerCharacter npc) {
                dto.isNPC = true;
                dto.currentHealth = npc.getHealth();
                dto.maxHealth = npc.getMaxHealth();
                dto.enemyType = npc.getEnemyType();
                if (character instanceof Horde horde){
                    dto.isHorde = true;
                    for (NonePlayerCharacter member: horde.getNpcs()){
                        dto.npcs.add(toNPCDTO(member));
                    }
                }
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
            System.out.println("Saved " + file.getAbsolutePath());
        } catch (Exception e) {
            throw new IOException("Failed to encrypt and save file", e);
        }
    }

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

            Set<String> existingNames = manager.getCharacterList().stream()
                    .map(c -> c.getCharacterData().name().toLowerCase())
                    .collect(Collectors.toSet());;

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
            throw new IOException("Failed to load file", e);
        }
    }

    private static Character getCharacter(CharacterRecordDTO dto) {
        Character character;
        if (dto.isNPC){
            NonePlayerCharacter npc;
            if (dto.isHorde){
                npc = new Horde(
                        dto.name,
                        dto.initiative,
                        dto.dexterity,
                        dto.maxHealth,
                        dto.deathStatus,
                        dto.maxHealth
                );
                npc.setCurrentHealth(dto.currentHealth, false);

                List<NonePlayerCharacter> members = new ArrayList<>();

                for (NPCRecordDTO memberDTO: dto.npcs){
                    members.add(fromNPCDTO(memberDTO));
                }
                ((Horde) npc).editNPCs(members);
                ((Horde) npc).relinkNPCs();
                ((Horde) npc).checkDead();

            }else {
                npc = new NonePlayerCharacter(
                        dto.name,
                        dto.initiative,
                        dto.dexterity,
                        dto.maxHealth,
                        dto.type,
                        dto.deathStatus
                );
                npc.setCurrentHealth(dto.currentHealth, false);
                npc.setDeathStatus(dto.deathStatus);
                npc.setEnemyType(dto.enemyType != null ? dto.enemyType : "");
            }
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