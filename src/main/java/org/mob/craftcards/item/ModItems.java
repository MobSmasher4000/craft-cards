package org.mob.craftcards.item;

import net.minecraft.component.ComponentType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.mob.craftcards.CraftCards;
import org.mob.craftcards.component.ModDataComponentTypes;
import org.mob.craftcards.helper.Tier;
import org.mob.craftcards.item.custom.CardCaseItem;
import org.mob.craftcards.item.custom.TieredCardItem;

import java.util.ArrayList;
import java.util.List;

public class ModItems {
    public static final Item CARD_CASE = register("card_case", new CardCaseItem(new Item.Settings().maxCount(1)));
    public static final Item FIRE_RESISTANCE = register("fire_resistance", new Item(new Item.Settings()));
    public static final Item WATER_BREATHING = register("water_breathing", new Item(new Item.Settings()));

    public static final List<Item> ARMOR = registerTier("armor");
    public static final List<Item> ARMOR_TOUGHNESS = registerTier("armor_toughness");
    public static final List<Item> HEALTH_BOOST = registerTier("health_boost");
    public static final List<Item> REGENERATION = registerTier("regeneration");
    public static final List<Item> DAMAGE = registerTier("damage");
    public static final List<Item> ATTACK_SPEED = registerTier("attack_speed");
    public static final List<Item> SIZE_UP = registerTier("size_up");
    public static final List<Item> SIZE_DOWN = registerTier("size_down");
    public static final List<Item> MINING_SPEED = registerTier("mining_speed");
    public static final List<Item> UNDERWATER_MINING_SPEED = registerTier("underwater_mining_speed");
    public static final List<Item> JUMP_BOOST = registerTier("jump_boost");
    public static final List<Item> BLOCK_INTERACTION_RANGE = registerTier("block_interaction_range");
    public static final List<Item> ENTITY_INTERACTION_RANGE = registerTier("entity_interaction_range");
    public static final List<Item> LUCK_BOOST = registerTier("luck_boost");
    public static final List<Item> STEP_HEIGHT = registerTier("step_height");
    public static final List<Item> FEATHER_FALLING = registerTier("feather_falling");
    public static final List<Item> SPEED_BOOST = registerTier("speed_boost");
    public static final List<Item> SNEAK_SPEED = registerTier("sneak_speed");
    public static final List<Item> SHINY_RATE = registerTier("shiny_rate");
    public static final List<Item> CAPTURE_RATE = registerTier("capture_rate");
    public static final List<Item> FORTUNE = registerTier("fortune");
    public static final List<Item> LOOTING = registerTier("looting");


    public static void registerAll() {
        CraftCards.LOGGER.info("Registering Items for " + CraftCards.MOD_ID);
    }

    private static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(CraftCards.MOD_ID, name), item);
    }

    /**
     * Registers a set of tiered items (T0 to T6) based on a base name.
     * Each item is registered with its corresponding ComponentType and Tier ordinal value.
     * @param baseName The base identifier name for the card (e.g., "damage_card").
     * @return A List of the registered Item instances.
     */
    private static List<Item> registerTier(String baseName){
        List<Item> items = new ArrayList<>();

        for (Tier tier : Tier.values()) {
            // 1. Get the correct component type from the ModDataComponentTypes.TIER_COMPONENTS array
            //    using the tier's ordinal (0 for T0, 1 for T1, etc.).
            ComponentType<Integer> componentType = ModDataComponentTypes.TIER_COMPONENTS[tier.ordinal()];

            // 2. Set the component value to the tier's ordinal. This value is used by the component.
            int componentValue = tier.ordinal();

            // 3. Construct the full item name (e.g., "armor_tier0") using the Tier's suffix.
            String fullName = baseName + tier.getIdSuffix();

            // 4. Create and register the item, applying the component type and value in the settings.
            Item registeredItem = register(
                    fullName,
                    new TieredCardItem(new Item.Settings().component(componentType, componentValue))
            );

            items.add(registeredItem);
        }
        return items;
    }
}
