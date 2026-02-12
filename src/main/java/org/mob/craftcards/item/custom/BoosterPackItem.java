package org.mob.craftcards.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.mob.craftcards.util.ModTags;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import org.mob.craftcards.util.ClientTooltipHelper;

import java.util.List;
import java.util.Optional;

public class BoosterPackItem extends Item {

    // How many times can you use this pack?
    private static final int MAX_USES = 5;

    // Cooldown in ticks
    // (hour * minutes * seconds * ticks per second)
    // (72,000 ticks)
    private static final int COOLDOWN_TICKS = 1 * 60 * 60 * 20;

    public BoosterPackItem(Properties properties) {
        super(properties.durability(MAX_USES).stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        // Server-Side Check
        if (!level.isClientSide) {

            Optional<HolderSet.Named<Item>> optionalTag = BuiltInRegistries.ITEM.getTag(ModTags.LOOT_CARD_ITEMS);

            if (optionalTag.isPresent() && optionalTag.get().size() > 0) {
                List<Holder<Item>> allCards = optionalTag.get().stream().toList();

                // Give 2-4 cards
                int cardsToGive = 2 + level.random.nextInt(3);

                for (int i = 0; i < cardsToGive; i++) {
                    Holder<Item> randomCard = allCards.get(level.random.nextInt(allCards.size()));
                    ItemStack cardStack = new ItemStack(randomCard);

                    if (!player.getInventory().add(cardStack)) {
                        player.drop(cardStack, false);
                    }
                }

                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.BUNDLE_DROP_CONTENTS, SoundSource.PLAYERS, 1.0F, 1.0F);

                // Apply Cooldown
                player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);

                // Damage the Item (Reduce uses)
                itemStack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);

                return InteractionResultHolder.success(itemStack);
            }
        }

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        // Description
        tooltipComponents.add(Component.translatable("tooltip.craftcards.booster_pack_desc")
                .withStyle(ChatFormatting.GRAY));

        // Uses Remaining Tooltip
        int remaining = stack.getMaxDamage() - stack.getDamageValue();
        tooltipComponents.add(Component.translatable("tooltip.craftcards.booster_pack_uses", remaining)
                .withStyle(ChatFormatting.BLUE));

        // Cooldown Tooltip
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientTooltipHelper.appendCooldownTooltip(this, tooltipComponents, COOLDOWN_TICKS);
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}