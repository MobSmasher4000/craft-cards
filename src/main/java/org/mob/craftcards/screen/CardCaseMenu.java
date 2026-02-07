package org.mob.craftcards.screen;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.mob.craftcards.CraftCards;
import org.mob.craftcards.inventory.CardSlot;
import org.mob.craftcards.util.ModTags;

public class CardCaseMenu extends AbstractContainerMenu {
    public static final int CASE_SIZE = CraftCards.CASE_SIZE;
    private static final int SLOT_SIZE = 18;
    private static final int PLAYER_INV_START_X = 8;
    private static final int PLAYER_INV_START_Y = 112;
    private static final int HOTBAR_START_Y = 170;

    private final Container caseInventory;

    public CardCaseMenu(int syncId, Inventory playerInventory, FriendlyByteBuf friendlyByteBuf) {
        this(syncId, playerInventory, new SimpleContainer(CASE_SIZE));
    }

    public CardCaseMenu(int syncId, Inventory playerInventory, Container caseInventory) {
        super(ModMenuTypes.CARD_CASE.get(), syncId);

        checkContainerSize(caseInventory, CASE_SIZE);
        this.caseInventory = caseInventory;

        caseInventory.startOpen(playerInventory.player);

        // Add Card Slots
        this.addSlot(new CardSlot(caseInventory, 0, 12, 15, ModTags.LUCK_BOOST));
        this.addSlot(new CardSlot(caseInventory, 1, 37, 15, ModTags.MINING_SPEED));
        this.addSlot(new CardSlot(caseInventory, 2, 61, 15, ModTags.UNDERWATER_MINING_SPEED));
        this.addSlot(new CardSlot(caseInventory, 3, 107, 15, ModTags.HEALTH_BOOST));
        this.addSlot(new CardSlot(caseInventory, 4, 130, 15, ModTags.REGENERATION));
        this.addSlot(new CardSlot(caseInventory, 5, 151, 15, ModTags.WATER_BREATHING));

        this.addSlot(new CardSlot(caseInventory, 6, 12, 37, ModTags.SIZE_DOWN));
        this.addSlot(new CardSlot(caseInventory, 7, 37, 37, ModTags.SIZE_UP));
        this.addSlot(new CardSlot(caseInventory, 8, 61, 37, ModTags.DAMAGE));
        this.addSlot(new CardSlot(caseInventory, 9, 107, 37, ModTags.ATTACK_SPEED));
        this.addSlot(new CardSlot(caseInventory, 10, 130, 37, ModTags.SNEAK_SPEED));
        this.addSlot(new CardSlot(caseInventory, 11, 151, 37, ModTags.STEP_HEIGHT));

        this.addSlot(new CardSlot(caseInventory, 12, 12, 61, ModTags.BLOCK_INTERACTION_RANGE));
        this.addSlot(new CardSlot(caseInventory, 13, 37, 61, ModTags.ENTITY_INTERACTION_RANGE));
        this.addSlot(new CardSlot(caseInventory, 14, 61, 61, ModTags.SPEED_BOOST));
        this.addSlot(new CardSlot(caseInventory, 15, 107, 61, ModTags.JUMP_BOOST));
        this.addSlot(new CardSlot(caseInventory, 16, 130, 61, ModTags.FEATHER_FALLING));
        this.addSlot(new CardSlot(caseInventory, 17, 151, 61, ModTags.ARMOR_TOUGHNESS));

        this.addSlot(new CardSlot(caseInventory, 18, 12, 83, ModTags.ARMOR));
        this.addSlot(new CardSlot(caseInventory, 19, 37, 83, ModTags.FIRE_RESISTANCE));
        this.addSlot(new CardSlot(caseInventory, 20, 61, 83, ModTags.SHINY_RATE));
        this.addSlot(new CardSlot(caseInventory, 21, 107, 83, ModTags.CAPTURE_RATE));
        this.addSlot(new CardSlot(caseInventory, 22, 130, 83, ModTags.FORTUNE));
        this.addSlot(new CardSlot(caseInventory, 23, 151, 83, ModTags.LOOTING));

        this.addSlot(new CardSlot(caseInventory, 24, 85, 49, ModTags.FLIGHT));

        // PLAYER INVENTORY
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int x = PLAYER_INV_START_X + col * SLOT_SIZE;
                int y = PLAYER_INV_START_Y + row * SLOT_SIZE;
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, x, y));
            }
        }

        // PLAYER HOTBAR
        for (int col = 0; col < 9; col++) {
            int x = PLAYER_INV_START_X + col * SLOT_SIZE;
            int y = HOTBAR_START_Y;
            this.addSlot(new Slot(playerInventory, col, x, y));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return caseInventory.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        caseInventory.stopOpen(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack original = slot.getItem();
            newStack = original.copy();

            int caseEnd = CASE_SIZE;
            int totalSlots = this.slots.size();

            if (index < caseEnd) {
                // from case -> player
                // moveItemStackTo = insertItem
                if (!this.moveItemStackTo(original, caseEnd, totalSlots, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // from player -> case
                if (!this.moveItemStackTo(original, 0, caseEnd, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (original.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return newStack;
    }
}