package com.warpgames.voltaicforgery.platform;

import com.warpgames.voltaicforgery.Constants;
import com.warpgames.voltaicforgery.platform.services.BlockEntityFactory;
import com.warpgames.voltaicforgery.platform.services.IVoltaicRegistrar;
import com.warpgames.voltaicforgery.platform.services.MenuFactory;
import com.warpgames.voltaicforgery.platform.services.RegistryEntry;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

public class NeoForgeVoltaicRegistrar implements IVoltaicRegistrar {

    private static DeferredRegister<net.minecraft.world.level.block.Block> BLOCKS;
    private static DeferredRegister<net.minecraft.world.item.Item> ITEMS;
    private static DeferredRegister<Fluid> FLUIDS;
    private static DeferredRegister<net.minecraft.world.level.block.entity.BlockEntityType<?>> BLOCK_ENTITIES;
    private static DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES;
    private static DeferredRegister<MenuType<?>> MENUS;
    private static DeferredRegister<RecipeType<?>> RECIPE_TYPES;
    private static DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS;
    private static DeferredRegister<RecipeBookCategory> RECIPE_BOOK_CATEGORIES;
    private static DeferredRegister<CreativeModeTab> CREATIVE_TABS;

    static void attach(IEventBus bus) {
        if (BLOCKS != null) return;
        BLOCKS = DeferredRegister.create(Registries.BLOCK, Constants.MOD_ID);
        ITEMS = DeferredRegister.create(Registries.ITEM, Constants.MOD_ID);
        FLUIDS = DeferredRegister.create(Registries.FLUID, Constants.MOD_ID);
        BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Constants.MOD_ID);
        DATA_COMPONENT_TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Constants.MOD_ID);
        MENUS = DeferredRegister.create(Registries.MENU, Constants.MOD_ID);
        RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, Constants.MOD_ID);
        RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, Constants.MOD_ID);
        RECIPE_BOOK_CATEGORIES = DeferredRegister.create(Registries.RECIPE_BOOK_CATEGORY, Constants.MOD_ID);
        CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Constants.MOD_ID);

        BLOCKS.register(bus);
        ITEMS.register(bus);
        FLUIDS.register(bus);
        BLOCK_ENTITIES.register(bus);
        DATA_COMPONENT_TYPES.register(bus);
        MENUS.register(bus);
        RECIPE_TYPES.register(bus);
        RECIPE_SERIALIZERS.register(bus);
        RECIPE_BOOK_CATEGORIES.register(bus);
        CREATIVE_TABS.register(bus);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> RegistryEntry<T> register(Registry<T> registry, Identifier id, Supplier<T> factory) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(factory, "factory");

        // We can't compare registries reliably by identity across environments, so use the registry key location.
        Identifier regKey = registry.key().identifier();

        if (regKey.equals(Registries.BLOCK.identifier())) {
            DeferredHolder ro = BLOCKS.register(id.getPath(), (Supplier) factory);
            return wrap(id, ro);
        }
        if (regKey.equals(Registries.ITEM.identifier())) {
            DeferredHolder ro = ITEMS.register(id.getPath(), (Supplier) factory);
            return wrap(id, ro);
        }
        if (regKey.equals(Registries.FLUID.identifier())) {
            DeferredHolder ro = FLUIDS.register(id.getPath(), (Supplier) factory);
            return wrap(id, ro);
        }
        if (regKey.equals(Registries.BLOCK_ENTITY_TYPE.identifier())) {
            DeferredHolder ro = BLOCK_ENTITIES.register(id.getPath(), (Supplier) factory);
            return wrap(id, ro);
        }
        if (regKey.equals(Registries.DATA_COMPONENT_TYPE.identifier())) {
            DeferredHolder ro = DATA_COMPONENT_TYPES.register(id.getPath(), (Supplier) factory);
            return wrap(id, ro);
        }
        if (regKey.equals(Registries.MENU.identifier())) {
            DeferredHolder ro = MENUS.register(id.getPath(), (Supplier) factory);
            return wrap(id, ro);
        }
        if (regKey.equals(Registries.RECIPE_TYPE.identifier())) {
            DeferredHolder ro = RECIPE_TYPES.register(id.getPath(), (Supplier) factory);
            return wrap(id, ro);
        }
        if (regKey.equals(Registries.RECIPE_SERIALIZER.identifier())) {
            DeferredHolder ro = RECIPE_SERIALIZERS.register(id.getPath(), (Supplier) factory);
            return wrap(id, ro);
        }
        if (regKey.equals(Registries.RECIPE_BOOK_CATEGORY.identifier())) {
            DeferredHolder ro = RECIPE_BOOK_CATEGORIES.register(id.getPath(), (Supplier) factory);
            return wrap(id, ro);
        }
        if (regKey.equals(Registries.CREATIVE_MODE_TAB.identifier())) {
            DeferredHolder ro = CREATIVE_TABS.register(id.getPath(), (Supplier) factory);
            return wrap(id, ro);
        }

        throw new IllegalStateException("Unsupported registry for NeoForge registrar: " + regKey);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> RegistryEntry<DataComponentType<T>> registerDataComponentType(Identifier id, Supplier<DataComponentType<T>> factory) {
        DeferredHolder<DataComponentType<?>, DataComponentType<T>> ro = (DeferredHolder) DATA_COMPONENT_TYPES.register(id.getPath(), (Supplier) factory);
        return wrap(id, ro);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T extends AbstractContainerMenu> RegistryEntry<MenuType<T>> registerMenuType(Identifier id, MenuFactory<T> factory) {
        Supplier<MenuType<T>> built = () -> IMenuTypeExtension.create((containerId, inventory, buffer) -> factory.create(containerId, inventory));
        DeferredHolder<MenuType<?>, MenuType<T>> ro = (DeferredHolder) MENUS.register(id.getPath(), (Supplier) built);
        return wrap(id, ro);
    }

    @Override
    @SafeVarargs
    public final <T extends BlockEntity> RegistryEntry<BlockEntityType<T>> registerBlockEntityType(
            Identifier id,
            BlockEntityFactory<? extends T> factory,
            Supplier<? extends Block>... validBlocks
    ) {
        Supplier<BlockEntityType<T>> built = () -> {
            Block[] blocks = Arrays.stream(validBlocks).map(Supplier::get).toArray(Block[]::new);
            return constructBlockEntityType(factory, blocks);
        };

        @SuppressWarnings({"unchecked", "rawtypes"})
        DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> ro = (DeferredHolder) BLOCK_ENTITIES.register(id.getPath(), (Supplier) built);
        return wrap(id, ro);
    }

    @SuppressWarnings("rawtypes")
    private static <T> RegistryEntry<T> wrap(Identifier id, DeferredHolder obj) {
        return new RegistryEntry<>() {
            @Override
            public Identifier id() {
                return id;
            }

            @Override
            public T get() {
                @SuppressWarnings("unchecked")
                T value = (T) obj.get();
                return value;
            }
        };
    }

    @SuppressWarnings("unchecked")
    private static <T extends BlockEntity> BlockEntityType<T> constructBlockEntityType(
            BlockEntityFactory<? extends T> supplier,
            Block... validBlocks
    ) {
        try {
            Constructor<BlockEntityType> ctor = BlockEntityType.class.getDeclaredConstructor(BlockEntityType.BlockEntitySupplier.class, Set.class);
            ctor.setAccessible(true);
            BlockEntityType.BlockEntitySupplier<? extends T> vanillaSupplier = supplier::create;
            return (BlockEntityType<T>) ctor.newInstance(vanillaSupplier, Set.of(validBlocks));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to construct BlockEntityType via reflection", e);
        }
    }
}

