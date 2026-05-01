package com.warpgames.voltaicforgery.voltaic.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import java.util.function.Supplier;

/**
 * Simple FlowingFluid implementation for molten metals.
 *
 * Textures are expected to be grayscale; tinting is applied via block/item color handlers (Priority 2).
 */
public abstract class MoltenMetalFluid extends FlowingFluid {

    private final Supplier<? extends Fluid> still;
    private final Supplier<? extends Fluid> flowing;

    protected MoltenMetalFluid(
            Supplier<? extends Fluid> still,
            Supplier<? extends Fluid> flowing
    ) {
        this.still = still;
        this.flowing = flowing;
    }

    @Override
    public Fluid getFlowing() {
        return flowing.get();
    }

    @Override
    public Fluid getSource() {
        return still.get();
    }

    @Override
    public Item getBucket() {
        // Molten metals are machine-handled only: pipes, faucets, and casting tables.
        return Items.AIR;
    }

    @Override
    protected BlockState createLegacyBlock(FluidState state) {
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean isSame(Fluid fluid) {
        return fluid == still.get() || fluid == flowing.get();
    }

    @Override
    protected int getDropOff(LevelReader level) {
        return 2;
    }

    @Override
    public int getTickDelay(LevelReader level) {
        return 20;
    }

    @Override
    protected float getExplosionResistance() {
        return 100.0F;
    }

    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid fluid, Direction direction) {
        return false;
    }

    @Override
    public int getSlopeFindDistance(LevelReader level) {
        return 2;
    }

    @Override
    public int getSpreadDelay(Level level, BlockPos pos, FluidState state, FluidState newState) {
        return 20;
    }

    @Override
    protected boolean canConvertToSource(net.minecraft.server.level.ServerLevel level) {
        return false;
    }

    @Override
    protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state) {
        // No special drops.
    }

    @Override
    protected boolean isRandomlyTicking() {
        return false;
    }

    public static final class Source extends MoltenMetalFluid {
        public Source(Supplier<? extends Fluid> still, Supplier<? extends Fluid> flowing) {
            super(still, flowing);
        }

        @Override
        public boolean isSource(FluidState state) {
            return true;
        }

        @Override
        public int getAmount(FluidState state) {
            return 8;
        }
    }

    public static final class Flowing extends MoltenMetalFluid {
        public Flowing(Supplier<? extends Fluid> still, Supplier<? extends Fluid> flowing) {
            super(still, flowing);
        }

        @Override
        protected void createFluidStateDefinition(net.minecraft.world.level.block.state.StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public boolean isSource(FluidState state) {
            return false;
        }

        @Override
        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }
    }
}

