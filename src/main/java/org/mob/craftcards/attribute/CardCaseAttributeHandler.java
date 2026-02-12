package org.mob.craftcards.attribute;

import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import org.mob.craftcards.CraftCards;
import org.mob.craftcards.helper.Tier;
import org.mob.craftcards.util.ModTags;

import java.util.*;
import java.util.stream.Collectors;

public class CardCaseAttributeHandler {
    // Tracks the last known Card Case stack for every player
    private static final Map<UUID, ItemStack> PLAYER_CASE_CACHE = new HashMap<>();

    // Simple record to hold both values
    public record GlobalBonusData(int tierOrdinal, double bonus) {}

    // Tracks the active global bonus and tier for each player
    private static final Map<UUID, GlobalBonusData> PLAYER_GLOBAL_BONUS_CACHE = new HashMap<>();

    /** Mapping from tag -> attribute + key + negative flag */
    private record AttributeMapping(
            Holder<Attribute> attribute,
            String key,
            boolean negative
    ) {}

    private static final Map<TagKey<Item>, AttributeMapping> TAG_TO_ATTRIBUTE = new HashMap<>();

    // A set of all tags that contribute to the Global Same-Tier Bonus check.
    private static final Set<TagKey<Item>> ATTRIBUTE_TAGS_FOR_GLOBAL;

    // The attribute that is explicitly excluded from receiving the global bonus.
    private static final Holder<Attribute> EXCLUDED_GLOBAL_ATTRIBUTE = Attributes.SCALE;

    // Map the minimum tier required for the global bonus to the bonus percentage
    private static final Map<Tier, Double> SAME_TIER_BONUSES = new HashMap<>();

    // The unique ID for the global bonus modifier
    private static final ResourceLocation GLOBAL_BONUS_ID = ResourceLocation.fromNamespaceAndPath(CraftCards.MOD_ID, "cardcase_global_tier_bonus");


    static {
        // =============== ATTRIBUTE-MAPPED STATS ===============
        // Armor & toughness
        TAG_TO_ATTRIBUTE.put(ModTags.ARMOR,
                new AttributeMapping(Attributes.ARMOR, "armor", false));
        TAG_TO_ATTRIBUTE.put(ModTags.ARMOR_TOUGHNESS,
                new AttributeMapping(Attributes.ARMOR_TOUGHNESS, "armor_toughness", false));

        // Damage & attack speed
        TAG_TO_ATTRIBUTE.put(ModTags.DAMAGE,
                new AttributeMapping(Attributes.ATTACK_DAMAGE, "attack_damage", false));
        TAG_TO_ATTRIBUTE.put(ModTags.ATTACK_SPEED,
                new AttributeMapping(Attributes.ATTACK_SPEED, "attack_speed", false));

        // Movement Speed, Sneak Speed
        TAG_TO_ATTRIBUTE.put(ModTags.SPEED_BOOST,
                new AttributeMapping(Attributes.MOVEMENT_SPEED, "move_speed", false));
        TAG_TO_ATTRIBUTE.put(ModTags.SNEAK_SPEED,
                new AttributeMapping(Attributes.SNEAKING_SPEED, "sneak_speed", false));

        // Size Up/Down (REMAINS HERE FOR INDIVIDUAL APPLICATION)
        TAG_TO_ATTRIBUTE.put(ModTags.SIZE_UP,
                new AttributeMapping(EXCLUDED_GLOBAL_ATTRIBUTE, "size_up", false));
        TAG_TO_ATTRIBUTE.put(ModTags.SIZE_DOWN,
                new AttributeMapping(EXCLUDED_GLOBAL_ATTRIBUTE, "size_down", true)); // NEGATIVE

        // Health Boost
        TAG_TO_ATTRIBUTE.put(ModTags.HEALTH_BOOST,
                new AttributeMapping(Attributes.MAX_HEALTH, "max_health", false));

        // Luck Boost
        TAG_TO_ATTRIBUTE.put(ModTags.LUCK_BOOST,
                new AttributeMapping(Attributes.LUCK, "luck", false));

        // Step height, fall height
        TAG_TO_ATTRIBUTE.put(ModTags.STEP_HEIGHT,
                new AttributeMapping(Attributes.STEP_HEIGHT, "step_height", false));
        TAG_TO_ATTRIBUTE.put(ModTags.FEATHER_FALLING,
                new AttributeMapping(Attributes.SAFE_FALL_DISTANCE, "safe_fall", false));

        // Reach / interaction
        TAG_TO_ATTRIBUTE.put(ModTags.BLOCK_INTERACTION_RANGE,
                new AttributeMapping(Attributes.BLOCK_INTERACTION_RANGE, "block_range", false));
        TAG_TO_ATTRIBUTE.put(ModTags.ENTITY_INTERACTION_RANGE,
                new AttributeMapping(Attributes.ENTITY_INTERACTION_RANGE, "entity_range", false));

        // Mining / Underwater mining speed
        TAG_TO_ATTRIBUTE.put(ModTags.MINING_SPEED,
                new AttributeMapping(Attributes.BLOCK_BREAK_SPEED, "mining_efficiency", false));
        TAG_TO_ATTRIBUTE.put(ModTags.UNDERWATER_MINING_SPEED,
                new AttributeMapping(Attributes.SUBMERGED_MINING_SPEED, "underwater_mining", false));

        // Jump
        TAG_TO_ATTRIBUTE.put(ModTags.JUMP_BOOST,
                new AttributeMapping(Attributes.JUMP_STRENGTH, "jump_strength", false));

        // Populate the set of all tags that contribute to the GLOBAL bonus (Excludes Size Up/Down and Speed Boost)
        ATTRIBUTE_TAGS_FOR_GLOBAL = TAG_TO_ATTRIBUTE.keySet().stream()
                .filter(tag -> {
                    if (TAG_TO_ATTRIBUTE.get(tag).attribute() == EXCLUDED_GLOBAL_ATTRIBUTE) return false;
                    // Exclude Attribute which are in Global_exempt tag
                    if (tag.equals(ModTags.SPEED_BOOST)) return false;
                    if (tag.equals(ModTags.SNEAK_SPEED)) return false;
                    if (tag.equals(ModTags.JUMP_BOOST)) return false;
                    if (tag.equals(ModTags.STEP_HEIGHT)) return false;
                    return true;
                }).collect(Collectors.toSet());

        // Same Tier Bonus map
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
        PLAYER_GLOBAL_BONUS_CACHE.remove(uuid);
    }

    /**
     * Returns the active global bonus multiplier (e.g., 0.20 for 20%).
     * Returns 0.0 if the player has no valid set.
     */
    public static double getActiveGlobalBonus(UUID uuid) {
        return PLAYER_GLOBAL_BONUS_CACHE.getOrDefault(uuid, new GlobalBonusData(-1, 0.0)).bonus();
    }

    /**
     * Returns the ordinal of the active global tier (e.g., 4 for T3).
     * Returns -1 if the player has no valid set.
     */
    public static int getActiveGlobalTier(UUID uuid) {
        return PLAYER_GLOBAL_BONUS_CACHE.getOrDefault(uuid, new GlobalBonusData(-1, 0.0)).tierOrdinal();
    }

    public static void updatePlayer(Player player, ItemStack currentStack){
        if (player.level().isClientSide) return;

        UUID uuid = player.getUUID();
        ItemStack lastStack = PLAYER_CASE_CACHE.getOrDefault(uuid, ItemStack.EMPTY);

        // 1. If both are empty, do nothing
        if (currentStack.isEmpty() && lastStack.isEmpty()) {
            return;
        }

        // 2. Detect if the item was removed or changed
        if (!ItemStack.matches(currentStack, lastStack)) {

            Map<TagKey<Item>, Double> tagBonuses = new HashMap<>();
            Set<Tier> attributeCardTiersForGlobal = new HashSet<>();
            Set<Holder<Attribute>> activeAttributes = new HashSet<>();
            Set<TagKey<Item>> presentAttributeTagsForGlobal = new HashSet<>();

            if (!currentStack.isEmpty()) {
                accumulateCase(currentStack, tagBonuses, attributeCardTiersForGlobal, activeAttributes, presentAttributeTagsForGlobal);
                PLAYER_CASE_CACHE.put(uuid, currentStack.copy());
            } else {
                PLAYER_CASE_CACHE.remove(uuid);
            }

            double globalBonus = calculateGlobalTierBonus(attributeCardTiersForGlobal, presentAttributeTagsForGlobal);

            int globalBonusLevel = -1;

            if (globalBonus > 0 && !attributeCardTiersForGlobal.isEmpty()) {
                // Get the lowest tier in the set
                globalBonusLevel = Collections.min(attributeCardTiersForGlobal).ordinal() + 1;
            }

            PLAYER_GLOBAL_BONUS_CACHE.put(uuid,new GlobalBonusData(globalBonusLevel, globalBonus));
            applyAttributes(player, tagBonuses, globalBonus, activeAttributes);
        }
    }

    // this method calc global bonus for tooltip
    public static double calculateBonusFromStack(ItemStack stack) {
        ItemContainerContents container = stack.get(DataComponents.CONTAINER);
        if (container == null) return 0.0;

        NonNullList<ItemStack> internal = NonNullList.withSize(CraftCards.CASE_SIZE, ItemStack.EMPTY);
        container.copyInto(internal);

        Set<Tier> tiers = new HashSet<>();
        Set<TagKey<Item>> presentTags = new HashSet<>();

        for (int i = 0; i < internal.size(); i++) {
            ItemStack card = internal.get(i);
            if (card.isEmpty()) continue;

            TagKey<Item> tag = ModTags.CARD_CASE_SLOT_TAGS[i];
            if (card.is(tag) && ATTRIBUTE_TAGS_FOR_GLOBAL.contains(tag)) {
                tiers.add(Tier.tierFromItem(card.getItem()));
                presentTags.add(tag);
            }
        }
        return calculateGlobalTierBonus(tiers, presentTags);
    }

    // ====================== INTERNAL ======================

    private static void accumulateCase(ItemStack caseStack,
                                       Map<TagKey<Item>, Double> tagBonuses,
                                       Set<Tier> attributeCardTiersForGlobal,
                                       Set<Holder<Attribute>> activeAttributes,
                                       Set<TagKey<Item>> presentAttributeTagsForGlobal) {

        if (caseStack.isEmpty()) return;

        ItemContainerContents container = caseStack.get(DataComponents.CONTAINER);
        if (container == null) return;

        NonNullList<ItemStack> internal = NonNullList.withSize(CraftCards.CASE_SIZE, ItemStack.EMPTY);
        container.copyInto(internal);

        for (int i = 0; i < internal.size(); i++) {
            ItemStack stack = internal.get(i);
            if (stack.isEmpty()) continue;

            TagKey<Item> tag = ModTags.CARD_CASE_SLOT_TAGS[i];
            if (!stack.is(tag)) continue;

            Tier tier = Tier.tierFromItem(stack.getItem());
            double bonus = tier.getBonus();

            tagBonuses.merge(tag, bonus, Double::sum);

            AttributeMapping mapping = TAG_TO_ATTRIBUTE.get(tag);

            if (mapping != null) {
                activeAttributes.add(mapping.attribute());

                if (ATTRIBUTE_TAGS_FOR_GLOBAL.contains(tag)) {
                    attributeCardTiersForGlobal.add(tier);
                    presentAttributeTagsForGlobal.add(tag);
                }
            }
        }
    }

    private static double calculateGlobalTierBonus(Set<Tier> attributeCardTiersForGlobal, Set<TagKey<Item>> presentAttributeTagsForGlobal) {
        if (presentAttributeTagsForGlobal.size() != ATTRIBUTE_TAGS_FOR_GLOBAL.size()) {
            return 0.0;
        }

        if (attributeCardTiersForGlobal.isEmpty()) {
            return 0.0;
        }

        Tier minTier = attributeCardTiersForGlobal.stream()
                .min(Tier::compareTo)
                .orElse(null);

        if (minTier == null) {
            return 0.0;
        }

        return SAME_TIER_BONUSES.getOrDefault(minTier, 0.0);
    }

    private static void applyAttributes(Player player,
                                        Map<TagKey<Item>, Double> tagBonuses,
                                        double globalBonus,
                                        Set<Holder<Attribute>> activeAttributes) {

        // --- PART 1: APPLY AND CLEAN INDIVIDUAL TAG BONUSES ---
        for (var tagEntry : TAG_TO_ATTRIBUTE.entrySet()) {
            TagKey<Item> tag = tagEntry.getKey();
            AttributeMapping mapping = tagEntry.getValue();

            double rawValue = tagBonuses.getOrDefault(tag, 0.0);
            double value = mapping.negative() ? -rawValue : rawValue;

            AttributeInstance instance = player.getAttribute(mapping.attribute());
            if (instance == null) continue;

            AttributeModifier.Operation operation;
            if (instance.getBaseValue() == 0.0) {
                operation = AttributeModifier.Operation.ADD_VALUE;
            } else {
                operation = AttributeModifier.Operation.ADD_MULTIPLIED_BASE;
            }

            String tagPath = tag.location().getPath().replace('/', '_');
            ResourceLocation modifierId = ResourceLocation.fromNamespaceAndPath(CraftCards.MOD_ID, "cardcase_tag_" + tagPath);

            if (instance.hasModifier(modifierId)) {
                instance.removeModifier(modifierId);
            }

            if (value != 0.0) {
                AttributeModifier modifier = new AttributeModifier(
                        modifierId,
                        value,
                        operation
                );
                instance.addPermanentModifier(modifier);
            }
        }

        // --- PART 2: GLOBAL SAME-TIER BONUS CLEANUP AND RE-APPLICATION ---

        Set<Holder<Attribute>> allPossibleAttributes = TAG_TO_ATTRIBUTE.values().stream()
                .map(AttributeMapping::attribute)
                .collect(Collectors.toSet());

        // 1. CLEANUP
        for (Holder<Attribute> attribute : allPossibleAttributes) {
            AttributeInstance instance = player.getAttribute(attribute);
            if (instance == null) continue;

            if (instance.hasModifier(GLOBAL_BONUS_ID)) {
                instance.removeModifier(GLOBAL_BONUS_ID);
            }
        }

        // 2. RE-APPLY
        if (globalBonus != 0.0) {
            for (Holder<Attribute> attribute : activeAttributes) {

                if (attribute.equals(EXCLUDED_GLOBAL_ATTRIBUTE) || attribute.equals(Attributes.MOVEMENT_SPEED)) {
                    continue;
                }

                AttributeInstance instance = player.getAttribute(attribute);
                if (instance == null) continue;

                AttributeModifier.Operation globalOperation;
                if (instance.getBaseValue() == 0.0) {
                    globalOperation = AttributeModifier.Operation.ADD_VALUE;
                } else {
                    globalOperation = AttributeModifier.Operation.ADD_MULTIPLIED_BASE;
                }

                AttributeModifier globalModifier = new AttributeModifier(
                        GLOBAL_BONUS_ID,
                        globalBonus,
                        globalOperation
                );

                instance.addPermanentModifier(globalModifier);
            }
        }
    }
}