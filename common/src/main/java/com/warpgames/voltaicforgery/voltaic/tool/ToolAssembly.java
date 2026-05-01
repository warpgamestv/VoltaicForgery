package com.warpgames.voltaicforgery.voltaic.tool;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ToolAssembly(
        String headMaterial,
        String bindingMaterial,
        String handleMaterial
) {

    public static final ToolAssembly DEFAULT = new ToolAssembly("copper", "copper", "copper");

    public static final Codec<ToolAssembly> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("head_material").forGetter(ToolAssembly::headMaterial),
            Codec.STRING.fieldOf("binding_material").forGetter(ToolAssembly::bindingMaterial),
            Codec.STRING.fieldOf("handle_material").forGetter(ToolAssembly::handleMaterial)
    ).apply(instance, ToolAssembly::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ToolAssembly> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            ToolAssembly::headMaterial,
            ByteBufCodecs.STRING_UTF8,
            ToolAssembly::bindingMaterial,
            ByteBufCodecs.STRING_UTF8,
            ToolAssembly::handleMaterial,
            ToolAssembly::new
    );
}

