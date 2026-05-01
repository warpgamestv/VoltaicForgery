package com.warpgames.voltaicforgery.platform;

import com.warpgames.voltaicforgery.voltaic.fluid.MoltenMetalFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.function.Supplier;

public abstract class NeoForgeMoltenMetalFluid extends MoltenMetalFluid {

    private final Supplier<FluidType> fluidType;

    protected NeoForgeMoltenMetalFluid(
            Supplier<? extends Fluid> still,
            Supplier<? extends Fluid> flowing,
            String metalName
    ) {
        super(still, flowing);
        this.fluidType = switch (metalName) {
            case "copper" -> NeoForgeVoltaicFluidTypes::moltenCopper;
            case "iron" -> NeoForgeVoltaicFluidTypes::moltenIron;
            default -> () -> {
                throw new IllegalArgumentException("Unknown molten metal: " + metalName);
            };
        };
    }

    @Override
    public FluidType getFluidType() {
        return fluidType.get();
    }

    public static final class Source extends NeoForgeMoltenMetalFluid {
        public Source(String metalName, Supplier<? extends Fluid> still, Supplier<? extends Fluid> flowing) {
            super(still, flowing, metalName);
        }

        @Override
        public boolean isSource(net.minecraft.world.level.material.FluidState state) {
            return true;
        }

        @Override
        public int getAmount(net.minecraft.world.level.material.FluidState state) {
            return 8;
        }
    }

    public static final class Flowing extends NeoForgeMoltenMetalFluid {
        public Flowing(String metalName, Supplier<? extends Fluid> still, Supplier<? extends Fluid> flowing) {
            super(still, flowing, metalName);
        }

        @Override
        protected void createFluidStateDefinition(net.minecraft.world.level.block.state.StateDefinition.Builder<Fluid, net.minecraft.world.level.material.FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public boolean isSource(net.minecraft.world.level.material.FluidState state) {
            return false;
        }

        @Override
        public int getAmount(net.minecraft.world.level.material.FluidState state) {
            return state.getValue(LEVEL);
        }
    }
}
