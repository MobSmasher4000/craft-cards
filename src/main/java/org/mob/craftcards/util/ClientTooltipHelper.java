package org.mob.craftcards.util;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import java.util.List;

public class ClientTooltipHelper {

    public static void appendCooldownTooltip(Item item, List<Component> tooltipComponents, int cooldownTicks) {
        Player player = Minecraft.getInstance().player;

        if (player != null) {
            int cooldownSeconds = cooldownTicks /20;
            if (player.getCooldowns().isOnCooldown(item)) {

                // Calculate percentage
                float percent = player.getCooldowns().getCooldownPercent(item, 0.0F);

                // Assuming 60 seconds (1200 ticks) is your max.
                // ideally pass the max ticks as a parameter to this method for flexibility!
                int presecondsLeft = (int) (percent * cooldownTicks);
                int secondsLeft = presecondsLeft / 20;

                tooltipComponents.add(Component.translatable("tooltip.craftcards.cooldown_active", secondsLeft)
                        .withStyle(ChatFormatting.RED));
            } else {
                tooltipComponents.add(Component.translatable("tooltip.craftcards.cooldown_ready", cooldownSeconds)
                        .withStyle(ChatFormatting.GREEN));
            }
        }
    }
}