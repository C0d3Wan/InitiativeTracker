package com.c0d3wan.initiativetracker.filemanagement.bestiary.data;

import java.util.List;
import java.util.Map;

public class BestiaryDTO {
    private List<Monster> monsters;

    public List<Monster> getMonsters() {
        return monsters;
    }

    public void setMonsters(List<Monster> monsters) {
        this.monsters = monsters;
    }

    public static class Monster {
        private String name;
        private String groupName;
        private List<String> subgroups;
        private List<String> tags;
        private String type;
        private String size;
        private String alignment;
        private int armorClass;
        private HitPoints hitPoints;
        private String challengeRating;
        private int experiencePoints;
        private boolean isOfficial = true;

        private Map<String, Integer> speed;
        private List<String> senses;
        private List<String> languages;

        private Map<String, Integer> stats;
        private Map<String, Integer> skills;
        private Map<String, Integer> savingThrows;

        private List<String> damageResistances;
        private List<String> damageVulnerabilities;
        private List<String> damageImmunities;
        private List<String> conditionImmunities;

        private List<Feature> traits;
        private List<Feature> actions;
        private List<Feature> reactions;

        private ActionGroup legendaryActions;
        private ActionGroup lairActions;

        // Nested Support Classes
        public static class HitPoints {
            private int average;
            private String formula;

            public int getAverage() { return average; }
            public void setAverage(int average) { this.average = average; }

            public String getFormula() { return formula; }
            public void setFormula(String formula) { this.formula = formula; }
        }

        public static class Feature {
            private String name;
            private String description;

            public String getName() { return name; }
            public void setName(String name) { this.name = name; }

            public String getDescription() { return description; }
            public void setDescription(String description) { this.description = description; }
        }

        public static class ActionGroup {
            private String description;
            private List<Feature> actions;

            public String getDescription() { return description; }
            public void setDescription(String description) { this.description = description; }

            public List<Feature> getActions() { return actions; }
            public void setActions(List<Feature> actions) { this.actions = actions; }
        }

        // Standard Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getGroupName() { return groupName; }
        public void setGroupName(String groupName) { this.groupName = groupName; }

        public List<String> getSubgroups() { return subgroups; }
        public void setSubgroups(List<String> subgroups) { this.subgroups = subgroups; }

        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }

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

        public String getChallengeRating() { return challengeRating; }
        public void setChallengeRating(String challengeRating) { this.challengeRating = challengeRating; }

        public int getExperiencePoints() { return experiencePoints; }
        public void setExperiencePoints(int experiencePoints) { this.experiencePoints = experiencePoints; }

        public boolean isOfficial() { return isOfficial; }
        public void setOfficial(boolean official) { isOfficial = official; }

        public Map<String, Integer> getSpeed() { return speed; }
        public void setSpeed(Map<String, Integer> speed) { this.speed = speed; }

        public List<String> getSenses() { return senses; }
        public void setSenses(List<String> senses) { this.senses = senses; }

        public List<String> getLanguages() { return languages; }
        public void setLanguages(List<String> languages) { this.languages = languages; }

        public Map<String, Integer> getStats() { return stats; }
        public void setStats(Map<String, Integer> stats) { this.stats = stats; }

        public Map<String, Integer> getSkills() { return skills; }
        public void setSkills(Map<String, Integer> skills) { this.skills = skills; }

        public Map<String, Integer> getSavingThrows() { return savingThrows; }
        public void setSavingThrows(Map<String, Integer> savingThrows) { this.savingThrows = savingThrows; }

        public List<String> getDamageResistances() { return damageResistances; }
        public void setDamageResistances(List<String> damageResistances) { this.damageResistances = damageResistances; }

        public List<String> getDamageVulnerabilities() { return damageVulnerabilities; }
        public void setDamageVulnerabilities(List<String> damageVulnerabilities) { this.damageVulnerabilities = damageVulnerabilities; }

        public List<String> getDamageImmunities() { return damageImmunities; }
        public void setDamageImmunities(List<String> damageImmunities) { this.damageImmunities = damageImmunities; }

        public List<String> getConditionImmunities() { return conditionImmunities; }
        public void setConditionImmunities(List<String> conditionImmunities) { this.conditionImmunities = conditionImmunities; }

        public List<Feature> getTraits() { return traits; }
        public void setTraits(List<Feature> traits) { this.traits = traits; }

        public List<Feature> getActions() { return actions; }
        public void setActions(List<Feature> actions) { this.actions = actions; }

        public List<Feature> getReactions() { return reactions; }
        public void setReactions(List<Feature> reactions) { this.reactions = reactions; }

        public ActionGroup getLegendaryActions() { return legendaryActions; }
        public void setLegendaryActions(ActionGroup legendaryActions) { this.legendaryActions = legendaryActions; }

        public ActionGroup getLairActions() { return lairActions; }
        public void setLairActions(ActionGroup lairActions) { this.lairActions = lairActions; }
    }
}