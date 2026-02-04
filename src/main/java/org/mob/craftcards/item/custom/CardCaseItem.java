package org.mob.craftcards.item.custom;

import io.wispforest.accessories.api.Accessory;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.mob.craftcards.CraftCards;
import org.mob.craftcards.CraftCardsClient;
import org.mob.craftcards.attribute.CardCaseAttributeHandler;
import org.mob.craftcards.screen.CardCaseScreenHandler;
import org.mob.craftcards.util.CardCaseInventory;

import java.util.List;

public class CardCaseItem extends Item implements Accessory {

    public CardCaseItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient) {
            user.openHandledScreen(new NamedScreenHandlerFactory() {
                @Override
                public ScreenHandler createMenu(int syncId, net.minecraft.entity.player.PlayerInventory playerInventory, PlayerEntity player) {
                    return new CardCaseScreenHandler(syncId, playerInventory, new CardCaseInventory(stack, CraftCards.CASE_SIZE));
                }

                @Override
                public Text getDisplayName() {
                    return Text.translatable("item.craftcards.card_case");
                }
            });
        }

        return TypedActionResult.success(stack, world.isClient());
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        double bonusValue = CardCaseAttributeHandler.calculateBonusFromStack(stack);
        int percent = (int) (bonusValue * 100);
        tooltip.add(Text.translatable("tooltip.craftcards.global_bonus", percent).formatted(Formatting.GOLD,Formatting.BOLD));

        // FabricLoader check ensures the server doesn't try to access Client-only KeyBindings
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            addClientTooltip(tooltip);
        }
    }

    // Separate method to prevent the ClassLoader from seeing KeyBinding on the server
    private void addClientTooltip(List<Text> tooltip) {
        Text keyName = CraftCardsClient.openCardCaseKey.getBoundKeyLocalizedText();
        tooltip.add(Text.translatable("tooltip.craftcards.open_key", keyName).formatted(Formatting.GOLD));
    }

}