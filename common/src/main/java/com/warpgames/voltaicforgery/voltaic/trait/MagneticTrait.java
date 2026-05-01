package com.warpgames.voltaicforgery.voltaic.trait;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class MagneticTrait implements ToolTrait {

    private static final double RANGE = 6.0D;
    private static final double SPEED = 0.45D;

    @Override
    public void onBlockBreak(Level level, BlockPos pos, BlockState state, Player player, ItemStack tool) {
        if (level instanceof ServerLevel serverLevel) {
            pullItems(serverLevel, Vec3.atCenterOf(pos), player);
        }
    }

    @Override
    public void onHitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Player player && attacker.level() instanceof ServerLevel serverLevel) {
            pullItems(serverLevel, target.position(), player);
        }
    }

    private static void pullItems(ServerLevel level, Vec3 origin, Player player) {
        AABB area = AABB.ofSize(origin, RANGE * 2.0D, RANGE * 2.0D, RANGE * 2.0D);
        for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, area, entity -> entity.isAlive() && !entity.getItem().isEmpty())) {
            Vec3 direction = player.position().add(0.0D, 0.8D, 0.0D).subtract(item.position());
            if (direction.lengthSqr() > 0.01D) {
                item.setDeltaMovement(direction.normalize().scale(SPEED));
            }
        }
    }
}
