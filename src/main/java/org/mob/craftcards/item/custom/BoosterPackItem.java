package org.mob.craftcards.item.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.mob.craftcards.util.ClientTooltipHelper;
import org.mob.craftcards.util.ModTags;

import java.util.List;
import java.util.Optional;

public class BoosterPackItem extends Item {

    private static final int MAX_USES = 5;

    // Cooldown in ticks
    // (hour * minutes * seconds * ticks per second)
    // (72,000 ticks)
    private static final int COOLDOWN_TICKS = 60 * 60 * 20;

    public BoosterPackItem(Settings settings) {
        super(settings.maxDamage(MAX_USES).maxCount(1));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        // Server-Side Check
        if (!world.isClient) {

            Optional<RegistryEntryList.Named<Item>> optionalTag = Registries.ITEM.getEntryList(ModTags.LOOT_CARD_ITEMS);

            if (optionalTag.isPresent() && optionalTag.get().size() > 0) {
                // Holder<Item> -> RegistryEntry<Item>
                List<RegistryEntry<Item>> allCards = optionalTag.get().stream().toList();

                // Give 2-4 cards
                int cardsToGive = 2 + world.random.nextInt(3);

                for (int i = 0; i < cardsToGive; i++) {
                    RegistryEntry<Item> randomCard = allCards.get(world.random.nextInt(allCards.size()));
                    ItemStack cardStack = new ItemStack(randomCard);

                    // player.getInventory().add() -> user.getInventory().insertStack()
                    if (!user.getInventory().insertStack(cardStack)) {
                        user.dropItem(cardStack, false);
                    }
                }

                // SoundSource -> SoundCategory
                world.playSound(null, user.getX(), user.getY(), user.getZ(),
                        SoundEvents.ITEM_BUNDLE_DROP_CONTENTS, SoundCategory.PLAYERS, 1.0F, 1.0F);

                // player.getCooldowns().addCooldown() -> user.getItemCooldownManager().set()
                user.getItemCooldownManager().set(this, COOLDOWN_TICKS);

                // hurtAndBreak -> damage
                itemStack.damage(1, user, EquipmentSlot.MAINHAND);

                return TypedActionResult.success(itemStack);
            }
        }

        // sidedSuccess -> success(stack, world.isClient())
        return TypedActionResult.success(itemStack, world.isClient());
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltipComponents, TooltipType tooltipType) {
        // Description
        tooltipComponents.add(Text.translatable("tooltip.craftcards.booster_pack_desc")
                .formatted(Formatting.GRAY));

        // Uses Remaining Tooltip
        int remaining = stack.getMaxDamage() - stack.getDamage();
        tooltipComponents.add(Text.translatable("tooltip.craftcards.booster_pack_uses", remaining)
                .formatted(Formatting.BLUE));

        // NeoForge FMLEnvironment.dist -> FabricLoader.getInstance().getEnvironmentType()
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ClientTooltipHelper.appendCooldownTooltip(this, tooltipComponents, COOLDOWN_TICKS);
        }

        super.appendTooltip(stack, context, tooltipComponents, tooltipType);
    }
}