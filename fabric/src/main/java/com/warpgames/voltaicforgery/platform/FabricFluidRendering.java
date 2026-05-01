package com.warpgames.voltaicforgery.platform;

import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import com.warpgames.voltaicforgery.voltaic.client.MoltenFluidColors;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderingRegistry;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;

import java.util.function.Supplier;

/**
 * Registers fluid render handlers for all molten metals on Fabric.
 * This is the Fabric equivalent of NeoForge's FluidType + RegisterFluidModelsEvent.
 */
public final class FabricFluidRendering {

    private static final Identifier MOLTEN_STILL = Identifier.fromNamespaceAndPath("voltaicforgery", "block/molten_metal_still");
    private static final Identifier MOLTEN_FLOWING = Identifier.fromNamespaceAndPath("voltaicforgery", "block/molten_metal_flow");
    private static final Identifier WATER_OVERLAY = Identifier.fromNamespaceAndPath("minecraft", "block/water_overlay");

    private FabricFluidRendering() {}

    public static void register() {
        registerMoltenFluid(VoltaicContent.MOLTEN_COPPER::get, VoltaicContent.FLOWING_MOLTEN_COPPER::get);
        registerMoltenFluid(VoltaicContent.MOLTEN_IRON::get, VoltaicContent.FLOWING_MOLTEN_IRON::get);
        registerMoltenFluid(VoltaicContent.MOLTEN_GOLD::get, VoltaicContent.FLOWING_MOLTEN_GOLD::get);
        registerMoltenFluid(VoltaicContent.MOLTEN_TIN::get, VoltaicContent.FLOWING_MOLTEN_TIN::get);
        registerMoltenFluid(VoltaicContent.MOLTEN_LEAD::get, VoltaicContent.FLOWING_MOLTEN_LEAD::get);
        registerMoltenFluid(VoltaicContent.MOLTEN_SILVER::get, VoltaicContent.FLOWING_MOLTEN_SILVER::get);
        registerMoltenFluid(VoltaicContent.MOLTEN_NICKEL::get, VoltaicContent.FLOWING_MOLTEN_NICKEL::get);
        registerMoltenFluid(VoltaicContent.MOLTEN_BRONZE::get, VoltaicContent.FLOWING_MOLTEN_BRONZE::get);
        registerMoltenFluid(VoltaicContent.MOLTEN_ELECTRUM::get, VoltaicContent.FLOWING_MOLTEN_ELECTRUM::get);
    }

    private static void registerMoltenFluid(Supplier<Fluid> still, Supplier<Fluid> flowing) {
        Fluid fluid = still.get();
        FluidRenderingRegistry.register(fluid, flowing.get(), createFluidModel(fluid));
    }

    private static FluidModel.Unbaked createFluidModel(Fluid fluid) {
        return new FluidModel.Unbaked(
                new Material(MOLTEN_STILL),
                new Material(MOLTEN_FLOWING),
                new Material(WATER_OVERLAY),
                state -> MoltenFluidColors.tint(fluid)
        );
    }
}
