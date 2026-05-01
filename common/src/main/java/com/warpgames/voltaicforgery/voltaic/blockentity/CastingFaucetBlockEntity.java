package com.warpgames.voltaicforgery.voltaic.blockentity;

import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import com.warpgames.voltaicforgery.voltaic.block.CastingFaucetBlock;
import com.warpgames.voltaicforgery.voltaic.recipe.CastingRecipe;
import com.warpgames.voltaicforgery.voltaic.recipe.CastingRecipeInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Optional;

public class CastingFaucetBlockEntity extends BlockEntity {

    private boolean isPouring = false;
    private Fluid pouringFluid = Fluids.EMPTY;
    private static final int POUR_RATE_MB = 10; // Mb per tick

    public CastingFaucetBlockEntity(BlockPos pos, BlockState state) {
        super(VoltaicContent.CASTING_FAUCET_BE.get(), pos, state);
    }

    public void togglePouring() {
        this.isPouring = !this.isPouring;
        if (!this.isPouring) {
            this.pouringFluid = Fluids.EMPTY;
        }
        setChanged();
        syncToClient();
    }

    public boolean isPouring() {
        return isPouring;
    }

    /** The fluid currently being poured — used by the client renderer for tinting. */
    public Fluid getPouringFluid() {
        return pouringFluid;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CastingFaucetBlockEntity be) {
        if (!be.isPouring || level.isClientSide()) {
            return;
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        Direction facing = state.getValue(CastingFaucetBlock.FACING);

        // Crucible is behind the faucet (the block it's attached to).
        BlockPos sourcePos = pos.relative(facing);
        // Casting table is directly below the faucet.
        BlockPos targetPos = pos.below();

        BlockEntity sourceBe = level.getBlockEntity(sourcePos);
        BlockEntity targetBe = level.getBlockEntity(targetPos);

        // Validate source: must be an Induction Crucible with fluid.
        if (!(sourceBe instanceof InductionCrucibleBlockEntity crucible)) {
            be.stopPouring();
            return;
        }
        if (crucible.getTank().isEmpty()) {
            be.stopPouring();
            return;
        }

        // Validate target: must be a Casting Table.
        if (!(targetBe instanceof CastingTableBlockEntity castingTable)) {
            be.stopPouring();
            return;
        }

        // Don't pour if the table already has a finished output item.
        if (!castingTable.getItem(CastingTableBlockEntity.SLOT_OUTPUT).isEmpty()) {
            be.stopPouring();
            return;
        }

        // Don't pour if the table is currently cooling (a recipe is already in progress).
        if (castingTable.getCoolingTime() > 0) {
            be.stopPouring();
            return;
        }

        // Require a cast on the table.
        ItemStack cast = castingTable.getItem(CastingTableBlockEntity.SLOT_CAST);
        if (cast.isEmpty()) {
            be.stopPouring();
            return;
        }

        // Look up a matching casting recipe using the cast and the crucible's fluid.
        Fluid fluid = crucible.getTank().getFluid();

        // Sync the pouring fluid to the client for rendering (only update when it changes).
        if (be.pouringFluid != fluid) {
            be.pouringFluid = fluid;
            be.setChanged();
            be.syncToClient();
        }

        // Use a large amount for matching so we match by fluid type, not by current tank level.
        CastingRecipeInput recipeInput = new CastingRecipeInput(cast, fluid, Integer.MAX_VALUE);
        Optional<RecipeHolder<CastingRecipe>> match = serverLevel.getServer().getRecipeManager().getRecipeFor(
                VoltaicContent.CASTING_RECIPE_TYPE.get(),
                recipeInput,
                level
        );

        if (match.isEmpty()) {
            be.stopPouring();
            return;
        }

        CastingRecipe recipe = match.get().value();
        int requiredMb = recipe.fluid().amountMb();
        int currentMb = castingTable.getTank().getAmountMb();

        // Auto-stop: table has reached the recipe's required fluid amount.
        if (currentMb >= requiredMb) {
            be.stopPouring();
            return;
        }

        // Calculate how much to transfer this tick (don't overshoot the recipe amount).
        int toTransfer = Math.min(POUR_RATE_MB, requiredMb - currentMb);

        // Simulate both sides before moving anything. If the table rejects the fluid
        // for any reason, never drain the crucible.
        int simDrain = crucible.drainMoltenFluid(fluid, toTransfer, true);
        if (simDrain <= 0) {
            be.stopPouring();
            return;
        }
        int simFill = castingTable.fill(fluid, simDrain, true);
        if (simFill <= 0) {
            be.stopPouring();
            return;
        }

        // Execute the transfer using the exact amount the table can accept.
        int actualDrain = crucible.drainMoltenFluid(fluid, simFill, false);
        if (actualDrain <= 0) {
            be.stopPouring();
            return;
        }
        int actualFill = castingTable.fill(fluid, actualDrain, false);
        if (actualFill < actualDrain || currentMb + actualFill >= requiredMb) {
            be.stopPouring();
        }
    }

    private void stopPouring() {
        this.isPouring = false;
        this.pouringFluid = Fluids.EMPTY;
        setChanged();
        syncToClient();
    }

    // --- Persistence & client sync ---

    private void syncToClient() {
        if (level == null || level.isClientSide()) return;
        BlockState bs = getBlockState();
        level.sendBlockUpdated(worldPosition, bs, bs, 3);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return saveWithoutMetadata(provider);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean("Pouring", isPouring);
        output.putString("PouringFluid", BuiltInRegistries.FLUID.getKey(pouringFluid).toString());
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        isPouring = input.getBooleanOr("Pouring", false);
        String fluidId = input.getStringOr("PouringFluid", BuiltInRegistries.FLUID.getKey(Fluids.EMPTY).toString());
        pouringFluid = BuiltInRegistries.FLUID.getOptional(Identifier.parse(fluidId)).orElse(Fluids.EMPTY);
    }
}
