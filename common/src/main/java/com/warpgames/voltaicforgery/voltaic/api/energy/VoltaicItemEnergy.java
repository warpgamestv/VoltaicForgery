package com.warpgames.voltaicforgery.voltaic.api.energy;

import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import com.warpgames.voltaicforgery.voltaic.item.component.ToolEnergyStorageComponent;
import net.minecraft.world.item.ItemStack;

/**
 * Data-component-backed energy helper for tool {@link net.minecraft.world.item.ItemStack}s.
 * External mods: NeoForge {@code Capabilities.Energy.ITEM} for FE/RF; Fabric via {@code EnergyStorage.ITEM} where registered.
 */
public final class VoltaicItemEnergy implements IEnergyTool {

    public static final VoltaicItemEnergy INSTANCE = new VoltaicItemEnergy();
    public static final ToolEnergyStorageComponent EMPTY = new ToolEnergyStorageComponent(0, 0);

    private VoltaicItemEnergy() {}

    public static boolean hasUsableEnergy(ItemStack stack) {
        ToolEnergyStorageComponent c = getOrEmpty(stack);
        return c.isEnabled() && c.currentEnergy() >= ToolEnergyStorageComponent.ENERGY_PER_USE;
    }

    private static ToolEnergyStorageComponent getOrEmpty(ItemStack stack) {
        return stack.getOrDefault(VoltaicContent.TOOL_ENERGY.get(), EMPTY);
    }

    @Override
    public long getEnergy(ItemStack stack) {
        return getOrEmpty(stack).currentEnergy();
    }

    @Override
    public long getMaxEnergy(ItemStack stack) {
        return getOrEmpty(stack).maxEnergy();
    }

    @Override
    public long receiveEnergy(ItemStack stack, long maxReceive, boolean simulate) {
        if (maxReceive <= 0) {
            return 0L;
        }
        ToolEnergyStorageComponent c = getOrEmpty(stack);
        if (!c.isEnabled()) {
            return 0L;
        }
        long space = c.maxEnergy() - c.currentEnergy();
        if (space <= 0) {
            return 0L;
        }
        long accepted = Math.min(space, maxReceive);
        if (!simulate) {
            stack.set(VoltaicContent.TOOL_ENERGY.get(), c.withCurrent(c.currentEnergy() + accepted));
        }
        return accepted;
    }

    @Override
    public long extractEnergy(ItemStack stack, long maxExtract, boolean simulate) {
        if (maxExtract <= 0) {
            return 0L;
        }
        ToolEnergyStorageComponent c = getOrEmpty(stack);
        if (!c.isEnabled() || c.currentEnergy() <= 0) {
            return 0L;
        }
        long removed = Math.min(c.currentEnergy(), maxExtract);
        if (!simulate) {
            stack.set(VoltaicContent.TOOL_ENERGY.get(), c.withCurrent(c.currentEnergy() - removed));
        }
        return removed;
    }

    /**
     * @return true if energy was consumed to pay for a durability use
     */
    public static boolean tryConsumeForDurabilityUse(ItemStack stack) {
        ToolEnergyStorageComponent c = getOrEmpty(stack);
        if (!c.isEnabled() || c.currentEnergy() < ToolEnergyStorageComponent.ENERGY_PER_USE) {
            return false;
        }
        stack.set(VoltaicContent.TOOL_ENERGY.get(), c.withCurrent(c.currentEnergy() - ToolEnergyStorageComponent.ENERGY_PER_USE));
        return true;
    }

    public static void setEnergyComponent(ItemStack stack, ToolEnergyStorageComponent component) {
        stack.set(VoltaicContent.TOOL_ENERGY.get(), component);
    }
}
