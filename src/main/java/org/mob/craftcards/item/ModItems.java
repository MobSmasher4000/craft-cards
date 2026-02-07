package org.mob.craftcards.item;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.mob.craftcards.CraftCards;
import org.mob.craftcards.component.ModDataComponentTypes;
import org.mob.craftcards.helper.Tier;
import org.mob.craftcards.item.custom.CardCaseItem;
import org.mob.craftcards.item.custom.TieredCardItem;

import java.util.ArrayList;
import java.util.List;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CraftCards.MOD_ID);

    public static final DeferredItem<Item> CARD_CASE = ITEMS.register("card_case",()-> new CardCaseItem(new Item.Properties()));
    public static final DeferredItem<Item> FIRE_RESISTANCE = ITEMS.register("fire_resistance",()-> new Item(new Item.Properties()){
        @Override
        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(Component.translatable("tooltip.craftcards.fire_water_effect"));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        }
    });
    public static final DeferredItem<Item> WATER_BREATHING = ITEMS.register("water_breathing",()-> new Item(new Item.Properties()){
        @Override
        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(Component.translatable("tooltip.craftcards.fire_water_effect"));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        }
    });
    public static final DeferredItem<Item> FLIGHT = ITEMS.register("flight",()-> new Item(new Item.Properties()){
        @Override
        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(Component.translatable("tooltip.craftcards.flight"));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        }
    });

    public static final List<DeferredItem<Item>> ARMOR = registerTier("armor");
    public static final List<DeferredItem<Item>> ARMOR_TOUGHNESS = registerTier("armor_toughness");
    public static final List<DeferredItem<Item>> HEALTH_BOOST = registerTier("health_boost");
    public static final List<DeferredItem<Item>> REGENERATION = registerTier("regeneration");
    public static final List<DeferredItem<Item>> DAMAGE = registerTier("damage");
    public static final List<DeferredItem<Item>> ATTACK_SPEED = registerTier("attack_speed");
    public static final List<DeferredItem<Item>> SIZE_UP = registerTier("size_up");
    public static final List<DeferredItem<Item>> SIZE_DOWN = registerTier("size_down");
    public static final List<DeferredItem<Item>> MINING_SPEED = registerTier("mining_speed");
    public static final List<DeferredItem<Item>> UNDERWATER_MINING_SPEED = registerTier("underwater_mining_speed");
    public static final List<DeferredItem<Item>> JUMP_BOOST = registerTier("jump_boost");
    public static final List<DeferredItem<Item>> BLOCK_INTERACTION_RANGE = registerTier("block_interaction_range");
    public static final List<DeferredItem<Item>> ENTITY_INTERACTION_RANGE = registerTier("entity_interaction_range");
    public static final List<DeferredItem<Item>> LUCK_BOOST = registerTier("luck_boost");
    public static final List<DeferredItem<Item>> STEP_HEIGHT = registerTier("step_height");
    public static final List<DeferredItem<Item>> FEATHER_FALLING = registerTier("feather_falling");
    public static final List<DeferredItem<Item>> SPEED_BOOST = registerTier("speed_boost");
    public static final List<DeferredItem<Item>> SNEAK_SPEED = registerTier("sneak_speed");
    public static final List<DeferredItem<Item>> SHINY_RATE = registerTier("shiny_rate");
    public static final List<DeferredItem<Item>> CAPTURE_RATE = registerTier("capture_rate");
    public static final List<DeferredItem<Item>> FORTUNE = registerTier("fortune");
    public static final List<DeferredItem<Item>> LOOTING = registerTier("looting");

    /**
     * Registers a set of tiered items (T0 to T6) based on a base name.
     * Each item is registered with its corresponding ComponentType and Tier ordinal value.
     * @param baseName The base identifier name for the card (e.g., "damage_card").
     * @return A List of the registered Item instances.
     */
    private static List<DeferredItem<Item>> registerTier(String baseName){
        List<DeferredItem<Item>> items = new ArrayList<>();

        for (Tier tier : Tier.values()) {
            // 1. Get the correct component type from the ModDataComponentTypes.TIER_COMPONENTS array
            //    using the tier's ordinal (0 for T0, 1 for T1, etc.).
            DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> componentType = ModDataComponentTypes.TIER_COMPONENTS[tier.ordinal()];

            // 2. Set the component value to the tier's ordinal. This value is used by the component.
            int componentValue = tier.ordinal();

            // 3. Construct the full item name (e.g., "armor_tier0") using the Tier's suffix.
            String fullName = baseName + tier.getIdSuffix();

            // 4. Create and register the item, applying the component type and value in the settings.
            DeferredItem<Item> registeredItem = ITEMS.register(
                    fullName,
                    () -> new TieredCardItem(new Item.Properties().component(componentType, componentValue))
            );

            items.add(registeredItem);
        }
        return items;
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
