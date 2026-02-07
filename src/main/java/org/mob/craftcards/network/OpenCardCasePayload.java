package org.mob.craftcards.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.mob.craftcards.CraftCards;

public record OpenCardCasePayload() implements CustomPacketPayload {

    public static final Type<OpenCardCasePayload> ID = new Type<>(
            ResourceLocation.fromNamespaceAndPath(CraftCards.MOD_ID, "open_case")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenCardCasePayload> CODEC =
            StreamCodec.unit(new OpenCardCasePayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}