package org.mob.craftcards.component;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.mob.craftcards.CraftCards;

import java.awt.*;
import java.util.List;
import java.util.function.UnaryOperator;

public class ModDataComponentTypes {
    public static final ComponentType<Integer> T0 =
            register("t0", builder -> builder.codec(Codec.INT));

    public static final ComponentType<Integer> T1 =
            register("t1", builder -> builder.codec(Codec.INT));

    public static final ComponentType<Integer> T2 =
            register("t2", builder -> builder.codec(Codec.INT));

    public static final ComponentType<Integer> T3 =
            register("t3", builder -> builder.codec(Codec.INT));

    public static final ComponentType<Integer> T4 =
            register("t4", builder -> builder.codec(Codec.INT));

    public static final ComponentType<Integer> T5 =
            register("t5", builder -> builder.codec(Codec.INT));

    public static final ComponentType<Integer> T6 =
            register("t6", builder -> builder.codec(Codec.INT));

    public static final ComponentType<Integer>[] TIER_COMPONENTS = new ComponentType[] {
            T0, T1, T2, T3, T4, T5, T6
    };


    private static <T> ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(CraftCards.MOD_ID, name),
                builderOperator.apply(ComponentType.builder()).build());
    }

    public static void registerDataComponentTypes() {
        CraftCards.LOGGER.info("Registering Data Component Types for " + CraftCards.MOD_ID);
    }
}
