package org.mob.craftcards.helper;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public enum Tier {
    T0("tier0", 0.02f),
    T1("tier1", 0.05f),
    T2("tier2", 0.10f),
    T3("tier3", 0.20f),
    T4("tier4", 0.50f),
    T5("tier5", 1.00f),
    T6("tier6", 1.50f);

    private final String textureName;
    private final float bonus; // percentage as decimal

    Tier(String textureName, float bonus) {
        this.textureName = textureName;
        this.bonus = bonus;
    }

    public String getTextureName() {
        return textureName;
    }

    public String getIdSuffix() {
        return "_" + textureName;
    }

    /**
     * Returns the bonus multiplier:
     * e.g. Tier 3 → 0.20f
     */
    public float getBonus() {
        return bonus;
    }

    public static Tier fromItem(Item item) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
        String path = id.getPath(); // example: "armor_tier3"

        for (Tier tier : Tier.values()) {
            if (path.endsWith(tier.getIdSuffix())) { // "_tier3"
                return tier;
            }
        }
        return Tier.T0;
    }

    /**
     * Returns the bonus as a percentage value:
     * e.g. Tier 3 → 20.0f
     */
    public float getBonusPercent() {
        return bonus * 100f;
    }
}