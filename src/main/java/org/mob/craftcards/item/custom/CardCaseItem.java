package org.mob.craftcards.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.mob.craftcards.CraftCards;
import org.mob.craftcards.attribute.CardCaseAttributeHandler;
import org.mob.craftcards.attribute.CardCaseEffectHandler;
import org.mob.craftcards.event.ClientAccess;
import org.mob.craftcards.screen.CardCaseMenu;
import org.mob.craftcards.util.CardCaseInventory;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class CardCaseItem extends Item implements ICurioItem {

    public CardCaseItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity() instanceof Player player && !player.level().isClientSide) {
            CardCaseAttributeHandler.updatePlayer(player, stack);
            CardCaseEffectHandler.updatePlayer(player, stack);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (slotContext.entity() instanceof Player player && !player.level().isClientSide) {
            // Cleanup when removed
            CardCaseAttributeHandler.updatePlayer(player, ItemStack.EMPTY);
            CardCaseEffectHandler.updatePlayer(player, ItemStack.EMPTY);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
        double bonusValue = CardCaseAttributeHandler.calculateBonusFromStack(stack);
        int percent = (int) (bonusValue * 100);
        tooltip.add(Component.translatable("tooltip.craftcards.global_bonus", percent).withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));

        // Client-Side Only Check
        if (context.level() != null && context.level().isClientSide) {
              addClientTooltip(tooltip);
        }
    }

    private void addClientTooltip(List<Component> tooltip) {
        String keyName = ClientAccess.openCardCaseKey.getKey().getDisplayName().getString();
        tooltip.add(Component.translatable("tooltip.craftcards.open_key", keyName).withStyle(ChatFormatting.GOLD));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            openMenu(serverPlayer, stack);
        }

        return InteractionResultHolder.success(stack);
    }

    // Helper to open the menu (used by PacketHandler too)
    public static void openMenu(ServerPlayer player, ItemStack stack) {
        MenuProvider containerProvider = new SimpleMenuProvider(
                (syncId, playerInventory, p) -> new CardCaseMenu(
                        syncId,
                        playerInventory,
                        new CardCaseInventory(stack, CraftCards.CASE_SIZE)
                ),
                Component.translatable("item.craftcards.card_case")
        );
        player.openMenu(containerProvider);
    }
}