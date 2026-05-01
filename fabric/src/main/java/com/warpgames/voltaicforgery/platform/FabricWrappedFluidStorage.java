package com.warpgames.voltaicforgery.platform;

import com.warpgames.voltaicforgery.voltaic.api.fluid.SimpleFluidTank;
import com.warpgames.voltaicforgery.voltaic.api.fluid.VFFluidTank;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.level.material.Fluids;

import java.util.Iterator;

public final class FabricWrappedFluidStorage extends SingleVariantStorage<FluidVariant> {

    private final VFFluidTank backing;

    public FabricWrappedFluidStorage(VFFluidTank backing) {
        this.backing = backing;
    }

    @Override
    protected FluidVariant getBlankVariant() {
        return FluidVariant.blank();
    }

    @Override
    protected long getCapacity(FluidVariant variant) {
        return backing.getCapacityMb();
    }

    @Override
    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        if (resource == null || resource.isBlank()) return 0;
        if (maxAmount <= 0) return 0;
        int accepted = backing.fill(resource.getFluid(), (int) Math.min(Integer.MAX_VALUE, maxAmount), true);
        if (accepted <= 0) return 0;
        updateSnapshots(transaction);
        return backing.fill(resource.getFluid(), accepted, false);
    }

    @Override
    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        if (resource == null || resource.isBlank()) return 0;
        if (maxAmount <= 0) return 0;
        if (backing.isEmpty()) return 0;
        if (backing.getFluid() != resource.getFluid()) return 0;
        int toDrain = (int) Math.min(Integer.MAX_VALUE, maxAmount);
        int drained = backing.drain(resource.getFluid(), toDrain, true);
        if (drained <= 0) return 0;
        updateSnapshots(transaction);
        return backing.drain(resource.getFluid(), drained, false);
    }

    @Override
    public Iterator<StorageView<FluidVariant>> iterator() {
        // SingleVariantStorage already implements proper views; keep default.
        return super.iterator();
    }

    @Override
    public FluidVariant getResource() {
        return backing.isEmpty() ? FluidVariant.blank() : FluidVariant.of(backing.getFluid());
    }

    @Override
    public long getAmount() {
        return backing.getAmountMb();
    }

    protected void setResource(FluidVariant resource) {
        if (resource == null || resource.isBlank()) {
            if (backing instanceof SimpleFluidTank sft) sft.set(Fluids.EMPTY, 0);
            return;
        }
        if (backing instanceof SimpleFluidTank sft) sft.set(resource.getFluid(), (int) getAmount());
    }

    protected void setAmount(long amount) {
        if (backing instanceof SimpleFluidTank sft) {
            if (amount <= 0) sft.set(Fluids.EMPTY, 0);
            else sft.set(sft.getFluid(), (int) Math.min(Integer.MAX_VALUE, amount));
        }
    }
}

