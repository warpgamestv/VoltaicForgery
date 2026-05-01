package com.warpgames.voltaicforgery.platform.services;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

@FunctionalInterface
public interface MenuFactory<T extends AbstractContainerMenu> {

    T create(int containerId, Inventory playerInventory);
}

