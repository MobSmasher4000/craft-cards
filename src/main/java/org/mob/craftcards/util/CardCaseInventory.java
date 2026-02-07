package org.mob.craftcards.util;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

public class CardCaseInventory implements Container {

    private final ItemStack ownerStack;
    private final NonNullList<ItemStack> stacks;

    public CardCaseInventory(ItemStack ownerStack, int size) {
        this.ownerStack = ownerStack;
        this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
        loadFromStack();
    }

    private void loadFromStack() {
        ItemContainerContents contents = ownerStack.get(DataComponents.CONTAINER);
        if (contents != null) {
            contents.copyInto(stacks);
        }
    }

    private void saveToStack() {
        ItemContainerContents contents = ItemContainerContents.fromItems(stacks);
        ownerStack.set(DataComponents.CONTAINER, contents);
    }

    @Override
    public void setChanged() {
        saveToStack();
    }

    @Override
    public int getContainerSize() {
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
    public ItemStack getItem(int slot) { // getStack -> getItem
        return stacks.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = ContainerHelper.removeItem(this.stacks, slot, amount);
        if (!result.isEmpty()) {
            setChanged();
        }
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack result = ContainerHelper.takeItem(this.stacks, slot);
        if (!result.isEmpty()) {
            setChanged();
        }
        return result;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        stacks.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        setChanged();
    }

    @Override
    public void clearContent() {
        stacks.clear();
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override public void startOpen(Player player) {}
    @Override public void stopOpen(Player player) {}
}