package org.mob.craftcards.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class ClientTooltipHelper {

    public static void appendCooldownTooltip(Item item, List<Text> tooltipComponents, int cooldownTicks) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        if (player != null) {
            int cooldownSeconds = cooldownTicks / 20;

            if (player.getItemCooldownManager().isCoolingDown(item)) {

                float percent = player.getItemCooldownManager().getCooldownProgress(item, 0.0F);

                int presecondsLeft = (int) (percent * cooldownTicks);
                int secondsLeft = presecondsLeft / 20;

                tooltipComponents.add(Text.translatable("tooltip.craftcards.cooldown_active", secondsLeft)
                        .formatted(Formatting.RED));
            } else {
                tooltipComponents.add(Text.translatable("tooltip.craftcards.cooldown_ready", cooldownSeconds)
                        .formatted(Formatting.GREEN));
            }
        }
    }
}