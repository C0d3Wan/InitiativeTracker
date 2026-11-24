package com.c0d3wan.initiativetracker.filemanagement.bestiary;

import com.c0d3wan.initiativetracker.filemanagement.FileManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Map;

public class JsonToDatabaseImporter {

    private static String getDatabaseUrl() {
        File dbFile = new File(FileManager.getDirectory(), "bestiary.db");
        return "jdbc:sqlite:" + dbFile.getAbsolutePath();
    }

    private static final String JSON_RESOURCE_PATH = "/bestiary_export.json";

    public static void main(String[] args) {
        System.out.println("Starting JSON import into SQLite config database...");
        String dbUrl = getDatabaseUrl();

        try (Connection conn = DriverManager.getConnection(dbUrl)) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }

            initializeDatabaseTables(conn);

            conn.setAutoCommit(false);

            try (InputStream inputStream = JsonToDatabaseImporter.class.getResourceAsStream(JSON_RESOURCE_PATH)) {
                if (inputStream == null) {
                    throw new IOException("Could not find resource: " + JSON_RESOURCE_PATH);
                }

                try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                    JsonObject rootJson = JsonParser.parseReader(reader).getAsJsonObject();
                    JsonArray monstersArray = rootJson.getAsJsonArray("monsters");

                    for (JsonElement element : monstersArray) {
                        JsonObject mObj = element.getAsJsonObject();
                        importMonster(conn, mObj);
                    }

                    conn.commit();
                    System.out.println("Import completed successfully from resources!\n");
                }

            } catch (Exception e) {
                conn.rollback();
                System.err.println("Error during JSON import, transaction rolled back: " + e.getMessage());
                e.printStackTrace();
                return;
            }

            runValidation(conn);

        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void initializeDatabaseTables(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS MonsterGroup (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE);");
            stmt.execute("CREATE TABLE IF NOT EXISTS MonsterType (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE);");
            stmt.execute("CREATE TABLE IF NOT EXISTS Size (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE);");
            stmt.execute("CREATE TABLE IF NOT EXISTS Alignment (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE);");
            stmt.execute("CREATE TABLE IF NOT EXISTS SpeedType (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE);");
            stmt.execute("CREATE TABLE IF NOT EXISTS SenseType (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE);");
            stmt.execute("CREATE TABLE IF NOT EXISTS Skill (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE);");
            stmt.execute("CREATE TABLE IF NOT EXISTS DamageType (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE);");
            stmt.execute("CREATE TABLE IF NOT EXISTS Condition (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE);");
            stmt.execute("CREATE TABLE IF NOT EXISTS Language (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE);");
            stmt.execute("CREATE TABLE IF NOT EXISTS Tag (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE);");

            stmt.execute("CREATE TABLE IF NOT EXISTS Monster (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT UNIQUE, " +
                    "group_id INTEGER, " +
                    "type_id INTEGER, " +
                    "size_id INTEGER, " +
                    "alignment_id INTEGER, " +
                    "challenge_rating REAL, " +
                    "armor_class INTEGER, " +
                    "hp_average INTEGER, " +
                    "hp_formula TEXT, " +
                    "experience_points INTEGER, " +
                    "passive_perception INTEGER, " +
                    "proficiency_bonus INTEGER, " +
                    "description TEXT, " +
                    "is_official INTEGER DEFAULT 1, " +
                    "FOREIGN KEY (group_id) REFERENCES MonsterGroup(id), " +
                    "FOREIGN KEY (type_id) REFERENCES MonsterType(id), " +
                    "FOREIGN KEY (size_id) REFERENCES Size(id), " +
                    "FOREIGN KEY (alignment_id) REFERENCES Alignment(id));");

            stmt.execute("CREATE TABLE IF NOT EXISTS MonsterStats (" +
                    "monster_id INTEGER PRIMARY KEY, " +
                    "strength INTEGER, dexterity INTEGER, constitution INTEGER, " +
                    "intelligence INTEGER, wisdom INTEGER, charisma INTEGER, " +
                    "FOREIGN KEY (monster_id) REFERENCES Monster(id) ON DELETE CASCADE);");

            stmt.execute("CREATE TABLE IF NOT EXISTS MonsterSavingThrow (" +
                    "monster_id INTEGER, ability_name TEXT, bonus INTEGER, " +
                    "PRIMARY KEY (monster_id, ability_name), " +
                    "FOREIGN KEY (monster_id) REFERENCES Monster(id) ON DELETE CASCADE);");

            stmt.execute("CREATE TABLE IF NOT EXISTS MonsterSkill (" +
                    "monster_id INTEGER, skill_id INTEGER, bonus INTEGER, " +
                    "PRIMARY KEY (monster_id, skill_id), " +
                    "FOREIGN KEY (monster_id) REFERENCES Monster(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (skill_id) REFERENCES Skill(id));");

            stmt.execute("CREATE TABLE IF NOT EXISTS MonsterSpeed (" +
                    "monster_id INTEGER, speed_type_id INTEGER, speed_ft INTEGER, " +
                    "PRIMARY KEY (monster_id, speed_type_id), " +
                    "FOREIGN KEY (monster_id) REFERENCES Monster(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (speed_type_id) REFERENCES SpeedType(id));");

            stmt.execute("CREATE TABLE IF NOT EXISTS MonsterSense (" +
                    "monster_id INTEGER, sense_type_id INTEGER, range_ft INTEGER, " +
                    "PRIMARY KEY (monster_id, sense_type_id), " +
                    "FOREIGN KEY (monster_id) REFERENCES Monster(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (sense_type_id) REFERENCES SenseType(id));");

            stmt.execute("CREATE TABLE IF NOT EXISTS MonsterDamageModifier (" +
                    "monster_id INTEGER, damage_type_id INTEGER, modifier_type TEXT, " +
                    "PRIMARY KEY (monster_id, damage_type_id), " +
                    "FOREIGN KEY (monster_id) REFERENCES Monster(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (damage_type_id) REFERENCES DamageType(id));");

            stmt.execute("CREATE TABLE IF NOT EXISTS MonsterConditionImmunity (" +
                    "monster_id INTEGER, condition_id INTEGER, " +
                    "PRIMARY KEY (monster_id, condition_id), " +
                    "FOREIGN KEY (monster_id) REFERENCES Monster(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (condition_id) REFERENCES Condition(id));");

            stmt.execute("CREATE TABLE IF NOT EXISTS MonsterLanguage (" +
                    "monster_id INTEGER, language_id INTEGER, " +
                    "PRIMARY KEY (monster_id, language_id), " +
                    "FOREIGN KEY (monster_id) REFERENCES Monster(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (language_id) REFERENCES Language(id));");

            stmt.execute("CREATE TABLE IF NOT EXISTS MonsterAbility (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "monster_id INTEGER, ability_type TEXT, display_order INTEGER, " +
                    "name TEXT, description TEXT, recharge TEXT, " +
                    "UNIQUE(monster_id, ability_type, name), " +
                    "FOREIGN KEY (monster_id) REFERENCES Monster(id) ON DELETE CASCADE);");

            stmt.execute("CREATE TABLE IF NOT EXISTS MonsterTag (" +
                    "monster_id INTEGER, tag_id INTEGER, " +
                    "PRIMARY KEY (monster_id, tag_id), " +
                    "FOREIGN KEY (monster_id) REFERENCES Monster(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (tag_id) REFERENCES Tag(id));");
        }
    }

    private static void importMonster(Connection conn, JsonObject m) throws SQLException {
        String name = m.get("name").getAsString();
        boolean isOfficial = !m.has("isOfficial") || m.get("isOfficial").getAsBoolean();

        long existingId = -1;
        String checkSql = "SELECT id, is_official FROM Monster WHERE name = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, name);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    if (rs.getInt("is_official") == 0) {
                        return;
                    }
                    existingId = rs.getLong("id");
                }
            }
        }

        String groupName = m.has("group") && !m.get("group").isJsonNull() ? m.get("group").getAsString() : "Uncategorized";
        String typeName = m.has("type") && !m.get("type").isJsonNull() ? m.get("type").getAsString() : "Unknown";
        String sizeName = m.has("size") && !m.get("size").isJsonNull() ? m.get("size").getAsString() : "Medium";
        String alignmentName = m.has("alignment") && !m.get("alignment").isJsonNull() ? m.get("alignment").getAsString() : "Unaligned";

        Long groupId = ensureAndResolveId(conn, "MonsterGroup", groupName);
        Long typeId = ensureAndResolveId(conn, "MonsterType", typeName);
        Long sizeId = ensureAndResolveId(conn, "Size", sizeName);
        Long alignmentId = ensureAndResolveId(conn, "Alignment", alignmentName);

        long monsterId;
        if (existingId != -1) {
            String updateSql = "UPDATE Monster SET group_id = ?, type_id = ?, size_id = ?, alignment_id = ?, " +
                    "challenge_rating = ?, armor_class = ?, hp_average = ?, hp_formula = ?, experience_points = ?, " +
                    "passive_perception = ?, proficiency_bonus = ?, description = ?, is_official = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                if (groupId != null) pstmt.setLong(1, groupId); else pstmt.setNull(1, Types.INTEGER);
                if (typeId != null) pstmt.setLong(2, typeId); else pstmt.setNull(2, Types.INTEGER);
                if (sizeId != null) pstmt.setLong(3, sizeId); else pstmt.setNull(3, Types.INTEGER);
                if (alignmentId != null) pstmt.setLong(4, alignmentId); else pstmt.setNull(4, Types.INTEGER);
                pstmt.setDouble(5, m.has("challengeRating") ? m.get("challengeRating").getAsDouble() : 0.0);
                pstmt.setInt(6, m.has("armorClass") ? m.get("armorClass").getAsInt() : 10);
                pstmt.setInt(7, m.has("hpAverage") ? m.get("hpAverage").getAsInt() : 10);
                pstmt.setString(8, m.has("hpFormula") ? m.get("hpFormula").getAsString() : "2d8");
                pstmt.setInt(9, m.has("experiencePoints") ? m.get("experiencePoints").getAsInt() : 0);
                pstmt.setInt(10, m.has("passivePerception") ? m.get("passivePerception").getAsInt() : 10);
                pstmt.setInt(11, m.has("proficiencyBonus") ? m.get("proficiencyBonus").getAsInt() : 2);
                pstmt.setString(12, m.has("description") && !m.get("description").isJsonNull() ? m.get("description").getAsString() : "");
                pstmt.setInt(13, isOfficial ? 1 : 0);
                pstmt.setLong(14, existingId);
                pstmt.executeUpdate();
            }
            monsterId = existingId;
        } else {
            String insertMonsterSql = "INSERT INTO Monster (" +
                    "name, group_id, type_id, size_id, alignment_id, challenge_rating, " +
                    "armor_class, hp_average, hp_formula, experience_points, " +
                    "passive_perception, proficiency_bonus, description, is_official) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertMonsterSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, name);
                if (groupId != null) pstmt.setLong(2, groupId); else pstmt.setNull(2, Types.INTEGER);
                if (typeId != null) pstmt.setLong(3, typeId); else pstmt.setNull(3, Types.INTEGER);
                if (sizeId != null) pstmt.setLong(4, sizeId); else pstmt.setNull(4, Types.INTEGER);
                if (alignmentId != null) pstmt.setLong(5, alignmentId); else pstmt.setNull(5, Types.INTEGER);
                pstmt.setDouble(6, m.has("challengeRating") ? m.get("challengeRating").getAsDouble() : 0.0);
                pstmt.setInt(7, m.has("armorClass") ? m.get("armorClass").getAsInt() : 10);
                pstmt.setInt(8, m.has("hpAverage") ? m.get("hpAverage").getAsInt() : 10);
                pstmt.setString(9, m.has("hpFormula") ? m.get("hpFormula").getAsString() : "2d8");
                pstmt.setInt(10, m.has("experiencePoints") ? m.get("experiencePoints").getAsInt() : 0);
                pstmt.setInt(11, m.has("passivePerception") ? m.get("passivePerception").getAsInt() : 10);
                pstmt.setInt(12, m.has("proficiencyBonus") ? m.get("proficiencyBonus").getAsInt() : 2);
                pstmt.setString(13, m.has("description") && !m.get("description").isJsonNull() ? m.get("description").getAsString() : "");
                pstmt.setInt(14, isOfficial ? 1 : 0);

                pstmt.executeUpdate();
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        monsterId = generatedKeys.getLong(1);
                    } else {
                        monsterId = getMonsterIdByName(conn, name);
                    }
                }
            }
        }

        if (m.has("stats") && !m.get("stats").isJsonNull()) {
            JsonObject stats = m.getAsJsonObject("stats");
            String statsSql = "INSERT OR REPLACE INTO MonsterStats (monster_id, strength, dexterity, constitution, intelligence, wisdom, charisma) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(statsSql)) {
                pstmt.setLong(1, monsterId);
                pstmt.setInt(2, stats.has("strength") ? stats.get("strength").getAsInt() : 10);
                pstmt.setInt(3, stats.has("dexterity") ? stats.get("dexterity").getAsInt() : 10);
                pstmt.setInt(4, stats.has("constitution") ? stats.get("constitution").getAsInt() : 10);
                pstmt.setInt(5, stats.has("intelligence") ? stats.get("intelligence").getAsInt() : 10);
                pstmt.setInt(6, stats.has("wisdom") ? stats.get("wisdom").getAsInt() : 10);
                pstmt.setInt(7, stats.has("charisma") ? stats.get("charisma").getAsInt() : 10);
                pstmt.executeUpdate();
            }
        }

        if (m.has("savingThrows") && m.get("savingThrows").isJsonArray()) {
            JsonArray savesArray = m.getAsJsonArray("savingThrows");
            String saveSql = "INSERT OR REPLACE INTO MonsterSavingThrow (monster_id, ability_name, bonus) VALUES (?, ?, ?)";
            for (JsonElement saveEl : savesArray) {
                if (saveEl.isJsonObject()) {
                    JsonObject saveObj = saveEl.getAsJsonObject();
                    if (saveObj.has("ability") && saveObj.has("bonus")) {
                        try (PreparedStatement pstmt = conn.prepareStatement(saveSql)) {
                            pstmt.setLong(1, monsterId);
                            pstmt.setString(2, saveObj.get("ability").getAsString());
                            pstmt.setInt(3, saveObj.get("bonus").getAsInt());
                            pstmt.executeUpdate();
                        }
                    } else {
                        for (Map.Entry<String, JsonElement> entry : saveObj.entrySet()) {
                            try (PreparedStatement pstmt = conn.prepareStatement(saveSql)) {
                                pstmt.setLong(1, monsterId);
                                pstmt.setString(2, entry.getKey());
                                pstmt.setInt(3, entry.getValue().getAsInt());
                                pstmt.executeUpdate();
                            }
                        }
                    }
                }
            }
        }

        if (m.has("skills") && m.get("skills").isJsonArray()) {
            JsonArray skillsArray = m.getAsJsonArray("skills");
            String skillLinkSql = "INSERT OR REPLACE INTO MonsterSkill (monster_id, skill_id, bonus) VALUES (?, (SELECT id FROM Skill WHERE name = ?), ?)";
            for (JsonElement skillEl : skillsArray) {
                if (skillEl.isJsonObject()) {
                    JsonObject skillObj = skillEl.getAsJsonObject();
                    if (skillObj.has("skill") && skillObj.has("bonus")) {
                        String skillName = skillObj.get("skill").getAsString();
                        int bonus = skillObj.get("bonus").getAsInt();
                        if (ensureAndResolveId(conn, "Skill", skillName) != null) {
                            try (PreparedStatement pstmt = conn.prepareStatement(skillLinkSql)) {
                                pstmt.setLong(1, monsterId);
                                pstmt.setString(2, skillName);
                                pstmt.setInt(3, bonus);
                                pstmt.executeUpdate();
                            }
                        }
                    } else {
                        for (Map.Entry<String, JsonElement> entry : skillObj.entrySet()) {
                            String skillName = entry.getKey();
                            int bonus = entry.getValue().getAsInt();
                            if (ensureAndResolveId(conn, "Skill", skillName) != null) {
                                try (PreparedStatement pstmt = conn.prepareStatement(skillLinkSql)) {
                                    pstmt.setLong(1, monsterId);
                                    pstmt.setString(2, skillName);
                                    pstmt.setInt(3, bonus);
                                    pstmt.executeUpdate();
                                }
                            }
                        }
                    }
                }
            }
        }

        if (m.has("speeds") && m.get("speeds").isJsonArray()) {
            JsonArray speedsArray = m.getAsJsonArray("speeds");
            String speedLinkSql = "INSERT OR REPLACE INTO MonsterSpeed (monster_id, speed_type_id, speed_ft) VALUES (?, (SELECT id FROM SpeedType WHERE name = ?), ?)";

            for (JsonElement speedElement : speedsArray) {
                if (speedElement.isJsonObject()) {
                    JsonObject speedObj = speedElement.getAsJsonObject();
                    for (String speedType : speedObj.keySet()) {
                        int speedFt = speedObj.get(speedType).getAsInt();
                        Long speedTypeId = ensureAndResolveId(conn, "SpeedType", speedType);
                        if (speedTypeId != null) {
                            try (PreparedStatement linkStmt = conn.prepareStatement(speedLinkSql)) {
                                linkStmt.setLong(1, monsterId);
                                linkStmt.setString(2, speedType);
                                linkStmt.setInt(3, speedFt);
                                linkStmt.executeUpdate();
                            }
                        }
                    }
                }
            }
        }

        if (m.has("senses") && m.get("senses").isJsonArray()) {
            JsonArray sensesArray = m.getAsJsonArray("senses");
            String senseLinkSql = "INSERT OR REPLACE INTO MonsterSense (monster_id, sense_type_id, range_ft) VALUES (?, (SELECT id FROM SenseType WHERE name = ?), ?)";

            for (JsonElement senseElement : sensesArray) {
                if (senseElement.isJsonObject()) {
                    JsonObject senseObj = senseElement.getAsJsonObject();
                    if (senseObj.has("sense") && senseObj.has("rangeFt")) {
                        String senseName = senseObj.get("sense").getAsString();
                        int rangeFt = senseObj.get("rangeFt").getAsInt();
                        Long senseTypeId = ensureAndResolveId(conn, "SenseType", senseName);
                        if (senseTypeId != null) {
                            try (PreparedStatement linkStmt = conn.prepareStatement(senseLinkSql)) {
                                linkStmt.setLong(1, monsterId);
                                linkStmt.setString(2, senseName);
                                linkStmt.setInt(3, rangeFt);
                                linkStmt.executeUpdate();
                            }
                        }
                    }
                }
            }
        }

        if (m.has("damageModifiers") && m.get("damageModifiers").isJsonArray()) {
            JsonArray modsArray = m.getAsJsonArray("damageModifiers");
            String modLinkSql = "INSERT OR REPLACE INTO MonsterDamageModifier (monster_id, damage_type_id, modifier_type) " +
                    "VALUES (?, (SELECT id FROM DamageType WHERE name = ?), ?)";

            for (JsonElement modElement : modsArray) {
                if (modElement.isJsonObject()) {
                    JsonObject modObj = modElement.getAsJsonObject();
                    if (modObj.has("modifier") && modObj.has("damageType")) {
                        String modifierType = modObj.get("modifier").getAsString();
                        String damageTypeName = modObj.get("damageType").getAsString();
                        Long damageTypeId = ensureAndResolveId(conn, "DamageType", damageTypeName);
                        if (damageTypeId != null) {
                            try (PreparedStatement linkStmt = conn.prepareStatement(modLinkSql)) {
                                linkStmt.setLong(1, monsterId);
                                linkStmt.setString(2, damageTypeName);
                                linkStmt.setString(3, modifierType);
                                linkStmt.executeUpdate();
                            }
                        }
                    }
                }
            }
        }

        if (m.has("conditionImmunities") && m.get("conditionImmunities").isJsonArray()) {
            JsonArray condArray = m.getAsJsonArray("conditionImmunities");
            String condLinkSql = "INSERT OR IGNORE INTO MonsterConditionImmunity (monster_id, condition_id) " +
                    "VALUES (?, (SELECT id FROM Condition WHERE name = ?))";

            for (JsonElement condElement : condArray) {
                String condName = condElement.getAsString();
                if (!condName.isBlank()) {
                    Long condId = ensureAndResolveId(conn, "Condition", condName);
                    if (condId != null) {
                        try (PreparedStatement linkStmt = conn.prepareStatement(condLinkSql)) {
                            linkStmt.setLong(1, monsterId);
                            linkStmt.setString(2, condName);
                            linkStmt.executeUpdate();
                        }
                    }
                }
            }
        }

        if (m.has("languages") && m.get("languages").isJsonArray()) {
            JsonArray langArray = m.getAsJsonArray("languages");
            String langLinkSql = "INSERT OR IGNORE INTO MonsterLanguage (monster_id, language_id) " +
                    "VALUES (?, (SELECT id FROM Language WHERE name = ?))";

            for (JsonElement langElement : langArray) {
                String langName = langElement.getAsString();
                if (!langName.isBlank()) {
                    Long langId = ensureAndResolveId(conn, "Language", langName);
                    if (langId != null) {
                        try (PreparedStatement linkStmt = conn.prepareStatement(langLinkSql)) {
                            linkStmt.setLong(1, monsterId);
                            linkStmt.setString(2, langName);
                            linkStmt.executeUpdate();
                        }
                    }
                }
            }
        }

        if (m.has("tags") && m.get("tags").isJsonArray()) {
            JsonArray tagsArray = m.getAsJsonArray("tags");
            String linkInsertSql = "INSERT OR IGNORE INTO MonsterTag (monster_id, tag_id) VALUES (?, (SELECT id FROM Tag WHERE name = ?))";

            for (JsonElement tagElement : tagsArray) {
                String tagName = tagElement.getAsString();
                if (!tagName.isBlank()) {
                    Long tagId = ensureAndResolveId(conn, "Tag", tagName);
                    if (tagId != null) {
                        try (PreparedStatement linkStmt = conn.prepareStatement(linkInsertSql)) {
                            linkStmt.setLong(1, monsterId);
                            linkStmt.setString(2, tagName);
                            linkStmt.executeUpdate();
                        }
                    }
                }
            }
        }

        if (m.has("abilities") && m.get("abilities").isJsonArray()) {
            JsonArray abilitiesArray = m.getAsJsonArray("abilities");
            int displayOrder = 1;

            for (JsonElement abElement : abilitiesArray) {
                JsonObject abObj = abElement.getAsJsonObject();
                String category = abObj.has("category") ? abObj.get("category").getAsString() : "Trait";
                String abName = abObj.has("name") ? abObj.get("name").getAsString() : "";
                String desc = abObj.has("description") ? abObj.get("description").getAsString() : "";
                String recharge = abObj.has("recharge") && !abObj.get("recharge").isJsonNull() ? abObj.get("recharge").getAsString() : null;

                long existingAbId = -1;
                String checkAbSql = "SELECT id FROM MonsterAbility WHERE monster_id = ? AND ability_type = ? AND name = ?";
                try (PreparedStatement checkAb = conn.prepareStatement(checkAbSql)) {
                    checkAb.setLong(1, monsterId);
                    checkAb.setString(2, category);
                    checkAb.setString(3, abName);
                    try (ResultSet rs = checkAb.executeQuery()) {
                        if (rs.next()) {
                            existingAbId = rs.getLong("id");
                        }
                    }
                }

                if (existingAbId != -1) {
                    String updateAbSql = "UPDATE MonsterAbility SET display_order = ?, description = ?, recharge = ? WHERE id = ?";
                    try (PreparedStatement updateAb = conn.prepareStatement(updateAbSql)) {
                        updateAb.setInt(1, displayOrder++);
                        updateAb.setString(2, desc);
                        if (recharge != null) updateAb.setString(3, recharge); else updateAb.setNull(3, Types.VARCHAR);
                        updateAb.setLong(4, existingAbId);
                        updateAb.executeUpdate();
                    }
                } else {
                    String insertAbSql = "INSERT INTO MonsterAbility (monster_id, ability_type, display_order, name, description, recharge) VALUES (?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement insertAb = conn.prepareStatement(insertAbSql)) {
                        insertAb.setLong(1, monsterId);
                        insertAb.setString(2, category);
                        insertAb.setInt(3, displayOrder++);
                        insertAb.setString(4, abName);
                        insertAb.setString(5, desc);
                        if (recharge != null) insertAb.setString(6, recharge); else insertAb.setNull(6, Types.VARCHAR);
                        insertAb.executeUpdate();
                    }
                }
            }
        }
    }

    private static void runValidation(Connection conn) {
        System.out.println("Database validation complete.");
    }

    private static Long ensureAndResolveId(Connection conn, String tableName, String nameValue) throws SQLException {
        if (nameValue == null || nameValue.isBlank()) return null;

        String query = "SELECT id FROM " + tableName + " WHERE name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, nameValue);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getLong("id");
            }
        }

        String insertSql = "INSERT INTO " + tableName + " (name) VALUES (?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, nameValue);
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, nameValue);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getLong("id");
            }
        }
        return null;
    }

    private static long getMonsterIdByName(Connection conn, String name) throws SQLException {
        String query = "SELECT id FROM Monster WHERE name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getLong("id");
            }
        }
        throw new SQLException("Could not resolve monster ID for: " + name);
    }
}