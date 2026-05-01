package com.warpgames.voltaicforgery.voltaic.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;

public record CastingFluidIngredient(Identifier fluid, int amountMb) {

    public static final Codec<CastingFluidIngredient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("fluid").forGetter(CastingFluidIngredient::fluid),
            Codec.INT.fieldOf("amount").forGetter(CastingFluidIngredient::amountMb)
    ).apply(instance, CastingFluidIngredient::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CastingFluidIngredient> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, CastingFluidIngredient::fluid,
            ByteBufCodecs.VAR_INT, CastingFluidIngredient::amountMb,
            CastingFluidIngredient::new
    );

    public boolean matches(Fluid fluid, int availableMb) {
        return this.fluid.equals(BuiltInRegistries.FLUID.getKey(fluid)) && availableMb >= amountMb;
    }
}
