package org.mob.craftcards.util;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class CardCaseInventory implements Inventory {

    private final ItemStack ownerStack;
    private final DefaultedList<ItemStack> stacks;

    public CardCaseInventory(ItemStack ownerStack, int size) {
        this.ownerStack = ownerStack;
        this.stacks = DefaultedList.ofSize(size, ItemStack.EMPTY);
        loadFromStack();
    }

    private void loadFromStack() {
        // Read container component from the card case item
        ContainerComponent component = ownerStack.get(DataComponentTypes.CONTAINER);
        if (component != null) {
            component.copyTo(stacks); // fills our DefaultedList
        }
    }

    private void saveToStack() {
        // Write back into the card case item as a container component
        ContainerComponent component = ContainerComponent.fromStacks(stacks);
        ownerStack.set(DataComponentTypes.CONTAINER, component);
    }

    @Override
    public void markDirty() {
        saveToStack();
    }

    @Override
    public int size() {
        return stacks.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return stacks.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack existing = stacks.get(slot);

        if (existing.isEmpty()) return ItemStack.EMPTY;

        ItemStack result = existing.split(amount);
        markDirty();
        return result;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack result = stacks.get(slot);
        if (!result.isEmpty()) {
            stacks.set(slot, ItemStack.EMPTY);
            markDirty();
        }
        return result;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        stacks.set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack()) {
            stack.setCount(getMaxCountPerStack());
        }
        markDirty();
    }

    @Override
    public void clear() {
        stacks.clear();
        markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    // All stacks in this inventory are 1 max
    @Override
    public int getMaxCountPerStack() {
        return 1;
    }
}
