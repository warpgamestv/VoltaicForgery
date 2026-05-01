package com.warpgames.voltaicforgery.voltaic.blockentity;

import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import com.warpgames.voltaicforgery.voltaic.api.energy.SimpleEnergyStorage;
import com.warpgames.voltaicforgery.voltaic.api.energy.VFEnergyStorage;
import com.warpgames.voltaicforgery.voltaic.api.fluid.SimpleFluidTank;
import com.warpgames.voltaicforgery.voltaic.api.fluid.VFFluidTank;
import com.warpgames.voltaicforgery.voltaic.item.CoilTier;
import com.warpgames.voltaicforgery.voltaic.item.CoilUpgradeItem;
import com.warpgames.voltaicforgery.voltaic.menu.InductionCrucibleMenu;
import com.warpgames.voltaicforgery.voltaic.recipe.MeltingRecipe;
import com.warpgames.voltaicforgery.voltaic.recipe.MeltingRecipeInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Iterator;
import java.util.Optional;

public class InductionCrucibleBlockEntity extends BlockEntity implements Container, MenuProvider {
    public static final int STATUS_IDLE = 0;
    public static final int STATUS_NO_RECIPE = 1;
    public static final int STATUS_HEATING = 2;
    public static final int STATUS_MELTING = 3;
    public static final int STATUS_TANK_FULL = 4;
    public static final int STATUS_NO_POWER = 5;

    public static final int SLOTS = 2;
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_COIL = 1;

    public static final int ENERGY_CAPACITY = 200_000;
    public static final int FE_PULL_PER_TICK = 2_000;

    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
    private final SimpleEnergyStorage energy = new SimpleEnergyStorage(ENERGY_CAPACITY, 2_000, 0);
    private final SimpleFluidTank tank = new SimpleFluidTank(4_000);
    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> energy.getEnergyStored();
                case 1 -> energy.getMaxEnergyStored();
                case 2 -> currentHeat;
                case 3 -> getCoilTier().maxHeat();
                case 4 -> BuiltInRegistries.FLUID.getId(tank.getFluid());
                case 5 -> tank.getAmountMb();
                case 6 -> tank.getCapacityMb();
                case 7 -> meltProgress;
                case 8 -> meltTimeTotal;
                case 9 -> statusCode;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> energy.setEnergy(value);
                case 2 -> currentHeat = value;
                case 7 -> meltProgress = value;
                case 8 -> meltTimeTotal = value;
                case 9 -> statusCode = value;
                default -> {
                }
            }
        }

        @Override
        public int getCount() {
            return 10;
        }
    };

    private int currentHeat = 0;
    private int meltProgress = 0;
    private int meltTimeTotal = 0;
    private int statusCode = STATUS_IDLE;

    public InductionCrucibleBlockEntity(BlockPos pos, BlockState state) {
        super(VoltaicContent.INDUCTION_CRUCIBLE_BE.get(), pos, state);
    }

    public VFEnergyStorage getEnergy() {
        return energy;
    }

    public VFFluidTank getTank() {
        return tank;
    }

    public ContainerData getData() {
        return data;
    }

    public int getCurrentHeat() {
        return currentHeat;
    }

    public ItemStack getCoilStack() {
        return items.get(SLOT_COIL);
    }

    public void setCoilStack(ItemStack stack) {
        setItem(SLOT_COIL, stack);
    }

    public CoilTier getCoilTier() {
        return CoilUpgradeItem.tierOf(getCoilStack());
    }

    public ItemStack getInputStack() {
        return items.get(SLOT_INPUT);
    }

    public void setInputStack(ItemStack stack) {
        setItem(SLOT_INPUT, stack);
    }

    @Override
    public int getContainerSize() {
        return SLOTS;
    }

    @Override
    public boolean isEmpty() {
        return getCoilStack().isEmpty() && getInputStack().isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack removed = ContainerHelper.removeItem(items, slot, amount);
        if (!removed.isEmpty()) setChanged();
        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = items.get(slot);
        items.set(slot, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) stack.setCount(getMaxStackSize());
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        items.clear();
    }

    @Override
    public Iterator<ItemStack> iterator() {
        return items.iterator();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.voltaicforgery.induction_crucible");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new InductionCrucibleMenu(containerId, playerInventory, this);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, InductionCrucibleBlockEntity be) {
        if (!level.isClientSide()) {
            // Input-only rule: crucible only pulls FE from the block entity below it.
            be.pullEnergyFromBelow(level, pos);
        }

        CoilTier tier = be.getCoilTier();
        int maxHeat = tier.maxHeat();
        int fePerTick = tier.fePerTick();

        boolean changed = false;

        boolean canHeat = be.currentHeat < maxHeat;
        boolean hasEnergy = be.energy.getEnergyStored() >= fePerTick;

        if (canHeat && hasEnergy && fePerTick > 0) {
            // Internal consumption: bypass extractEnergy (maxExtract=0 blocks external pulls only).
            be.energy.setEnergy(be.energy.getEnergyStored() - fePerTick);
            be.currentHeat = Math.min(maxHeat, be.currentHeat + 1);
            changed = true;
        } else {
            if (be.currentHeat > 0) {
                be.currentHeat = Math.max(0, be.currentHeat - 1);
                changed = true;
            }
        }

        if (!level.isClientSide()) {
            // Melting processing: if hot enough, melt input into tank using melting recipes.
            changed |= be.tryMelt(level);
        }

        if (changed) {
            be.setChanged();
            be.syncToClient();
        }
    }

    private void pullEnergyFromBelow(Level level, BlockPos pos) {
        // Only pull if we have room.
        int space = energy.getMaxEnergyStored() - energy.getEnergyStored();
        if (space <= 0) {
            return;
        }

        BlockEntity below = level.getBlockEntity(pos.below());
        if (!(below instanceof SolidFuelDynamoBlockEntity dynamo)) {
            return;
        }

        int request = Math.min(FE_PULL_PER_TICK, space);
        int extracted = dynamo.getEnergy().extractEnergy(request, true);
        if (extracted <= 0) {
            return;
        }

        int accepted = energy.receiveEnergy(extracted, false);
        if (accepted > 0) {
            dynamo.getEnergy().extractEnergy(accepted, false);
        }
    }

    private boolean tryMelt(Level level) {
        ItemStack input = getInputStack();
        if (input.isEmpty()) {
            meltProgress = 0;
            meltTimeTotal = 0;
            statusCode = STATUS_IDLE;
            return false;
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }

        Optional<RecipeHolder<MeltingRecipe>> match = serverLevel.getServer().getRecipeManager().getRecipeFor(
                VoltaicContent.MELTING_RECIPE_TYPE.get(),
                new MeltingRecipeInput(input),
                level
        );
        if (match.isEmpty()) {
            meltProgress = 0;
            meltTimeTotal = 0;
            statusCode = STATUS_NO_RECIPE;
            return false;
        }

        MeltingRecipe recipe = match.get().value();
        meltTimeTotal = Math.max(1, recipe.time());
        if (currentHeat < recipe.requiredHeat()) {
            meltProgress = 0;
            statusCode = energy.getEnergyStored() >= getCoilTier().fePerTick() ? STATUS_HEATING : STATUS_NO_POWER;
            return false;
        }

        // Ensure tank can accept the fluid.
        if (tank.fill(recipe.resultFluid(), recipe.resultAmountMb(), true) < recipe.resultAmountMb()) {
            statusCode = STATUS_TANK_FULL;
            return false;
        }

        statusCode = STATUS_MELTING;
        meltProgress++;
        if (meltProgress < meltTimeTotal) {
            return true;
        }

        // Finish: consume 1 input and fill tank.
        input.shrink(1);
        if (input.isEmpty()) {
            items.set(SLOT_INPUT, ItemStack.EMPTY);
        }
        tank.fill(recipe.resultFluid(), recipe.resultAmountMb(), false);
        meltProgress = 0;
        return true;
    }

    public int drainMoltenFluid(net.minecraft.world.level.material.Fluid fluid, int amountMb, boolean simulate) {
        int drained = tank.drain(fluid, amountMb, simulate);
        if (!simulate && drained > 0) {
            setChanged();
            syncToClient();
        }
        return drained;
    }

    private void syncToClient() {
        if (level == null || level.isClientSide()) {
            return;
        }
        BlockState state = getBlockState();
        level.sendBlockUpdated(worldPosition, state, state, 3);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, items);
        output.putInt("Energy", energy.getEnergyStored());
        output.putInt("Heat", currentHeat);
        output.putInt("MeltProgress", meltProgress);
        output.putInt("MeltTimeTotal", meltTimeTotal);
        output.putInt("StatusCode", statusCode);
        output.putInt("TankAmount", tank.getAmountMb());
        output.putInt("TankCapacity", tank.getCapacityMb());
        output.putInt("TankFluid", BuiltInRegistries.FLUID.getId(tank.getFluid()));
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        ContainerHelper.loadAllItems(input, items);
        energy.setEnergy(input.getIntOr("Energy", 0));
        currentHeat = input.getIntOr("Heat", 0);
        meltProgress = input.getIntOr("MeltProgress", 0);
        meltTimeTotal = input.getIntOr("MeltTimeTotal", 0);
        statusCode = input.getIntOr("StatusCode", STATUS_IDLE);

        int amount = input.getIntOr("TankAmount", 0);
        if (amount <= 0) {
            tank.set(Fluids.EMPTY, 0);
            return;
        }
        int fluidId = input.getIntOr("TankFluid", BuiltInRegistries.FLUID.getId(Fluids.EMPTY));
        tank.set(BuiltInRegistries.FLUID.byId(fluidId), amount);
    }
}

