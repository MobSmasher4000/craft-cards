package org.mob.craftcards.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.mob.craftcards.CraftCards;

import java.util.List;

public class ModItemGroups {

    // 1. Create the DeferredRegister for Creative Tabs
    public static final DeferredRegister<CreativeModeTab> REGISTRY =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CraftCards.MOD_ID);

    // 2. Register the Tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CRAFTCARDS_GROUP = REGISTRY.register("craftcards_group", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.craftcards.craftcards_group"))
            .icon(() -> new ItemStack(ModItems.CARD_CASE.get()))
            .displayItems((parameters, output) -> {

                // Add single items
                output.accept(ModItems.CARD_CASE.get());
                output.accept(ModItems.FLIGHT.get());
                output.accept(ModItems.FIRE_RESISTANCE.get());
                output.accept(ModItems.WATER_BREATHING.get());

                // Add lists of items using the helper
                addAll(output, ModItems.ARMOR);
                addAll(output, ModItems.ARMOR_TOUGHNESS);
                addAll(output, ModItems.HEALTH_BOOST);
                addAll(output, ModItems.REGENERATION);
                addAll(output, ModItems.DAMAGE);
                addAll(output, ModItems.ATTACK_SPEED);
                addAll(output, ModItems.LUCK_BOOST);

                addAll(output, ModItems.SPEED_BOOST);
                addAll(output, ModItems.SNEAK_SPEED);
                addAll(output, ModItems.JUMP_BOOST);
                addAll(output, ModItems.STEP_HEIGHT);
                addAll(output, ModItems.FEATHER_FALLING);

                addAll(output, ModItems.SIZE_UP);
                addAll(output, ModItems.SIZE_DOWN);

                addAll(output, ModItems.MINING_SPEED);
                addAll(output, ModItems.UNDERWATER_MINING_SPEED);

                addAll(output, ModItems.BLOCK_INTERACTION_RANGE);
                addAll(output, ModItems.ENTITY_INTERACTION_RANGE);

                addAll(output, ModItems.SHINY_RATE);
                addAll(output, ModItems.CAPTURE_RATE);
                addAll(output, ModItems.FORTUNE);
                addAll(output, ModItems.LOOTING);
            })
            .build());

    // Helper method
    private static void addAll(CreativeModeTab.Output output, List<? extends ItemLike> items) {
        for (ItemLike item : items) {
            output.accept(item);
        }
    }

    public static void register(IEventBus eventBus){
        REGISTRY.register(eventBus);
    }
}