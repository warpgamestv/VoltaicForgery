package com.warpgames.voltaicforgery.voltaic.api.fluid;

import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Fluid;

public final class SimpleFluidTank implements VFFluidTank {

    private final int capacityMb;
    private Fluid fluid = Fluids.EMPTY;
    private int amountMb = 0;

    public SimpleFluidTank(int capacityMb) {
        this.capacityMb = Math.max(0, capacityMb);
    }

    public void set(Fluid fluid, int amountMb) {
        if (fluid == null || fluid == Fluids.EMPTY || amountMb <= 0) {
            this.fluid = Fluids.EMPTY;
            this.amountMb = 0;
            return;
        }
        this.fluid = fluid;
        this.amountMb = Math.min(capacityMb, amountMb);
    }

    @Override
    public int getCapacityMb() {
        return capacityMb;
    }

    @Override
    public Fluid getFluid() {
        return fluid;
    }

    @Override
    public int getAmountMb() {
        return amountMb;
    }

    @Override
    public boolean isEmpty() {
        return amountMb <= 0 || fluid == Fluids.EMPTY;
    }

    @Override
    public boolean canFill(Fluid fluid) {
        if (fluid == null || fluid == Fluids.EMPTY) return false;
        return isEmpty() || this.fluid.isSame(fluid);
    }

    @Override
    public int fill(Fluid fluid, int amountMb, boolean simulate) {
        if (amountMb <= 0) return 0;
        if (!canFill(fluid)) return 0;
        int space = capacityMb - this.amountMb;
        if (space <= 0) return 0;
        int accepted = Math.min(space, amountMb);
        if (!simulate && accepted > 0) {
            if (isEmpty()) this.fluid = fluid;
            this.amountMb += accepted;
        }
        return accepted;
    }

    @Override
    public int drain(Fluid fluid, int amountMb, boolean simulate) {
        if (amountMb <= 0) return 0;
        if (isEmpty()) return 0;
        if (fluid == null || fluid == Fluids.EMPTY) return 0;
        if (!this.fluid.isSame(fluid)) return 0;
        int drained = Math.min(this.amountMb, amountMb);
        if (!simulate && drained > 0) {
            this.amountMb -= drained;
            if (this.amountMb <= 0) {
                this.fluid = Fluids.EMPTY;
                this.amountMb = 0;
            }
        }
        return drained;
    }

    @Override
    public int drainAny(int amountMb, boolean simulate) {
        if (amountMb <= 0) return 0;
        if (isEmpty()) return 0;
        int drained = Math.min(this.amountMb, amountMb);
        if (!simulate && drained > 0) {
            this.amountMb -= drained;
            if (this.amountMb <= 0) {
                this.fluid = Fluids.EMPTY;
                this.amountMb = 0;
            }
        }
        return drained;
    }
}

