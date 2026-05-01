package com.warpgames.voltaicforgery.voltaic.api.energy;

public final class SimpleEnergyStorage implements VFEnergyStorage {

    private final int capacity;
    private final int maxReceive;
    private final int maxExtract;

    private int energy;

    public SimpleEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        this.capacity = Math.max(0, capacity);
        this.maxReceive = Math.max(0, maxReceive);
        this.maxExtract = Math.max(0, maxExtract);
        this.energy = 0;
    }

    public int setEnergy(int value) {
        int clamped = Math.max(0, Math.min(capacity, value));
        this.energy = clamped;
        return clamped;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive()) return 0;
        int toReceive = Math.min(this.maxReceive, Math.max(0, maxReceive));
        int accepted = Math.min(capacity - energy, toReceive);
        if (!simulate && accepted > 0) energy += accepted;
        return accepted;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract()) return 0;
        int toExtract = Math.min(this.maxExtract, Math.max(0, maxExtract));
        int extracted = Math.min(energy, toExtract);
        if (!simulate && extracted > 0) energy -= extracted;
        return extracted;
    }

    @Override
    public int getEnergyStored() {
        return energy;
    }

    @Override
    public int getMaxEnergyStored() {
        return capacity;
    }

    @Override
    public boolean canExtract() {
        return maxExtract > 0;
    }

    @Override
    public boolean canReceive() {
        return maxReceive > 0;
    }
}

