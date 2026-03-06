package org.mob.craftcards;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.math.MathHelper;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CraftCardsConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("craftcards.json").toFile();

    // The single instance holding your active config values
    public static CraftCardsConfig INSTANCE = new CraftCardsConfig();

    // ==========================================
    // CONFIGURATION VALUES
    // ==========================================

    public BaseTierValues baseTierValues = new BaseTierValues();
    public GlobalTierBonuses globalTierBonuses = new GlobalTierBonuses();

    public static class BaseTierValues {
        // Set the base bonus for an individual card of this tier (0.0 - 10.0)
        public double tier0_base = 0.02;
        public double tier1_base = 0.05;
        public double tier2_base = 0.10;
        public double tier3_base = 0.20;
        public double tier4_base = 0.50;
        public double tier5_base = 1.00;
        public double tier6_base = 1.50;
    }

    public static class GlobalTierBonuses {
        // Set the global bonus multiplier for having a full case of cards (0.0 - 10.0)
        public double tier0_global = 0.02;
        public double tier1_global = 0.05;
        public double tier2_global = 0.10;
        public double tier3_global = 0.20;
        public double tier4_global = 0.30;
        public double tier5_global = 0.40;
        public double tier6_global = 0.50;
    }

    // ==========================================
    // LOAD & SAVE LOGIC
    // ==========================================

    public static void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                INSTANCE = GSON.fromJson(reader, CraftCardsConfig.class);
                validateRanges(); // Ensure users didn't enter values outside 0.0 - 10.0
            } catch (IOException e) {
                System.err.println("Failed to load Craft Cards config! Using defaults.");
                e.printStackTrace();
            }
        } else {
            save(); // Create the file with default values if it doesn't exist
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(INSTANCE, writer);
        } catch (IOException e) {
            System.err.println("Failed to save Craft Cards config!");
            e.printStackTrace();
        }
    }

    // ==========================================
    // VALIDATION (Equivalent to defineInRange)
    // ==========================================

    private static void validateRanges() {
        boolean modified = false;

        // Base Tier Validation
        double t0_base = MathHelper.clamp(INSTANCE.baseTierValues.tier0_base, 0.0, 10.0);
        if (t0_base != INSTANCE.baseTierValues.tier0_base) { INSTANCE.baseTierValues.tier0_base = t0_base; modified = true; }

        double t1_base = MathHelper.clamp(INSTANCE.baseTierValues.tier1_base, 0.0, 10.0);
        if (t1_base != INSTANCE.baseTierValues.tier1_base) { INSTANCE.baseTierValues.tier1_base = t1_base; modified = true; }

        double t2_base = MathHelper.clamp(INSTANCE.baseTierValues.tier2_base, 0.0, 10.0);
        if (t2_base != INSTANCE.baseTierValues.tier2_base) { INSTANCE.baseTierValues.tier2_base = t2_base; modified = true; }

        double t3_base = MathHelper.clamp(INSTANCE.baseTierValues.tier3_base, 0.0, 10.0);
        if (t3_base != INSTANCE.baseTierValues.tier3_base) { INSTANCE.baseTierValues.tier3_base = t3_base; modified = true; }

        double t4_base = MathHelper.clamp(INSTANCE.baseTierValues.tier4_base, 0.0, 10.0);
        if (t4_base != INSTANCE.baseTierValues.tier4_base) { INSTANCE.baseTierValues.tier4_base = t4_base; modified = true; }

        double t5_base = MathHelper.clamp(INSTANCE.baseTierValues.tier5_base, 0.0, 10.0);
        if (t5_base != INSTANCE.baseTierValues.tier5_base) { INSTANCE.baseTierValues.tier5_base = t5_base; modified = true; }

        double t6_base = MathHelper.clamp(INSTANCE.baseTierValues.tier6_base, 0.0, 10.0);
        if (t6_base != INSTANCE.baseTierValues.tier6_base) { INSTANCE.baseTierValues.tier6_base = t6_base; modified = true; }

        // Global Tier Validation
        double t0_global = MathHelper.clamp(INSTANCE.globalTierBonuses.tier0_global, 0.0, 10.0);
        if (t0_global != INSTANCE.globalTierBonuses.tier0_global) { INSTANCE.globalTierBonuses.tier0_global = t0_global; modified = true; }

        double t1_global = MathHelper.clamp(INSTANCE.globalTierBonuses.tier1_global, 0.0, 10.0);
        if (t1_global != INSTANCE.globalTierBonuses.tier1_global) { INSTANCE.globalTierBonuses.tier1_global = t1_global; modified = true; }

        double t2_global = MathHelper.clamp(INSTANCE.globalTierBonuses.tier2_global, 0.0, 10.0);
        if (t2_global != INSTANCE.globalTierBonuses.tier2_global) { INSTANCE.globalTierBonuses.tier2_global = t2_global; modified = true; }

        double t3_global = MathHelper.clamp(INSTANCE.globalTierBonuses.tier3_global, 0.0, 10.0);
        if (t3_global != INSTANCE.globalTierBonuses.tier3_global) { INSTANCE.globalTierBonuses.tier3_global = t3_global; modified = true; }

        double t4_global = MathHelper.clamp(INSTANCE.globalTierBonuses.tier4_global, 0.0, 10.0);
        if (t4_global != INSTANCE.globalTierBonuses.tier4_global) { INSTANCE.globalTierBonuses.tier4_global = t4_global; modified = true; }

        double t5_global = MathHelper.clamp(INSTANCE.globalTierBonuses.tier5_global, 0.0, 10.0);
        if (t5_global != INSTANCE.globalTierBonuses.tier5_global) { INSTANCE.globalTierBonuses.tier5_global = t5_global; modified = true; }

        double t6_global = MathHelper.clamp(INSTANCE.globalTierBonuses.tier6_global, 0.0, 10.0);
        if (t6_global != INSTANCE.globalTierBonuses.tier6_global) { INSTANCE.globalTierBonuses.tier6_global = t6_global; modified = true; }

        // If any values were fixed, automatically re-save the file with the corrected bounds
        if (modified) {
            save();
            System.out.println("[Craft Cards] Corrected invalid config values back to the 0.0 - 10.0 range.");
        }
    }
}