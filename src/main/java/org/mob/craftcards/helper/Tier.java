package org.mob.craftcards.helper;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.mob.craftcards.CraftCardsConfig;

import java.util.function.Supplier;

public enum Tier {
    T0("tier0", () -> CraftCardsConfig.TIER_0_BASE.get().floatValue()),
    T1("tier1", () -> CraftCardsConfig.TIER_1_BASE.get().floatValue()),
    T2("tier2", () -> CraftCardsConfig.TIER_2_BASE.get().floatValue()),
    T3("tier3", () -> CraftCardsConfig.TIER_3_BASE.get().floatValue()),
    T4("tier4", () -> CraftCardsConfig.TIER_4_BASE.get().floatValue()),
    T5("tier5", () -> CraftCardsConfig.TIER_5_BASE.get().floatValue()),
    T6("tier6", () -> CraftCardsConfig.TIER_6_BASE.get().floatValue());

    private final String textureName;
    private final Supplier<Float> bonusSupplier;

    Tier(String textureName, Supplier<Float> bonusSupplier) {
        this.textureName = textureName;
        this.bonusSupplier = bonusSupplier;
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
        return bonusSupplier.get();
    }

    public static Tier tierFromItem(Item item) {
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
        return getBonus() * 100f; // Safely calls getBonus()
    }
}