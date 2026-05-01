package com.warpgames.voltaicforgery.platform;

import com.warpgames.voltaicforgery.voltaic.api.energy.VFEnergyStorage;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public final class NeoForgeWrappedEnergyHandler implements EnergyHandler {

    private final VFEnergyStorage backing;

    public NeoForgeWrappedEnergyHandler(VFEnergyStorage backing) {
        this.backing = backing;
    }

    @Override
    public long getAmountAsLong() {
        return backing.getEnergyStored();
    }

    @Override
    public long getCapacityAsLong() {
        return backing.getMaxEnergyStored();
    }

    @Override
    public int insert(int amount, TransactionContext transaction) {
        return backing.receiveEnergy(amount, false);
    }

    @Override
    public int extract(int amount, TransactionContext transaction) {
        return backing.extractEnergy(amount, false);
    }
}

