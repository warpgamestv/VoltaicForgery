package com.warpgames.voltaicforgery.platform.services;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

/**
 * Loader-specific registration bridge.
 * <p>
 * Common code can safely reference vanilla registries/types; the actual registration mechanism differs per loader.
 */
public interface IVoltaicRegistrar {

    <T> RegistryEntry<T> register(Registry<T> registry, Identifier id, Supplier<T> factory);

    <T> RegistryEntry<DataComponentType<T>> registerDataComponentType(Identifier id, Supplier<DataComponentType<T>> factory);

    <T extends AbstractContainerMenu> RegistryEntry<MenuType<T>> registerMenuType(Identifier id, MenuFactory<T> factory);

    <T extends BlockEntity> RegistryEntry<BlockEntityType<T>> registerBlockEntityType(
            Identifier id,
            BlockEntityFactory<? extends T> factory,
            Supplier<? extends Block>... validBlocks
    );
}

