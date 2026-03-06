package org.mob.craftcards.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.mob.craftcards.CraftCards;
import org.mob.craftcards.inventory.cardSlot;
import org.mob.craftcards.util.ModTags;

public class CardCaseScreenHandler extends ScreenHandler {
    public static final int CASE_SIZE = CraftCards.CASE_SIZE;
    private static final int SLOT_SIZE = 18;
    private static final int PLAYER_INV_START_X = 8;
    private static final int PLAYER_INV_START_Y = 112;
    private static final int HOTBAR_START_Y = 170;

    private final Inventory caseInventory;

    // Client-side
    public CardCaseScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(CASE_SIZE));
    }

    // Server-side
    public CardCaseScreenHandler(int syncId, PlayerInventory playerInventory, Inventory caseInventory) {
        super(ModScreenHandlers.CARD_CASE, syncId);
        this.caseInventory = caseInventory;

        caseInventory.onOpen(playerInventory.player);

        this.addSlot(new cardSlot(caseInventory, 0, 14, 15, ModTags.LUCK_BOOST));
        this.addSlot(new cardSlot(caseInventory, 1, 41, 15, ModTags.MINING_SPEED));
        this.addSlot(new cardSlot(caseInventory, 2, 68, 15, ModTags.UNDERWATER_MINING_SPEED));
        this.addSlot(new cardSlot(caseInventory, 3, 94, 15, ModTags.HEALTH_BOOST));
        this.addSlot(new cardSlot(caseInventory, 4, 121, 15, ModTags.REGENERATION));
        this.addSlot(new cardSlot(caseInventory, 5, 148, 15, ModTags.WATER_BREATHING));

        this.addSlot(new cardSlot(caseInventory, 6, 14, 37, ModTags.SIZE_DOWN));
        this.addSlot(new cardSlot(caseInventory, 7, 41, 37, ModTags.SIZE_UP));
        this.addSlot(new cardSlot(caseInventory, 8, 68, 37, ModTags.DAMAGE));
        this.addSlot(new cardSlot(caseInventory, 9, 94, 37, ModTags.ATTACK_SPEED));
        this.addSlot(new cardSlot(caseInventory, 10, 121, 37, ModTags.SNEAK_SPEED));
        this.addSlot(new cardSlot(caseInventory, 11, 148, 37, ModTags.STEP_HEIGHT));

        this.addSlot(new cardSlot(caseInventory, 12, 14, 61, ModTags.BLOCK_INTERACTION_RANGE));
        this.addSlot(new cardSlot(caseInventory, 13, 41, 61, ModTags.ENTITY_INTERACTION_RANGE));
        this.addSlot(new cardSlot(caseInventory, 14, 68, 61, ModTags.SPEED_BOOST));
        this.addSlot(new cardSlot(caseInventory, 15, 94, 61, ModTags.JUMP_BOOST));
        this.addSlot(new cardSlot(caseInventory, 16, 121, 61, ModTags.FEATHER_FALLING));
        this.addSlot(new cardSlot(caseInventory, 17, 148, 61, ModTags.ARMOR_TOUGHNESS));

        this.addSlot(new cardSlot(caseInventory, 18, 14, 83, ModTags.ARMOR));
        this.addSlot(new cardSlot(caseInventory, 19, 41, 83, ModTags.FIRE_RESISTANCE));
        this.addSlot(new cardSlot(caseInventory, 20, 68, 83, ModTags.SHINY_RATE));
        this.addSlot(new cardSlot(caseInventory, 21, 94, 83, ModTags.CAPTURE_RATE));
        this.addSlot(new cardSlot(caseInventory, 22, 121, 83, ModTags.FORTUNE));
        this.addSlot(new cardSlot(caseInventory, 23, 148, 83, ModTags.LOOTING));

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
    public boolean canUse(PlayerEntity player) {
        return caseInventory.canPlayerUse(player);
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        caseInventory.onClose(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack original = slot.getStack();
            newStack = original.copy();

            int caseEnd = CASE_SIZE;
            int totalSlots = this.slots.size();

            if (index < caseEnd) {
                // from case -> player
                if (!this.insertItem(original, caseEnd, totalSlots, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // from player -> case (respects per-slot canInsert())
                if (!this.insertItem(original, 0, caseEnd, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (original.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return newStack;
    }
}
