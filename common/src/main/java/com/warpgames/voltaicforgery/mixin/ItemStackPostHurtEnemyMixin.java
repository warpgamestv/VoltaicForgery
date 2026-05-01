package com.warpgames.voltaicforgery.mixin;

import com.warpgames.voltaicforgery.voltaic.api.energy.VoltaicItemEnergy;
import com.warpgames.voltaicforgery.voltaic.item.ModularPickaxeItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemStack.class)
public abstract class ItemStackPostHurtEnemyMixin {

    @Redirect(
            method = "postHurtEnemy",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;hurtAndBreak(ILnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;)V"
            )
    )
    private void voltaicforgery$useEnergyForAttackDurability(
            ItemStack self,
            int amount,
            LivingEntity holder,
            EquipmentSlot slot) {
        if (self.getItem() instanceof ModularPickaxeItem && VoltaicItemEnergy.tryConsumeForDurabilityUse(self)) {
            self.hurtAndBreak(0, holder, slot);
        } else {
            self.hurtAndBreak(amount, holder, slot);
        }
    }
}
