package com.warpgames.voltaicforgery.platform;

import com.warpgames.voltaicforgery.voltaic.api.fluid.VFFluidTank;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public final class NeoForgeWrappedFluidResourceHandler implements ResourceHandler<FluidResource> {

    private final VFFluidTank backing;

    public NeoForgeWrappedFluidResourceHandler(VFFluidTank backing) {
        this.backing = backing;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public FluidResource getResource(int slot) {
        if (slot != 0) return FluidResource.of(Fluids.EMPTY);
        if (backing.isEmpty()) return FluidResource.of(Fluids.EMPTY);
        return FluidResource.of(backing.getFluid());
    }

    @Override
    public long getAmountAsLong(int slot) {
        if (slot != 0) return 0;
        return backing.getAmountMb();
    }

    @Override
    public long getCapacityAsLong(int slot, FluidResource resource) {
        if (slot != 0) return 0;
        return backing.getCapacityMb();
    }

    @Override
    public boolean isValid(int slot, FluidResource resource) {
        if (slot != 0) return false;
        if (resource == null) return false;
        return backing.canFill(resource.getFluid());
    }

    @Override
    public int insert(int slot, FluidResource resource, int amount, TransactionContext transaction) {
        if (slot != 0) return 0;
        if (resource == null) return 0;
        return backing.fill(resource.getFluid(), amount, false);
    }

    @Override
    public int extract(int slot, FluidResource resource, int amount, TransactionContext transaction) {
        if (slot != 0) return 0;
        if (resource == null) return 0;
        if (backing.isEmpty()) return 0;
        if (backing.getFluid() != resource.getFluid()) return 0;
        return backing.drain(resource.getFluid(), amount, false);
    }
}

