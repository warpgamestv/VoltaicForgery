package com.warpgames.voltaicforgery.voltaic.item;

import com.warpgames.voltaicforgery.voltaic.api.energy.VoltaicItemEnergy;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

final class ModularToolDurability {

    private ModularToolDurability() {
    }

    static void damageForAttack(ItemStack stack, LivingEntity attacker) {
        damageOrConsumeEnergy(stack, attacker, 1);
    }

    static void damageForMining(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
        if (level.isClientSide() || state.getDestroySpeed(level, pos) == 0.0F) {
            return;
        }
        damageOrConsumeEnergy(stack, miningEntity, 1);
    }

    private static void damageOrConsumeEnergy(ItemStack stack, LivingEntity entity, int amount) {
        if (VoltaicItemEnergy.tryConsumeForDurabilityUse(stack)) {
            return;
        }
        stack.hurtAndBreak(amount, entity, EquipmentSlot.MAINHAND);
    }
}
