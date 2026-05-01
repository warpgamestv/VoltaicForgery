package com.warpgames.voltaicforgery.platform;

import com.warpgames.voltaicforgery.platform.services.IVoltaicRegistrar;
import com.warpgames.voltaicforgery.platform.services.BlockEntityFactory;
import com.warpgames.voltaicforgery.platform.services.MenuFactory;
import com.warpgames.voltaicforgery.platform.services.RegistryEntry;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

import java.lang.reflect.Constructor;
import java.util.function.Supplier;

public class FabricVoltaicRegistrar implements IVoltaicRegistrar {

    @Override
    public <T> RegistryEntry<T> register(Registry<T> registry, Identifier id, Supplier<T> factory) {
        T value = factory.get();
        Registry.register(registry, id, value);
        return new RegistryEntry<>() {
            @Override
            public Identifier id() {
                return id;
            }

            @Override
            public T get() {
                return value;
            }
        };
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> RegistryEntry<DataComponentType<T>> registerDataComponentType(Identifier id, Supplier<DataComponentType<T>> factory) {
        DataComponentType<T> value = factory.get();
        Registry.register((Registry) BuiltInRegistries.DATA_COMPONENT_TYPE, id, value);
        return new RegistryEntry<>() {
            @Override
            public Identifier id() {
                return id;
            }

            @Override
            public DataComponentType<T> get() {
                return value;
            }
        };
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T extends AbstractContainerMenu> RegistryEntry<MenuType<T>> registerMenuType(Identifier id, MenuFactory<T> factory) {
        try {
            // Using reflection to access protected MenuType constructor for a regular (non-extended) menu.
            // This ensures compatibility with standard serverPlayer.openMenu(provider) calls on Fabric,
            // as regular MenuTypes do not require an ExtendedScreenHandlerFactory.
            Constructor<MenuType> ctor = MenuType.class.getDeclaredConstructor(MenuType.MenuSupplier.class, net.minecraft.world.flag.FeatureFlagSet.class);
            ctor.setAccessible(true);
            MenuType<T> value = ctor.newInstance((MenuType.MenuSupplier<T>) factory::create, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS);
            
            Registry.register((Registry) BuiltInRegistries.MENU, id, value);
            return new RegistryEntry<>() {
                @Override
                public Identifier id() {
                    return id;
                }

                @Override
                public MenuType<T> get() {
                    return value;
                }
            };
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to register regular MenuType on Fabric", e);
        }
    }

    @Override
    @SafeVarargs
    public final <T extends BlockEntity> RegistryEntry<BlockEntityType<T>> registerBlockEntityType(
            Identifier id,
            BlockEntityFactory<? extends T> factory,
            Supplier<? extends Block>... validBlocks
    ) {
        Block[] blocks = new Block[validBlocks.length];
        for (int i = 0; i < validBlocks.length; i++) blocks[i] = validBlocks[i].get();

        @SuppressWarnings("unchecked")
        BlockEntityType<T> value = (BlockEntityType<T>) FabricBlockEntityTypeBuilder.create(factory::create, blocks).build();
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, value);

        return new RegistryEntry<>() {
            @Override
            public Identifier id() {
                return id;
            }

            @Override
            public BlockEntityType<T> get() {
                return value;
            }
        };
    }
}
