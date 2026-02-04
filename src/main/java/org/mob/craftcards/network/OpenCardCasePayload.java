package org.mob.craftcards.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record OpenCardCasePayload() implements CustomPayload {
    public static final Id<OpenCardCasePayload> ID = new Id<>(Identifier.of("craftcards", "open_case"));
    public static final PacketCodec<RegistryByteBuf, OpenCardCasePayload> CODEC = PacketCodec.unit(new OpenCardCasePayload());

    @Override
    public Id<? extends CustomPayload> getId() { return ID; }
}