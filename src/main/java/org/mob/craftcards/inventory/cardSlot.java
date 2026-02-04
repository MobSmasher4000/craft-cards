package org.mob.craftcards.inventory;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.slot.Slot;

public class cardSlot extends Slot {
    TagKey<Item> Tag;
    public cardSlot(Inventory inventory, int index, int x, int y, TagKey<Item> tag) {
        super(inventory, index, x, y);
        this.Tag = tag;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return stack.isIn(Tag);
    }

    @Override
    public int getMaxItemCount(ItemStack stack) {
        return 1;
    }
}
