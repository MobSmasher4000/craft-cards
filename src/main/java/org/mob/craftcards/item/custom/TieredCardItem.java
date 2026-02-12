package org.mob.craftcards.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.mob.craftcards.helper.Tier;
import org.mob.craftcards.util.ModTags;

import java.util.List;

public class TieredCardItem extends Item {

    public TieredCardItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack,
                                TooltipContext context,
                                List<Component> tooltip,
                                TooltipFlag tooltipFlag) {

        // Get Tier
        Tier tier = Tier.tierFromItem(stack.getItem());


        if (stack.is(ModTags.REGENERATION)){
            int effectAmp = Tier.tierFromItem(stack.getItem()).ordinal() + 1;
            tooltip.add(Component.translatable("tooltip.craftcards.regeneration",effectAmp).withStyle(ChatFormatting.AQUA));
            return;
        }

        if (stack.is(ModTags.FORTUNE)){
            int levelAmp = Tier.tierFromItem(stack.getItem()).ordinal() + 1;
            tooltip.add(Component.translatable("tooltip.craftcards.fortune", levelAmp).withStyle(ChatFormatting.AQUA));
            return;
        }

        if (stack.is(ModTags.LOOTING)){
            int levelAmp = Tier.tierFromItem(stack.getItem()).ordinal() + 1;
            tooltip.add(Component.translatable("tooltip.craftcards.looting", levelAmp).withStyle(ChatFormatting.AQUA));
            return;
        }

        // Calculate Percent (0.20f -> 20)
        int percent = Math.round(tier.getBonus() * 100.0f);

        // 4. Get Stat ID (e.g., "armor", "speed_boost")
        String path = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
        String statId = path;

        int idx = path.indexOf("_tier");
        if (idx != -1) {
            statId = path.substring(0, idx); // "armor_tier3" -> "armor"
        }

        MutableComponent statName = Component.translatable("stat.craftcards." + statId);

        tooltip.add(
                Component.translatable("tooltip.craftcards.stat_bonus", percent, statName)
                        .withStyle(ChatFormatting.AQUA)
        );
    }
}