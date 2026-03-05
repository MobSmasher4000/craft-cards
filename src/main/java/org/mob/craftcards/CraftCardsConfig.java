package org.mob.craftcards;

import net.neoforged.neoforge.common.ModConfigSpec;

public class CraftCardsConfig {

    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.DoubleValue TIER_0_BASE;
    public static final ModConfigSpec.DoubleValue TIER_1_BASE;
    public static final ModConfigSpec.DoubleValue TIER_2_BASE;
    public static final ModConfigSpec.DoubleValue TIER_3_BASE;
    public static final ModConfigSpec.DoubleValue TIER_4_BASE;
    public static final ModConfigSpec.DoubleValue TIER_5_BASE;
    public static final ModConfigSpec.DoubleValue TIER_6_BASE;

    public static final ModConfigSpec.DoubleValue TIER_0_GLOBAL;
    public static final ModConfigSpec.DoubleValue TIER_1_GLOBAL;
    public static final ModConfigSpec.DoubleValue TIER_2_GLOBAL;
    public static final ModConfigSpec.DoubleValue TIER_3_GLOBAL;
    public static final ModConfigSpec.DoubleValue TIER_4_GLOBAL;
    public static final ModConfigSpec.DoubleValue TIER_5_GLOBAL;
    public static final ModConfigSpec.DoubleValue TIER_6_GLOBAL;

    static {
        BUILDER.push("Base Tier Values");
        BUILDER.comment("Set the base bonus for an individual card of this tier.");

        TIER_0_BASE = BUILDER.defineInRange("tier0_base", 0.02, 0.0, 10.0);
        TIER_1_BASE = BUILDER.defineInRange("tier1_base", 0.05, 0.0, 10.0);
        TIER_2_BASE = BUILDER.defineInRange("tier2_base", 0.10, 0.0, 10.0);
        TIER_3_BASE = BUILDER.defineInRange("tier3_base", 0.20, 0.0, 10.0);
        TIER_4_BASE = BUILDER.defineInRange("tier4_base", 0.50, 0.0, 10.0);
        TIER_5_BASE = BUILDER.defineInRange("tier5_base", 1.00, 0.0, 10.0);
        TIER_6_BASE = BUILDER.defineInRange("tier6_base", 1.50, 0.0, 10.0);
        BUILDER.pop();

        BUILDER.push("Global Tier Bonuses");
        BUILDER.comment("Set the global bonus multiplier for having a full case of cards.");

        TIER_0_GLOBAL = BUILDER.defineInRange("tier0_global", 0.02, 0.0, 10.0);
        TIER_1_GLOBAL = BUILDER.defineInRange("tier1_global", 0.05, 0.0, 10.0);
        TIER_2_GLOBAL = BUILDER.defineInRange("tier2_global", 0.10, 0.0, 10.0);
        TIER_3_GLOBAL = BUILDER.defineInRange("tier3_global", 0.20, 0.0, 10.0);
        TIER_4_GLOBAL = BUILDER.defineInRange("tier4_global", 0.30, 0.0, 10.0);
        TIER_5_GLOBAL = BUILDER.defineInRange("tier5_global", 0.40, 0.0, 10.0);
        TIER_6_GLOBAL = BUILDER.defineInRange("tier6_global", 0.50, 0.0, 10.0);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}