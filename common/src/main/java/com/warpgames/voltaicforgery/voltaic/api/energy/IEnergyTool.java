package com.warpgames.voltaicforgery.voltaic.api.energy;

import net.minecraft.world.item.ItemStack;

/**
 * Data-component-backed energy helper for tool {@link net.minecraft.world.item.ItemStack}s.
 * Implementations for charging/discharging are in {@link VoltaicItemEnergy};
 * external mods should use platform capabilities (NeoForge {@code Capabilities.Energy.ITEM}).
 */
public interface IEnergyTool {

    long getEnergy(ItemStack stack);

    long getMaxEnergy(ItemStack stack);

    /**
     * @param maxReceive maximum FE to insert
     * @param simulate   if true, do not mutate the stack
     * @return amount accepted
     */
    long receiveEnergy(ItemStack stack, long maxReceive, boolean simulate);

    /**
     * @param maxExtract maximum FE to remove
     * @param simulate   if true, do not mutate the stack
     * @return amount extracted
     */
    long extractEnergy(ItemStack stack, long maxExtract, boolean simulate);
}
