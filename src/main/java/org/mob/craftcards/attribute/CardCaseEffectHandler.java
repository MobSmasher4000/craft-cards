package org.mob.craftcards.attribute;

import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.collection.DefaultedList;
import org.mob.craftcards.CraftCards;
import org.mob.craftcards.helper.Tier;
import org.mob.craftcards.item.ModItems;
import org.mob.craftcards.util.ModTags;
import net.minecraft.component.DataComponentTypes;

import java.util.*;

public class CardCaseEffectHandler {

    // Define constants
    private static final int FULL_DURATION = 200; // 10 seconds (200 ticks)
    private static final int REFRESH_THRESHOLD = 40; // Refresh when duration is 2 seconds (40 ticks) or less

    // Maps to store bonuses per player UUID
    private static final Map<UUID, Float> SHINY_BONUSES = new HashMap<>();
    private static final Map<UUID, Float> CAPTURE_BONUSES = new HashMap<>();
    private static final Map<UUID, Integer> FORTUNE_BONUSES = new HashMap<>();
    private static final Map<UUID, Integer> LOOTING_BONUSES = new HashMap<>();

    // Tracks the last known Card Case stack for every player
    private static final Map<UUID, ItemStack> PLAYER_CASE_CACHE = new HashMap<>();

    // The "Key" to the current player's data on this specific thread
    private static final ThreadLocal<UUID> CURRENT_PLAYER_UUID = new ThreadLocal<>();

    // Fire Resistance Setup
    private static final RegistryEntry<StatusEffect> FIRE_RESISTANCE_EFFECT = StatusEffects.FIRE_RESISTANCE;
    private static final StatusEffectInstance FIRE_RESISTANCE_INSTANCE = new StatusEffectInstance(
            FIRE_RESISTANCE_EFFECT,
            FULL_DURATION,
            0, // Amplifier (Level I)
            true, // Ambient (used to distinguish this mod's effect)
            false, // Show particles
            true // Show icon
    );

    // Water Breathing Setup
    private static final RegistryEntry<StatusEffect> WATER_BREATHING_EFFECT = StatusEffects.WATER_BREATHING;
    private static final StatusEffectInstance WATER_BREATHING_INSTANCE = new StatusEffectInstance(
            WATER_BREATHING_EFFECT,
            FULL_DURATION,
            0, // Amplifier (Level I)
            true, // Ambient (used to distinguish this mod's effect)
            false, // Show particles
            true // Show icon
    );

    // Regeneration Setup
    private static final RegistryEntry<StatusEffect> REGENERATION_EFFECT = StatusEffects.REGENERATION;

    /**
     * Main handler called every server tick to update player attributes and effects based on cards.
     * @param player The player to update.
     */
    public static void updatePlayer(PlayerEntity player, ItemStack currentStack) {
        if (player.getWorld().isClient) return;

        UUID uuid = player.getUuid();
        setContext(uuid);

        // activeCards returns the full list of case slots (including empty ones)
        List<ItemStack> activeCards = getCardsInCase(currentStack);

        // Handle Effects
        handleFireResistance(player, activeCards);
        handleWaterBreathing(player, activeCards);
        handleRegeneration(player, activeCards);
        handleShinyBonus(player, activeCards);
        handleCaptureBonus(player, activeCards);
        handleFortuneBonus(player, activeCards);
        handleLootingBonus(player, activeCards);
    }

    // --- Player UUID Management ---
    public static void setContext(UUID uuid) { CURRENT_PLAYER_UUID.set(uuid); }
    public static void removeFromCache(UUID uuid) { PLAYER_CASE_CACHE.remove(uuid); }

    private static void handleShinyBonus(PlayerEntity player, List<ItemStack> activeCards) {
        boolean hasCard = activeCards.size() > 20 && activeCards.get(20).isIn(ModTags.SHINY_RATE);

        if (hasCard) {
            float bonus = Tier.fromItem(activeCards.get(20).getItem()).getBonus();
            SHINY_BONUSES.put(player.getUuid(), bonus);
        } else {
            SHINY_BONUSES.remove(player.getUuid()); // Reset on drop
        }
    }

    private static void handleCaptureBonus(PlayerEntity player, List<ItemStack> activeCards) {
        boolean hasCard = activeCards.size() > 21 && activeCards.get(21).isIn(ModTags.CAPTURE_RATE);

        if (hasCard) {
            float bonus = Tier.fromItem(activeCards.get(21).getItem()).getBonus();
            CAPTURE_BONUSES.put(player.getUuid(), bonus);
        } else {
            CAPTURE_BONUSES.remove(player.getUuid()); // Reset on drop
        }
    }

    private static void handleFortuneBonus(PlayerEntity player, List<ItemStack> activeCards) {
        boolean hasCard = activeCards.size() > 22 && activeCards.get(22).isIn(ModTags.FORTUNE);

        if (hasCard) {
            int bonus = Tier.fromItem(activeCards.get(22).getItem()).ordinal();
            FORTUNE_BONUSES.put(player.getUuid(), bonus+1);
        } else {
            FORTUNE_BONUSES.remove(player.getUuid()); // Reset on drop
        }
    }

    private static void handleLootingBonus(PlayerEntity player, List<ItemStack> activeCards) {
        boolean hasCard = activeCards.size() > 23 && activeCards.get(23).isIn(ModTags.LOOTING);

        if (hasCard) {
            int bonus = Tier.fromItem(activeCards.get(23).getItem()).ordinal();
            LOOTING_BONUSES.put(player.getUuid(), bonus+1);
        } else {
            LOOTING_BONUSES.remove(player.getUuid()); // Reset on drop
        }
    }


    // --- Parameterless Getters ---

    /**
     * Returns the Bonus for the player currently in context.
     * Useful for Mixins where passing a PlayerEntity is difficult.
     */
    public static float getShinyBonus() {
        UUID currentId = CURRENT_PLAYER_UUID.get();
        return SHINY_BONUSES.getOrDefault(currentId, 0f);
    }

    public static float getCaptureBonus() {
        UUID currentId = CURRENT_PLAYER_UUID.get();
        return CAPTURE_BONUSES.getOrDefault(currentId, 0f);
    }

    public static int getFortuneBonus() {
        UUID currentId = CURRENT_PLAYER_UUID.get();
        return FORTUNE_BONUSES.getOrDefault(currentId, 0);
    }

    public static int getLootingBonus() {
        UUID currentId = CURRENT_PLAYER_UUID.get();
        return LOOTING_BONUSES.getOrDefault(currentId, 0);
    }


    private static void handleRegeneration(PlayerEntity player, List<ItemStack> activeCards) {
        boolean hasRegenerationCard = activeCards.size() > 4 &&
                activeCards.get(4).isIn(ModTags.REGENERATION);

        if(hasRegenerationCard){Tier cardTier = Tier.fromItem(activeCards.get(4).getItem());

        int desiredAmplifier = cardTier.ordinal();

        final StatusEffectInstance REGENERATION_INSTANCE = new StatusEffectInstance(
                REGENERATION_EFFECT,
                FULL_DURATION,
                desiredAmplifier,
                true,
                false,
                true
        );

        if (hasRegenerationCard) {
            StatusEffectInstance existingEffect = player.getStatusEffect(REGENERATION_EFFECT);

//            // Apply/refresh only if the effect is not present OR if the existing duration is low (2 seconds or less).
            if (existingEffect == null || existingEffect.getDuration() <= REFRESH_THRESHOLD) {
                player.addStatusEffect(REGENERATION_INSTANCE, player);
            }
        } else {
            // Remove the effect if the card is not present, BUT only if the effect originated from this mod.
            removeModEffect(player, REGENERATION_EFFECT, REGENERATION_INSTANCE);
        }}
    }

    /**
     * Checks for the Fire Resistance card at index 19 and applies or removes the effect.
     * @param player The player to affect.
     * @param activeCards List of ItemStacks retrieved from the card case.
     */
    private static void handleFireResistance(PlayerEntity player, List<ItemStack> activeCards) {
        // CHECK INDEX 19: Ensure the list is large enough, then check the card at that index.
        boolean hasFireResistanceCard = activeCards.size() > 19 &&
                activeCards.get(19).isIn(ModTags.FIRE_RESISTANCE);

        if (hasFireResistanceCard) {
            StatusEffectInstance existingEffect = player.getStatusEffect(FIRE_RESISTANCE_EFFECT);

//            // Apply/refresh only if the effect is not present OR if the existing duration is low (2 seconds or less).
            if (existingEffect == null || existingEffect.getDuration() <= REFRESH_THRESHOLD) {
                player.addStatusEffect(FIRE_RESISTANCE_INSTANCE, player);
            }
        } else {
            // Remove the effect if the card is not present, BUT only if the effect originated from this mod.
            removeModEffect(player, FIRE_RESISTANCE_EFFECT, FIRE_RESISTANCE_INSTANCE);
        }
    }

    /**
     * Checks for the Water Breathing card at index 7 and applies or removes the effect.
     * @param player The player to affect.
     * @param activeCards List of ItemStacks retrieved from the card case.
     */
    private static void handleWaterBreathing(PlayerEntity player, List<ItemStack> activeCards) {
        // CHECK INDEX 7: Ensure the list is large enough, then check the card at that index.
        boolean hasWaterBreathingCard = activeCards.size() > 5 &&
                activeCards.get(5).isIn(ModTags.WATER_BREATHING);

        if (hasWaterBreathingCard) {
            StatusEffectInstance existingEffect = player.getStatusEffect(WATER_BREATHING_EFFECT);

            // Apply/refresh only if the effect is not present OR if the existing duration is low (2 seconds or less).
            if (existingEffect == null || existingEffect.getDuration() <= REFRESH_THRESHOLD) {
                player.addStatusEffect(WATER_BREATHING_INSTANCE);
            }
        } else {
            // Remove the effect if the card is not present, BUT only if the effect originated from this mod.
            removeModEffect(player, WATER_BREATHING_EFFECT, WATER_BREATHING_INSTANCE);
        }
    }


    /**
     * Helper to safely remove a status effect applied by this mod.
     * It checks for matching amplifier and ambient flag to avoid removing effects from other sources.
     */
    private static void removeModEffect(PlayerEntity player, RegistryEntry<StatusEffect> effect, StatusEffectInstance modInstance) {
        if (player.hasStatusEffect(effect)) {
            StatusEffectInstance existingEffect = player.getStatusEffect(effect);

            // Check if the existing effect matches the properties of the one applied by the mod.
            if (existingEffect != null &&
                    existingEffect.getAmplifier() == modInstance.getAmplifier() &&
                    existingEffect.isAmbient()) {
                player.removeStatusEffect(effect);
            }
        }
    }

    /**
     * Retrieves all cards stored inside the player's card case (including empty slots).
     * This implementation uses the standard DataComponentTypes.CONTAINER component and assumes 30 slots.
     * @param player The player whose inventory to check.
     * @return A list of ItemStacks (including empty ones) representing the case contents.
     */
    private static List<ItemStack> getCardsInCase(ItemStack cardCaseStack) {

        if (cardCaseStack.isEmpty()) {
            // Return an empty list if no case is found
            return List.of();
        }

        // 2. Extract the ContainerComponent from the cardCaseStack using the standard DataComponentTypes.CONTAINER.
        ContainerComponent container = cardCaseStack.get(DataComponentTypes.CONTAINER);
        if (container == null) {
            // Return empty list if component is missing or null
            return List.of();
        }

        // 3. Copy contents to a DefaultedList of size 24 to enforce slot mapping.
        DefaultedList<ItemStack> internal = DefaultedList.ofSize(CraftCards.CASE_SIZE, ItemStack.EMPTY);
        container.copyTo(internal);

        return internal;
    }
}