package com.warpgames.voltaicforgery.voltaic.client;

import com.mojang.serialization.MapCodec;
import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Tints tool part items based on their {@code tool_part_material} component.
 */
public record ToolPartTintSource() implements ItemTintSource {

    public static final MapCodec<ToolPartTintSource> MAP_CODEC = MapCodec.unit(ToolPartTintSource::new);
    public static final Identifier ID = VoltaicContent.id("tool_part_material");

    @Override
    public int calculate(ItemStack stack, ClientLevel level, LivingEntity entity) {
        if (level != null) {
            return ToolPartColorHandler.getTintColor(stack, level.registryAccess());
        }
        if (entity != null) {
            return ToolPartColorHandler.getTintColor(stack, entity.registryAccess());
        }
        return ToolPartColorHandler.UNRESOLVED_COLOR;
    }

    @Override
    public MapCodec<? extends ItemTintSource> type() {
        return MAP_CODEC;
    }
}

