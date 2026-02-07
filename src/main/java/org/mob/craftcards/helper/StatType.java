package org.mob.craftcards.helper;

public enum StatType {
    ARMOR("armor"),
    ARMOR_TOUGHNESS("armor_toughness"),
    ATTACK_SPEED("attack_speed"),
    BLOCK_INTERACTION_RANGE("block_interaction_range"),
    CAPTURE_RATE("capture_rate"),
    DAMAGE("damage"),
    ENTITY_INTERACTION_RANGE("entity_interaction_range"),
    FEATHER_FALLING("feather_falling"),
    FORTUNE("fortune"),
    HEALTH_BOOST("health_boost"),
    JUMP_BOOST("jump_boost"),
    LOOTING("looting"),
    LUCK_BOOST("luck_boost"),
    MINING_SPEED("mining_speed"),
    SHINY_RATE("shiny_rate"),
    SIZE_DOWN("size_down"),
    SIZE_UP("size_up"),
    SNEAK_SPEED("sneak_speed"),
    SPEED_BOOST("speed_boost"),
    STEP_HEIGHT("step_height"),
    UNDERWATER_MINING_SPEED("underwater_mining_speed");

    private final String baseName; // used in item id & texture folder

    StatType(String baseName) {
        this.baseName = baseName;
    }

    public String getBaseName() {
        return baseName;
    }
}
