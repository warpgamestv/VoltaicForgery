package com.warpgames.voltaicforgery.platform.services;

import net.minecraft.world.item.ItemStack;

/**
 * Loader-specific capability/transfer wiring bridge.
 * <p>
 * Implementations should register exposure for energy + fluid on the relevant block entities.
 */
public interface IVoltaicCapabilities {

    void init();

    /**
     * Best-effort FE insertion into arbitrary items using the loader's common energy interoperability layer
     * (NeoForge item energy capability, Fabric future energy API bridge).
     * <p>
     * Common code should first handle native VF energy tools via {@code IEnergyTool}, then fall back to this.
     */
    default long receiveEnergyToItem(ItemStack stack, long maxReceive, boolean simulate) {
        return 0L;
    }
}

