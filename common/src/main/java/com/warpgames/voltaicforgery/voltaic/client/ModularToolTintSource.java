package com.warpgames.voltaicforgery.voltaic.client;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public record ModularToolTintSource(int layer) implements ItemTintSource {

    public static final MapCodec<ModularToolTintSource> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            com.mojang.serialization.Codec.INT.fieldOf("layer").forGetter(ModularToolTintSource::layer)
    ).apply(instance, ModularToolTintSource::new));

    public static final net.minecraft.resources.Identifier ID = VoltaicContent.id("modular_tool_material");

    @Override
    public int calculate(ItemStack stack, ClientLevel level, LivingEntity entity) {
        if (level != null) {
            return ModularToolColorHandler.getTintColor(stack, level.registryAccess(), layer);
        }
        if (entity != null) {
            return ModularToolColorHandler.getTintColor(stack, entity.registryAccess(), layer);
        }
        return ModularToolColorHandler.UNRESOLVED_COLOR;
    }

    @Override
    public MapCodec<? extends ItemTintSource> type() {
        return MAP_CODEC;
    }
}
