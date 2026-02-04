package org.mob.craftcards.datagen;

import io.wispforest.accessories.api.data.AccessoriesTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.mob.craftcards.CraftCards;
import org.mob.craftcards.item.ModItems;
import org.mob.craftcards.util.ModTags;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(ModTags.FIRE_RESISTANCE)
                .add(ModItems.FIRE_RESISTANCE);
        getOrCreateTagBuilder(ModTags.WATER_BREATHING)
                .add(ModItems.WATER_BREATHING);

        getOrCreateTagBuilder(AccessoriesTags.BELT_TAG)
                .add(ModItems.CARD_CASE);

        // Per-stat tags – each gets all its tiered card items
        addTieredCards(ModTags.ARMOR, "armor");
        addTieredCards(ModTags.ARMOR_TOUGHNESS, "armor_toughness");
        addTieredCards(ModTags.ATTACK_SPEED, "attack_speed");
        addTieredCards(ModTags.BLOCK_INTERACTION_RANGE, "block_interaction_range");
        addTieredCards(ModTags.CAPTURE_RATE, "capture_rate");
        addTieredCards(ModTags.DAMAGE, "damage"); // Sharpness card
        addTieredCards(ModTags.ENTITY_INTERACTION_RANGE, "entity_interaction_range");
        addTieredCards(ModTags.FEATHER_FALLING, "feather_falling");
        addTieredCards(ModTags.FORTUNE, "fortune");
        addTieredCards(ModTags.HEALTH_BOOST, "health_boost");
        addTieredCards(ModTags.JUMP_BOOST, "jump_boost");
        addTieredCards(ModTags.LOOTING, "looting");
        addTieredCards(ModTags.LUCK_BOOST, "luck_boost");
        addTieredCards(ModTags.MINING_SPEED, "mining_speed");
        addTieredCards(ModTags.REGENERATION, "regeneration");
        addTieredCards(ModTags.SHINY_RATE, "shiny_rate");
        addTieredCards(ModTags.SIZE_DOWN, "size_down");
        addTieredCards(ModTags.SIZE_UP, "size_up");
        addTieredCards(ModTags.SNEAK_SPEED, "sneak_speed");
        addTieredCards(ModTags.SPEED_BOOST, "speed_boost"); // Movement Speed card
        addTieredCards(ModTags.STEP_HEIGHT, "step_height");
        addTieredCards(ModTags.UNDERWATER_MINING_SPEED, "underwater_mining_speed");

        // Master tag that contains *all* card tags
        getOrCreateTagBuilder(ModTags.CARD_ITEMS)
                .addTag(ModTags.ARMOR)
                .addTag(ModTags.ARMOR_TOUGHNESS)
                .addTag(ModTags.ATTACK_SPEED)
                .addTag(ModTags.BLOCK_INTERACTION_RANGE)
                .addTag(ModTags.CAPTURE_RATE)
                .addTag(ModTags.DAMAGE)
                .addTag(ModTags.ENTITY_INTERACTION_RANGE)
                .addTag(ModTags.FEATHER_FALLING)
                .addTag(ModTags.FIRE_RESISTANCE)
                .addTag(ModTags.FORTUNE)
                .addTag(ModTags.HEALTH_BOOST)
                .addTag(ModTags.JUMP_BOOST)
                .addTag(ModTags.LOOTING)
                .addTag(ModTags.LUCK_BOOST)
                .addTag(ModTags.MINING_SPEED)
                .addTag(ModTags.REGENERATION)
                .addTag(ModTags.SHINY_RATE)
                .addTag(ModTags.SIZE_DOWN)
                .addTag(ModTags.SIZE_UP)
                .addTag(ModTags.SNEAK_SPEED)
                .addTag(ModTags.SPEED_BOOST)
                .addTag(ModTags.STEP_HEIGHT)
                .addTag(ModTags.UNDERWATER_MINING_SPEED)
                .addTag(ModTags.WATER_BREATHING);
    }

    /**
     * Adds all tiered card items to a tag, assuming ids like:
     *   <baseId>_tier0 .. <baseId>_tier6
     * Example: "armor_tier0" .. "armor_tier6"
     */
    private void addTieredCards(TagKey<Item> tag, String baseId) {
        var builder = getOrCreateTagBuilder(tag);

        for (int tier = 0; tier <= 6; tier++) {
            Identifier id = Identifier.of(CraftCards.MOD_ID, baseId + "_tier" + tier);
            // addOptional so missing items won't crash datagen
            builder.addOptional(id);
        }
    }
}
