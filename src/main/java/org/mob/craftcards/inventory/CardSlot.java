package org.mob.craftcards.inventory;


import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CardSlot extends Slot {
    TagKey<Item> Tag;

    public CardSlot(Container inventory, int index, int x, int y, TagKey<Item> tag) {
        super(inventory, index, x, y);
        this.Tag = tag;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.is(Tag);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }
}
