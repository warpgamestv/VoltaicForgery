package com.warpgames.voltaicforgery.voltaic.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class CoilUpgradeItem extends Item {

    private final CoilTier tier;

    public CoilUpgradeItem(Properties properties, CoilTier tier) {
        super(properties);
        this.tier = tier;
    }

    public CoilTier getTier() {
        return tier;
    }

    public static CoilTier tierOf(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return CoilTier.BASIC;
        if (stack.getItem() instanceof CoilUpgradeItem coil) return coil.getTier();
        return CoilTier.BASIC;
    }
}

