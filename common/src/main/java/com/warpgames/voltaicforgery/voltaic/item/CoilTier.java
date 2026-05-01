package com.warpgames.voltaicforgery.voltaic.item;

public enum CoilTier {
    BASIC(1, 1000, 10),
    ADVANCED(2, 2000, 20),
    ELITE(3, 4000, 40);

    private final int tier;
    private final int maxHeat;
    private final int fePerTick;

    CoilTier(int tier, int maxHeat, int fePerTick) {
        this.tier = tier;
        this.maxHeat = maxHeat;
        this.fePerTick = fePerTick;
    }

    public int tier() {
        return tier;
    }

    public int maxHeat() {
        return maxHeat;
    }

    public int fePerTick() {
        return fePerTick;
    }
}

