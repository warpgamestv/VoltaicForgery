package com.warpgames.voltaicforgery.voltaic.trait;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface ToolTrait {

    default void onBlockBreak(Level level, BlockPos pos, BlockState state, Player player) {
    }

    default void onBlockBreak(Level level, BlockPos pos, BlockState state, Player player, ItemStack tool) {
        onBlockBreak(level, pos, state, player);
    }

    default void onHitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
    }
}
