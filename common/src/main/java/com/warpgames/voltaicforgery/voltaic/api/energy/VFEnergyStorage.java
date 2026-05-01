package com.warpgames.voltaicforgery.voltaic.api.energy;

/**
 * Common energy abstraction for Voltaic Forgery machines.
 * Units are FE-style "energy" per tick; stored values are integers.
 */
public interface VFEnergyStorage {

    int receiveEnergy(int maxReceive, boolean simulate);

    int extractEnergy(int maxExtract, boolean simulate);

    int getEnergyStored();

    int getMaxEnergyStored();

    boolean canExtract();

    boolean canReceive();
}

