package org.mob.craftcards.attribute;

import org.mob.craftcards.CraftCards;
import org.mob.craftcards.util.ModTags;
import org.mob.craftcards.helper.Tier;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import java.util.*;
import java.util.stream.Collectors;

public class CardCaseAttributeHandler {
    // Tracks the last known Card Case stack for every player
    private static final Map<UUID, ItemStack> PLAYER_CASE_CACHE = new HashMap<>();

    /** Mapping from tag -> attribute + key + negative flag */
    private record AttributeMapping(
            RegistryEntry<EntityAttribute> attribute,
            String key,
            boolean negative
    ) {}

    private static final Map<TagKey<Item>, AttributeMapping> TAG_TO_ATTRIBUTE = new HashMap<>();

    // A set of all tags that contribute to the Global Same-Tier Bonus check.
    // This is the set of all attribute tags MINUS SIZE_UP and SIZE_DOWN.
    private static final Set<TagKey<Item>> ATTRIBUTE_TAGS_FOR_GLOBAL;

    // The attribute that is explicitly excluded from receiving the global bonus.
    private static final RegistryEntry<EntityAttribute> EXCLUDED_GLOBAL_ATTRIBUTE = EntityAttributes.GENERIC_SCALE;

    // Map the minimum tier required for the global bonus to the bonus percentage
    private static final Map<Tier, Double> SAME_TIER_BONUSES = new HashMap<>();

    // The unique ID for the global bonus modifier
    private static final Identifier GLOBAL_BONUS_ID = Identifier.of(CraftCards.MOD_ID, "cardcase_global_tier_bonus");


    static {
        // =============== ATTRIBUTE-MAPPED STATS (Includes SIZE attributes for individual bonuses) ===============

        // Armor & toughness
        TAG_TO_ATTRIBUTE.put(ModTags.ARMOR,
                new AttributeMapping(EntityAttributes.GENERIC_ARMOR, "armor", false));
        TAG_TO_ATTRIBUTE.put(ModTags.ARMOR_TOUGHNESS,
                new AttributeMapping(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, "armor_toughness", false));

        // Damage & attack speed
        TAG_TO_ATTRIBUTE.put(ModTags.DAMAGE,
                new AttributeMapping(EntityAttributes.GENERIC_ATTACK_DAMAGE, "attack_damage", false));
        TAG_TO_ATTRIBUTE.put(ModTags.ATTACK_SPEED,
                new AttributeMapping(EntityAttributes.GENERIC_ATTACK_SPEED, "attack_speed", false));

        // Movement Speed, Sneak Speed,
        TAG_TO_ATTRIBUTE.put(ModTags.SPEED_BOOST,
                new AttributeMapping(EntityAttributes.GENERIC_MOVEMENT_SPEED, "move_speed", false));
        TAG_TO_ATTRIBUTE.put(ModTags.SNEAK_SPEED,
                new AttributeMapping(EntityAttributes.PLAYER_SNEAKING_SPEED, "sneak_speed", false));

        // Size Up/Down (REMAINS HERE FOR INDIVIDUAL APPLICATION)
        TAG_TO_ATTRIBUTE.put(ModTags.SIZE_UP,
                new AttributeMapping(EXCLUDED_GLOBAL_ATTRIBUTE, "size_up", false));
        TAG_TO_ATTRIBUTE.put(ModTags.SIZE_DOWN,
                new AttributeMapping(EXCLUDED_GLOBAL_ATTRIBUTE, "size_down", true)); // NEGATIVE

        // Health Boost
        TAG_TO_ATTRIBUTE.put(ModTags.HEALTH_BOOST,
                new AttributeMapping(EntityAttributes.GENERIC_MAX_HEALTH, "max_health", false));

        // Luck Boost
        TAG_TO_ATTRIBUTE.put(ModTags.LUCK_BOOST,
                new AttributeMapping(EntityAttributes.GENERIC_LUCK, "luck", false));

        // Step height, fall height
        TAG_TO_ATTRIBUTE.put(ModTags.STEP_HEIGHT,
                new AttributeMapping(EntityAttributes.GENERIC_STEP_HEIGHT, "step_height", false));
        TAG_TO_ATTRIBUTE.put(ModTags.FEATHER_FALLING,
                new AttributeMapping(EntityAttributes.GENERIC_SAFE_FALL_DISTANCE, "safe_fall", false));

        // Reach / interaction
        TAG_TO_ATTRIBUTE.put(ModTags.BLOCK_INTERACTION_RANGE,
                new AttributeMapping(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE, "block_range", false));
        TAG_TO_ATTRIBUTE.put(ModTags.ENTITY_INTERACTION_RANGE,
                new AttributeMapping(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE, "entity_range", false));

        // Mining / Underwater mining speed
        TAG_TO_ATTRIBUTE.put(ModTags.MINING_SPEED,
                new AttributeMapping(EntityAttributes.PLAYER_BLOCK_BREAK_SPEED, "mining_efficiency", false));
        TAG_TO_ATTRIBUTE.put(ModTags.UNDERWATER_MINING_SPEED,
                new AttributeMapping(EntityAttributes.PLAYER_SUBMERGED_MINING_SPEED, "underwater_mining", false));

        // Jump
        TAG_TO_ATTRIBUTE.put(ModTags.JUMP_BOOST,
                new AttributeMapping(EntityAttributes.GENERIC_JUMP_STRENGTH, "jump_strength", false));

        // Populate the set of all tags that contribute to the GLOBAL bonus (Excludes Size Up/Down)
        ATTRIBUTE_TAGS_FOR_GLOBAL = TAG_TO_ATTRIBUTE.keySet().stream()
                .filter(tag -> TAG_TO_ATTRIBUTE.get(tag).attribute() != EXCLUDED_GLOBAL_ATTRIBUTE)
                .collect(Collectors.toSet());

        // Populate the Same Tier Bonus map
        SAME_TIER_BONUSES.put(Tier.T0, 0.02); // 2%
        SAME_TIER_BONUSES.put(Tier.T1, 0.05); // 5%
        SAME_TIER_BONUSES.put(Tier.T2, 0.10); // 10%
        SAME_TIER_BONUSES.put(Tier.T3, 0.20); // 20%
        SAME_TIER_BONUSES.put(Tier.T4, 0.30); // 30%
        SAME_TIER_BONUSES.put(Tier.T5, 0.40); // 40%
        SAME_TIER_BONUSES.put(Tier.T6, 0.50); // 50%
    }

    // ====================== PUBLIC API ======================
    public static void removeFromCache(UUID uuid) {
        PLAYER_CASE_CACHE.remove(uuid);
    }

    public static void updatePlayer(PlayerEntity player, ItemStack currentStack){
        if (player.getWorld().isClient) return;

        UUID uuid = player.getUuid();
        ItemStack lastStack = PLAYER_CASE_CACHE.getOrDefault(uuid, ItemStack.EMPTY);

        // 1. If both are empty, do nothing
        if (currentStack.isEmpty() && lastStack.isEmpty()) {
            return;
        }

        // 2. Detect if the item was removed or changed
        // We compare the stacks to see if we actually need to re-calculate everything
        if (!ItemStack.areEqual(currentStack, lastStack)) {

            Map<TagKey<Item>, Double> tagBonuses = new HashMap<>();
            Set<Tier> attributeCardTiersForGlobal = new HashSet<>();
            Set<RegistryEntry<EntityAttribute>> activeAttributes = new HashSet<>();
            Set<TagKey<Item>> presentAttributeTagsForGlobal = new HashSet<>();

            if (!currentStack.isEmpty()) {
                accumulateCase(currentStack, tagBonuses, attributeCardTiersForGlobal, activeAttributes, presentAttributeTagsForGlobal);
                PLAYER_CASE_CACHE.put(uuid, currentStack.copy());
            } else {
                // Item was removed, clear cache and let applyAttributes clean up
                PLAYER_CASE_CACHE.remove(uuid);
            }

            double globalBonus = calculateGlobalTierBonus(attributeCardTiersForGlobal, presentAttributeTagsForGlobal);

            // This cleans old modifiers and applies new ones (or none if currentStack is empty)
            applyAttributes(player, tagBonuses, globalBonus, activeAttributes);
        }

    }

    // this method calc global bonus for tooltip
    public static double calculateBonusFromStack(ItemStack stack) {
        ContainerComponent container = stack.get(DataComponentTypes.CONTAINER);
        if (container == null) return 0.0;

        DefaultedList<ItemStack> internal = DefaultedList.ofSize(CraftCards.CASE_SIZE, ItemStack.EMPTY);
        container.copyTo(internal);

        Set<Tier> tiers = new HashSet<>();
        Set<TagKey<Item>> presentTags = new HashSet<>();

        for (int i = 0; i < internal.size(); i++) {
            ItemStack card = internal.get(i);
            if (card.isEmpty()) continue;

            TagKey<Item> tag = ModTags.CARD_CASE_SLOT_TAGS[i];
            if (card.isIn(tag) && ATTRIBUTE_TAGS_FOR_GLOBAL.contains(tag)) {
                tiers.add(Tier.fromItem(card.getItem()));
                presentTags.add(tag);
            }
        }
        return calculateGlobalTierBonus(tiers, presentTags);
    }

    // ====================== INTERNAL ======================

    private static void accumulateCase(ItemStack caseStack,
                                       Map<TagKey<Item>, Double> tagBonuses,
                                       Set<Tier> attributeCardTiersForGlobal,
                                       Set<RegistryEntry<EntityAttribute>> activeAttributes,
                                       Set<TagKey<Item>> presentAttributeTagsForGlobal) {

        // 1. Find the Card Case item stack in the player's inventory or equipment.
        Optional<ItemStack> cardCaseStack = Optional.ofNullable(caseStack);

        if (cardCaseStack.isEmpty()) {
            // Return an empty list if no case is found
            return;
        }

        // 2. Extract the ContainerComponent from the cardCaseStack using the standard DataComponentTypes.CONTAINER.
        ContainerComponent container = cardCaseStack.get().get(DataComponentTypes.CONTAINER);
        if (container == null) {
            // Return empty list if component is missing or null
            return;
        }

        // 3. Copy contents to a DefaultedList of size 24 to enforce slot mapping.
        DefaultedList<ItemStack> internal = DefaultedList.ofSize(CraftCards.CASE_SIZE, ItemStack.EMPTY);
        container.copyTo(internal);

        for (int i = 0; i < internal.size(); i++) {
            ItemStack stack = internal.get(i);
            if (stack.isEmpty()) continue;

            TagKey<Item> tag = ModTags.CARD_CASE_SLOT_TAGS[i];
            if (!stack.isIn(tag)) continue;

            Tier tier = Tier.fromItem(stack.getItem());
            double bonus = tier.getBonus();

            tagBonuses.merge(tag, bonus, Double::sum);

            AttributeMapping mapping = TAG_TO_ATTRIBUTE.get(tag);

            if (mapping != null) {
                // 1. Track the attribute as active for targeted individual/global application
                activeAttributes.add(mapping.attribute());

                // 2. Only track for Global Bonus calculation if the tag is not excluded
                if (ATTRIBUTE_TAGS_FOR_GLOBAL.contains(tag)) {
                    attributeCardTiersForGlobal.add(tier);
                    presentAttributeTagsForGlobal.add(tag);
                }
            }
        }
    }

    /**
     * Determines the global bonus value only if a card is present for *every* NON-EXCLUDED attribute-mapped tag.
     */
    private static double calculateGlobalTierBonus(Set<Tier> attributeCardTiersForGlobal, Set<TagKey<Item>> presentAttributeTagsForGlobal) {

        // 1. Check for Full Completion (based on the EXCLUDED size)
        if (presentAttributeTagsForGlobal.size() != ATTRIBUTE_TAGS_FOR_GLOBAL.size()) {
            return 0.0;
        }

        if (attributeCardTiersForGlobal.isEmpty()) {
            return 0.0;
        }

        // 2. Find the lowest tier among the non-excluded attributes
        Tier minTier = attributeCardTiersForGlobal.stream()
                .min(Tier::compareTo)
                .orElse(null);

        if (minTier == null) {
            return 0.0;
        }

        // 3. The bonus is based on the lowest tier found
        return SAME_TIER_BONUSES.getOrDefault(minTier, 0.0);
    }

    /**
     * Applies attribute modifiers, including the required aggressive cleanup of the global bonus,
     * while explicitly skipping the excluded attribute (GENERIC_SCALE) for the global bonus application.
     */
    private static void applyAttributes(PlayerEntity player,
                                        Map<TagKey<Item>, Double> tagBonuses,
                                        double globalBonus,
                                        Set<RegistryEntry<EntityAttribute>> activeAttributes) {

        // --- PART 1: APPLY AND CLEAN INDIVIDUAL TAG BONUSES (NO CHANGE) ---
        for (var tagEntry : TAG_TO_ATTRIBUTE.entrySet()) {
            TagKey<Item> tag = tagEntry.getKey();
            AttributeMapping mapping = tagEntry.getValue();
            // ... (Individual tag modifier logic remains the same) ...
            double rawValue = tagBonuses.getOrDefault(tag, 0.0);
            double value = mapping.negative() ? -rawValue : rawValue;

            EntityAttributeInstance instance = player.getAttributeInstance(mapping.attribute());
            if (instance == null) continue;

            EntityAttributeModifier.Operation operation;
            if (instance.getBaseValue() == 0.0) {
                operation = EntityAttributeModifier.Operation.ADD_VALUE;
            } else {
                operation = EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE;
            }

            String tagPath = tag.id().getPath().replace('/', '_');
            Identifier modifierId = Identifier.of(CraftCards.MOD_ID, "cardcase_tag_" + tagPath);

            if (instance.hasModifier(modifierId)) {
                instance.removeModifier(modifierId);
            }

            if (value != 0.0) {
                EntityAttributeModifier modifier = new EntityAttributeModifier(
                        modifierId,
                        value,
                        operation
                );
                instance.addPersistentModifier(modifier);
            }
        }

        // --- PART 2: GLOBAL SAME-TIER BONUS CLEANUP AND RE-APPLICATION (REVISED CLEANUP) ---

        // Get the complete set of ALL possible attributes this handler touches.
        Set<RegistryEntry<EntityAttribute>> allPossibleAttributes = TAG_TO_ATTRIBUTE.values().stream()
                .map(AttributeMapping::attribute)
                .collect(Collectors.toSet());

        // 1. AGGRESSIVE CLEANUP: Remove the global modifier from ALL attributes.
        for (RegistryEntry<EntityAttribute> attribute : allPossibleAttributes) {
            EntityAttributeInstance instance = player.getAttributeInstance(attribute);
            if (instance == null) continue;

            if (instance.hasModifier(GLOBAL_BONUS_ID)) {
                instance.removeModifier(GLOBAL_BONUS_ID);
            }
        }

        // 2. RE-APPLY: Apply the global bonus, EXCLUDING the GENERIC_SCALE attribute.
        if (globalBonus != 0.0) {
            for (RegistryEntry<EntityAttribute> attribute : activeAttributes) {

                // EXCLUSION CHECK: Skip the excluded attribute
                if (attribute.equals(EXCLUDED_GLOBAL_ATTRIBUTE)) {
                    continue;
                }

                EntityAttributeInstance instance = player.getAttributeInstance(attribute);
                if (instance == null) continue;

                // Determine Operation based on attribute base value
                EntityAttributeModifier.Operation globalOperation;
                if (instance.getBaseValue() == 0.0) {
                    globalOperation = EntityAttributeModifier.Operation.ADD_VALUE;
                } else {
                    globalOperation = EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE;
                }

                EntityAttributeModifier globalModifier = new EntityAttributeModifier(
                        GLOBAL_BONUS_ID,
                        globalBonus,
                        globalOperation
                );

                instance.addPersistentModifier(globalModifier);
            }
        }
    }
}