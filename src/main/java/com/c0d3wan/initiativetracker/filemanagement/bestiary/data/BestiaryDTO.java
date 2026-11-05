package com.c0d3wan.initiativetracker.filemanagement.bestiary.data;

import java.util.List;
import java.util.Map;

public class BestiaryDTO {
    private String version;
    private List<Monster> monsters;

    public BestiaryDTO() {}

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public List<Monster> getMonsters() { return monsters; }
    public void setMonsters(List<Monster> monsters) { this.monsters = monsters; }

    /**
     * Represents an individual D&D 5e monster record.
     */
    public static class Monster {
        private String name;
        private String group_name;
        private String type;
        private String size;
        private String alignment;
        private int armorClass;

        private HitPoints hitPoints;
        private Map<String, Integer> speed;
        private Map<String, Integer> stats;

        private String challengeRating;
        private int experiencePoints;

        private Map<String, Integer> savingThrows;
        private Map<String, Integer> skills;

        private List<String> damageResistances;
        private List<String> damageVulnerabilities;
        private List<String> damageImmunities;
        private List<String> conditionImmunities;
        private List<String> senses;
        private List<String> languages;

        private List<Feature> traits;
        private List<Feature> actions;
        private ActionGroup legendaryActions;
        private ActionGroup lairActions;

        public Monster() {}


        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getGroupName() { return group_name; }
        public void setGroup(String group) { this.group_name = group; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getSize() { return size; }
        public void setSize(String size) { this.size = size; }

        public String getAlignment() { return alignment; }
        public void setAlignment(String alignment) { this.alignment = alignment; }

        public int getArmorClass() { return armorClass; }
        public void setArmorClass(int armorClass) { this.armorClass = armorClass; }

        public HitPoints getHitPoints() { return hitPoints; }
        public void setHitPoints(HitPoints hitPoints) { this.hitPoints = hitPoints; }

        public Map<String, Integer> getSpeed() { return speed; }
        public void setSpeed(Map<String, Integer> speed) { this.speed = speed; }

        public Map<String, Integer> getStats() { return stats; }
        public void setStats(Map<String, Integer> stats) { this.stats = stats; }

        public String getChallengeRating() { return challengeRating; }
        public void setChallengeRating(String challengeRating) { this.challengeRating = challengeRating; }

        public int getExperiencePoints() { return experiencePoints; }
        public void setExperiencePoints(int experiencePoints) { this.experiencePoints = experiencePoints; }

        public Map<String, Integer> getSavingThrows() { return savingThrows; }
        public void setSavingThrows(Map<String, Integer> savingThrows) { this.savingThrows = savingThrows; }

        public Map<String, Integer> getSkills() { return skills; }
        public void setSkills(Map<String, Integer> skills) { this.skills = skills; }

        public List<String> getDamageResistances() { return damageResistances; }
        public void setDamageResistances(List<String> damageResistances) { this.damageResistances = damageResistances; }

        public List<String> getDamageVulnerabilities() { return damageVulnerabilities; }
        public void setDamageVulnerabilities(List<String> damageVulnerabilities) { this.damageVulnerabilities = damageVulnerabilities; }

        public List<String> getDamageImmunities() { return damageImmunities; }
        public void setDamageImmunities(List<String> damageImmunities) { this.damageImmunities = damageImmunities; }

        public List<String> getConditionImmunities() { return conditionImmunities; }
        public void setConditionImmunities(List<String> conditionImmunities) { this.conditionImmunities = conditionImmunities; }

        public List<String> getSenses() { return senses; }
        public void setSenses(List<String> senses) { this.senses = senses; }

        public List<String> getLanguages() { return languages; }
        public void setLanguages(List<String> languages) { this.languages = languages; }

        public List<Feature> getTraits() { return traits; }
        public void setTraits(List<Feature> traits) { this.traits = traits; }

        public List<Feature> getActions() { return actions; }
        public void setActions(List<Feature> actions) { this.actions = actions; }

        public ActionGroup getLegendaryActions() { return legendaryActions; }
        public void setLegendaryActions(ActionGroup legendaryActions) { this.legendaryActions = legendaryActions; }

        public ActionGroup getLairActions() { return lairActions; }
        public void setLairActions(ActionGroup lairActions) { this.lairActions = lairActions; }
    }

    public static class HitPoints {
        private int average;
        private String formula;

        public HitPoints() {}

        public int getAverage() { return average; }
        public void setAverage(int average) { this.average = average; }

        public String getFormula() { return formula; }
        public void setFormula(String formula) { this.formula = formula; }
    }

    public static class Feature {
        private String name;
        private String description;

        public Feature() {}

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class ActionGroup {
        private String description;
        private List<Feature> actions;

        public ActionGroup() {}

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public List<Feature> getActions() { return actions; }
        public void setActions(List<Feature> actions) { this.actions = actions; }
    }
}
