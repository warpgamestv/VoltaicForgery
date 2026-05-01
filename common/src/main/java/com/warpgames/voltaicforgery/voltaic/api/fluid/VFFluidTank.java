package com.warpgames.voltaicforgery.voltaic.api.fluid;

import net.minecraft.world.level.material.Fluid;

/**
 * Common, minimal fluid tank abstraction.
 * Amount is tracked in millibuckets (mB).
 */
public interface VFFluidTank {

    int getCapacityMb();

    Fluid getFluid();

    int getAmountMb();

    boolean isEmpty();

    boolean canFill(Fluid fluid);

    int fill(Fluid fluid, int amountMb, boolean simulate);

    int drain(Fluid fluid, int amountMb, boolean simulate);

    int drainAny(int amountMb, boolean simulate);
}

