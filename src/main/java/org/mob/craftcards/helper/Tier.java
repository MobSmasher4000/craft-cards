package org.mob.craftcards.helper;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.mob.craftcards.CraftCardsConfig;

import java.util.function.Supplier;

public enum Tier {
    T0("tier0", () -> (float) CraftCardsConfig.INSTANCE.baseTierValues.tier0_base),
    T1("tier1", () -> (float) CraftCardsConfig.INSTANCE.baseTierValues.tier1_base),
    T2("tier2", () -> (float) CraftCardsConfig.INSTANCE.baseTierValues.tier2_base),
    T3("tier3", () -> (float) CraftCardsConfig.INSTANCE.baseTierValues.tier3_base),
    T4("tier4", () -> (float) CraftCardsConfig.INSTANCE.baseTierValues.tier4_base),
    T5("tier5", () -> (float) CraftCardsConfig.INSTANCE.baseTierValues.tier5_base),
    T6("tier6", () -> (float) CraftCardsConfig.INSTANCE.baseTierValues.tier6_base);

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
        // BuiltInRegistries.ITEM.getKey() -> Registries.ITEM.getId()
        Identifier id = Registries.ITEM.getId(item);
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