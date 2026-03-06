package org.mob.craftcards.loot;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.TagEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.util.Identifier;
import org.mob.craftcards.item.ModItems;
import org.mob.craftcards.util.ModTags;

public class ModLootTableModifiers {

    public static void modifyLootTables() {

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            Identifier id = key.getValue();

            // Random Card -> All Fishing (10%)
            if (id.equals(LootTables.FISHING_FISH_GAMEPLAY.getValue()) ||
                    id.equals(LootTables.FISHING_GAMEPLAY.getValue()) ||
                    id.equals(LootTables.FISHING_JUNK_GAMEPLAY.getValue()) ||
                    id.equals(LootTables.FISHING_TREASURE_GAMEPLAY.getValue())) {

                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.10f)) // 10% chance
                        .with(TagEntry.expandBuilder(ModTags.LOOT_CARD_ITEMS));

                tableBuilder.pool(poolBuilder);
            }

            // Random Card -> All Chests (10%)
            if (id.getPath().startsWith("chests/")) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.10f)) // 10% chance
                        .with(TagEntry.expandBuilder(ModTags.LOOT_CARD_ITEMS));

                tableBuilder.pool(poolBuilder);
            }
        });
    }
}