package com.warpgames.voltaicforgery.platform;

import com.warpgames.voltaicforgery.Constants;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class NeoForgeVoltaicFluidTypes {

    private static DeferredRegister<FluidType> FLUID_TYPES;
    private static DeferredHolder<FluidType, FluidType> MOLTEN_COPPER_TYPE;
    private static DeferredHolder<FluidType, FluidType> MOLTEN_IRON_TYPE;
    private static DeferredHolder<FluidType, FluidType> MOLTEN_GOLD_TYPE;
    private static DeferredHolder<FluidType, FluidType> MOLTEN_TIN_TYPE;
    private static DeferredHolder<FluidType, FluidType> MOLTEN_LEAD_TYPE;
    private static DeferredHolder<FluidType, FluidType> MOLTEN_SILVER_TYPE;
    private static DeferredHolder<FluidType, FluidType> MOLTEN_NICKEL_TYPE;
    private static DeferredHolder<FluidType, FluidType> MOLTEN_BRONZE_TYPE;
    private static DeferredHolder<FluidType, FluidType> MOLTEN_ELECTRUM_TYPE;

    private NeoForgeVoltaicFluidTypes() {}

    static void attach(IEventBus bus) {
        if (FLUID_TYPES != null) {
            return;
        }

        FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, Constants.MOD_ID);
        MOLTEN_COPPER_TYPE = FLUID_TYPES.register("molten_copper", () -> create("molten_copper"));
        MOLTEN_IRON_TYPE = FLUID_TYPES.register("molten_iron", () -> create("molten_iron"));
        MOLTEN_GOLD_TYPE = FLUID_TYPES.register("molten_gold", () -> create("molten_gold"));
        MOLTEN_TIN_TYPE = FLUID_TYPES.register("molten_tin", () -> create("molten_tin"));
        MOLTEN_LEAD_TYPE = FLUID_TYPES.register("molten_lead", () -> create("molten_lead"));
        MOLTEN_SILVER_TYPE = FLUID_TYPES.register("molten_silver", () -> create("molten_silver"));
        MOLTEN_NICKEL_TYPE = FLUID_TYPES.register("molten_nickel", () -> create("molten_nickel"));
        MOLTEN_BRONZE_TYPE = FLUID_TYPES.register("molten_bronze", () -> create("molten_bronze"));
        MOLTEN_ELECTRUM_TYPE = FLUID_TYPES.register("molten_electrum", () -> create("molten_electrum"));
        FLUID_TYPES.register(bus);
    }

    public static FluidType moltenCopper() {
        return MOLTEN_COPPER_TYPE.get();
    }

    public static FluidType moltenIron() {
        return MOLTEN_IRON_TYPE.get();
    }

    public static FluidType moltenGold() {
        return MOLTEN_GOLD_TYPE.get();
    }

    private static FluidType create(String path) {
        return new FluidType(FluidType.Properties.create()
                .descriptionId("fluid." + Constants.MOD_ID + "." + path)
                .canSwim(false)
                .canDrown(false)
                .canExtinguish(false)
                .canHydrate(false)
                .supportsBoating(false)
                .canConvertToSource(false)
                .density(3000)
                .viscosity(6000)
                .temperature(1300)
                .lightLevel(10)
                .rarity(Rarity.UNCOMMON));
    }

    public static Identifier stillTexture() {
        return Identifier.fromNamespaceAndPath("voltaicforgery", "block/molten_metal_still");
    }

    public static Identifier flowingTexture() {
        return Identifier.fromNamespaceAndPath("voltaicforgery", "block/molten_metal_flow");
    }

    public static Identifier overlayTexture() {
        return Identifier.fromNamespaceAndPath("minecraft", "block/water_overlay");
    }

}
