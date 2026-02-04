package org.mob.craftcards;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.mob.craftcards.item.ModItems;

import java.util.List;

public class ModItemGroups {

    public static final ItemGroup CRAFTCARDS_GROUP = Registry.register(
            Registries.ITEM_GROUP,
            Identifier.of(CraftCards.MOD_ID, "craftcards_group"),
            new ItemGroup.Builder(ItemGroup.Row.TOP, 0)
                    .icon(() -> new ItemStack(ModItems.CARD_CASE))
                    .displayName(Text.translatable("itemGroup.craftcards.craftcards_group"))
                    .entries((context, entries) -> {

                        entries.add(ModItems.CARD_CASE);
                        entries.add(ModItems.FIRE_RESISTANCE);
                        entries.add(ModItems.WATER_BREATHING);

                        addAll(entries, ModItems.ARMOR);
                        addAll(entries, ModItems.ARMOR_TOUGHNESS);
                        addAll(entries, ModItems.HEALTH_BOOST);
                        addAll(entries, ModItems.REGENERATION);
                        addAll(entries, ModItems.DAMAGE);
                        addAll(entries, ModItems.ATTACK_SPEED);
                        addAll(entries, ModItems.LUCK_BOOST);

                        addAll(entries, ModItems.SPEED_BOOST);
                        addAll(entries, ModItems.SNEAK_SPEED);
                        addAll(entries, ModItems.JUMP_BOOST);
                        addAll(entries, ModItems.STEP_HEIGHT);
                        addAll(entries, ModItems.FEATHER_FALLING);

                        addAll(entries, ModItems.SIZE_UP);
                        addAll(entries, ModItems.SIZE_DOWN);

                        addAll(entries, ModItems.MINING_SPEED);
                        addAll(entries, ModItems.UNDERWATER_MINING_SPEED);

                        addAll(entries, ModItems.BLOCK_INTERACTION_RANGE);
                        addAll(entries, ModItems.ENTITY_INTERACTION_RANGE);

                        addAll(entries, ModItems.SHINY_RATE);
                        addAll(entries, ModItems.CAPTURE_RATE);
                        addAll(entries, ModItems.FORTUNE);
                        addAll(entries, ModItems.LOOTING);
                    })
                    .build()
    );

    private static void addAll(ItemGroup.Entries entries, List<Item> items) {
        for (var item : items) {
            entries.add(item);
        }
    }

    public static void register() {
        CraftCards.LOGGER.info("Registered vanilla item group");
    }
}
