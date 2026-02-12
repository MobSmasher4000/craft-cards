package org.mob.craftcards.attribute;

import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import org.mob.craftcards.CraftCards;
import org.mob.craftcards.helper.Tier;
import org.mob.craftcards.util.ModTags;

import java.util.*;

public class CardCaseEffectHandler {

    // Constants
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
    private static final Holder<MobEffect> FIRE_RESISTANCE_EFFECT = MobEffects.FIRE_RESISTANCE;
    private static final MobEffectInstance FIRE_RESISTANCE_INSTANCE = new MobEffectInstance(
            FIRE_RESISTANCE_EFFECT,
            FULL_DURATION,
            0, // Amplifier (Level I)
            true, // Ambient
            false, // Show particles
            true // Show icon
    );

    // Water Breathing Setup
    private static final Holder<MobEffect> WATER_BREATHING_EFFECT = MobEffects.WATER_BREATHING;
    private static final MobEffectInstance WATER_BREATHING_INSTANCE = new MobEffectInstance(
            WATER_BREATHING_EFFECT,
            FULL_DURATION,
            0, // Amplifier (Level I)
            true, // Ambient
            false, // Show particles
            true // Show icon
    );

    // Regeneration Setup
    private static final Holder<MobEffect> REGENERATION_EFFECT = MobEffects.REGENERATION;

    /**
     * Main handler called every server tick to update player attributes and effects based on cards.
     * @param player The player to update.
     */
    public static void updatePlayer(Player player, ItemStack currentStack) {
        if (player.level().isClientSide) return;

        UUID uuid = player.getUUID();
        setContext(uuid);
        ItemStack lastStack = PLAYER_CASE_CACHE.getOrDefault(uuid, ItemStack.EMPTY);

        // activeCards returns the full list of case slots (including empty ones)
        List<ItemStack> activeCards = getCardsInCase(currentStack);

        // Handle Effects and Bonuses
        handleFireResistance(player, activeCards);
        handleWaterBreathing(player, activeCards);
        handleRegeneration(player, activeCards);

        if (!ItemStack.matches(currentStack, lastStack)){
            handleShinyBonus(player, activeCards);
            handleCaptureBonus(player, activeCards);
            handleFortuneBonus(player, activeCards);
            handleLootingBonus(player, activeCards);
            handleFlight(player, activeCards);
        }

        PLAYER_CASE_CACHE.put(uuid, currentStack.copy());
    }

    // --- Player UUID Management ---
    public static void setContext(UUID uuid) { CURRENT_PLAYER_UUID.set(uuid); }
    public static void removeFromCache(UUID uuid) { PLAYER_CASE_CACHE.remove(uuid); }

    private static void handleShinyBonus(Player player, List<ItemStack> activeCards) {
        boolean hasCard = activeCards.size() > 20 && activeCards.get(20).is(ModTags.SHINY_RATE);

        if (hasCard) {
            float bonus = Tier.tierFromItem(activeCards.get(20).getItem()).getBonus();
            float globalBonus = (float) CardCaseAttributeHandler.getActiveGlobalBonus(player.getUUID());
            SHINY_BONUSES.put(player.getUUID(), bonus + globalBonus);
        } else {
            SHINY_BONUSES.remove(player.getUUID()); // Reset on drop
        }
    }

    private static void handleCaptureBonus(Player player, List<ItemStack> activeCards) {
        boolean hasCard = activeCards.size() > 21 && activeCards.get(21).is(ModTags.CAPTURE_RATE);

        if (hasCard) {
            float bonus = Tier.tierFromItem(activeCards.get(21).getItem()).getBonus();
            float globalBonus = (float) CardCaseAttributeHandler.getActiveGlobalBonus(player.getUUID());
            CAPTURE_BONUSES.put(player.getUUID(), bonus + globalBonus);
        } else {
            CAPTURE_BONUSES.remove(player.getUUID()); // Reset on drop
        }
    }

    private static void handleFortuneBonus(Player player, List<ItemStack> activeCards) {
        boolean hasCard = activeCards.size() > 22 && activeCards.get(22).is(ModTags.FORTUNE);

        if (hasCard) {
            int bonus = Tier.tierFromItem(activeCards.get(22).getItem()).ordinal() + 1;
            int globalBonusLevel = CardCaseAttributeHandler.getActiveGlobalTier(player.getUUID());
            if (globalBonusLevel == -1) {
                FORTUNE_BONUSES.put(player.getUUID(), bonus);
            }else {
                FORTUNE_BONUSES.put(player.getUUID(), bonus + globalBonusLevel);
            }
        } else {
            FORTUNE_BONUSES.remove(player.getUUID()); // Reset on drop
        }
    }

    private static void handleLootingBonus(Player player, List<ItemStack> activeCards) {
        boolean hasCard = activeCards.size() > 23 && activeCards.get(23).is(ModTags.LOOTING);

        if (hasCard) {
            int bonus = Tier.tierFromItem(activeCards.get(23).getItem()).ordinal() + 1;
            int globalBonusLevel = CardCaseAttributeHandler.getActiveGlobalTier(player.getUUID());
            if (globalBonusLevel == -1) {
                LOOTING_BONUSES.put(player.getUUID(), bonus);
            }else {
                LOOTING_BONUSES.put(player.getUUID(), bonus + globalBonusLevel);
            }
        } else {
            LOOTING_BONUSES.remove(player.getUUID()); // Reset on drop
        }
    }

    private static void handleFlight(Player player, List<ItemStack> activeCards) {
        // Check Index 24
        boolean hasFlightCard = activeCards.size() > 24 && activeCards.get(24).is(ModTags.FLIGHT);

        if (hasFlightCard) {
            // Enable Flight if not already enabled
            if (!player.getAbilities().mayfly) {
                player.getAbilities().mayfly = true;
                player.onUpdateAbilities(); // Send to client
            }
        } else {
            // Disable Flight ONLY if:
            // They don't have the card
            // They are NOT in Creative or Spectator mode
            // They currently can fly
            if (!player.isCreative() && !player.isSpectator() && player.getAbilities().mayfly) {
                player.getAbilities().mayfly = false;
                player.getAbilities().flying = false; // Stop them from flying instantly
                player.onUpdateAbilities(); // Send to client
            }
        }
    }


    // --- Parameterless Getters ---

    // Returns the Bonus for the player currently in context.
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


    private static void handleRegeneration(Player player, List<ItemStack> activeCards) {
        boolean hasRegenerationCard = activeCards.size() > 4 &&
                activeCards.get(4).is(ModTags.REGENERATION);

        if (hasRegenerationCard) {
            int desiredAmplifier = Tier.tierFromItem(activeCards.get(4).getItem()).ordinal();

            final MobEffectInstance REGENERATION_INSTANCE = new MobEffectInstance(
                    REGENERATION_EFFECT,
                    FULL_DURATION,
                    desiredAmplifier,
                    true,
                    false,
                    true
            );

            MobEffectInstance existingEffect = player.getEffect(REGENERATION_EFFECT);

            // Apply/refresh only if the effect is not present OR if the existing duration is low (2 seconds or less).
            if (existingEffect == null || existingEffect.getDuration() <= REFRESH_THRESHOLD) {
                player.addEffect(REGENERATION_INSTANCE, player);
            }
        } else {
            if (player.hasEffect(REGENERATION_EFFECT)) {
                MobEffectInstance existing = player.getEffect(REGENERATION_EFFECT);
                if (existing != null && existing.isAmbient()) {
                    player.removeEffect(REGENERATION_EFFECT);
                }
            }
        }
    }

    /**
     * Checks for the Fire Resistance card at index 19 and applies or removes the effect.
     * @param player The player to affect.
     * @param activeCards List of ItemStacks retrieved from the card case.
     */
    private static void handleFireResistance(Player player, List<ItemStack> activeCards) {
        boolean hasFireResistanceCard = activeCards.size() > 19 &&
                activeCards.get(19).is(ModTags.FIRE_RESISTANCE);

        if (hasFireResistanceCard) {
            MobEffectInstance existingEffect = player.getEffect(FIRE_RESISTANCE_EFFECT);

            // Apply/refresh only if the effect is not present OR if the existing duration is low (2 seconds or less).
            if (existingEffect == null || existingEffect.getDuration() <= REFRESH_THRESHOLD) {
                player.addEffect(FIRE_RESISTANCE_INSTANCE, player);
            }
        } else {
            // Remove the effect if the card is not present, BUT only if the effect originated from this mod.
            removeModEffect(player, FIRE_RESISTANCE_EFFECT, FIRE_RESISTANCE_INSTANCE);
        }
    }

    /**
     * Checks for the Water Breathing card at index 5 and applies or removes the effect.
     * @param player The player to affect.
     * @param activeCards List of ItemStacks retrieved from the card case.
     */
    private static void handleWaterBreathing(Player player, List<ItemStack> activeCards) {
        boolean hasWaterBreathingCard = activeCards.size() > 5 &&
                activeCards.get(5).is(ModTags.WATER_BREATHING);

        if (hasWaterBreathingCard) {
            MobEffectInstance existingEffect = player.getEffect(WATER_BREATHING_EFFECT);

            // Apply/refresh only if the effect is not present OR if the existing duration is low (2 seconds or less).
            if (existingEffect == null || existingEffect.getDuration() <= REFRESH_THRESHOLD) {
                player.addEffect(WATER_BREATHING_INSTANCE);
            }
        } else {
            // Remove the effect if the card is not present, BUT only if the effect originated from this mod.
            removeModEffect(player, WATER_BREATHING_EFFECT, WATER_BREATHING_INSTANCE);
        }
    }


    /**
     * Helper to safely remove a status effect applied by the mod.
     * It checks for matching amplifier and ambient flag to avoid removing effects from other sources.
     */
    private static void removeModEffect(Player player, Holder<MobEffect> effect, MobEffectInstance modInstance) {
        if (player.hasEffect(effect)) {
            MobEffectInstance existingEffect = player.getEffect(effect);

            // Check if the existing effect matches the properties of the one applied by the mod.
            if (existingEffect != null &&
                    existingEffect.getAmplifier() == modInstance.getAmplifier() &&
                    existingEffect.isAmbient()) {
                player.removeEffect(effect);
            }
        }
    }

    /**
     * Retrieves all cards stored inside the player's card case (including empty slots).
     * This implementation uses the standard DataComponentTypes.CONTAINER component and assumes 25 slots.
     * @param cardCaseStack The stack to check.
     * @return A list of ItemStacks (including empty ones) representing the case contents.
     */
    private static List<ItemStack> getCardsInCase(ItemStack cardCaseStack) {

        if (cardCaseStack.isEmpty()) {
            // Return an empty list if no case is found
            return List.of();
        }

        // 2. Extract the ItemContainerContents from the cardCaseStack using DataComponents.CONTAINER.
        ItemContainerContents container = cardCaseStack.get(DataComponents.CONTAINER);
        if (container == null) {
            // Return empty list if component is missing or null
            return List.of();
        }

        // 3. Copy contents to a NonNullList of size 24 to enforce slot mapping.
        NonNullList<ItemStack> internal = NonNullList.withSize(CraftCards.CASE_SIZE, ItemStack.EMPTY);
        container.copyInto(internal);

        return internal;
    }
}