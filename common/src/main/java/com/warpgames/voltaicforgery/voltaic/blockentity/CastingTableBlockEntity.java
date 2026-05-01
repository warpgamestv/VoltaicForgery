package com.warpgames.voltaicforgery.voltaic.blockentity;

import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import com.warpgames.voltaicforgery.voltaic.api.fluid.SimpleFluidTank;
import com.warpgames.voltaicforgery.voltaic.api.fluid.VFFluidTank;
import com.warpgames.voltaicforgery.voltaic.recipe.CastingRecipe;
import com.warpgames.voltaicforgery.voltaic.recipe.CastingRecipeInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Iterator;
import java.util.Optional;

public class CastingTableBlockEntity extends BlockEntity implements Container {

    public static final int SLOTS = 2;
    public static final int SLOT_CAST = 0;
    public static final int SLOT_OUTPUT = 1;
    public static final int TANK_CAPACITY_MB = 1_000;
    public static final int COOLING_DURATION_TICKS = 100;

    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
    private final SimpleFluidTank tank = new SimpleFluidTank(TANK_CAPACITY_MB);

    private int coolingTime;
    private int matchedRecipeAmountMb;
    private boolean pendingConsumeCast;
    private ItemStack pendingResult = ItemStack.EMPTY;

    public CastingTableBlockEntity(BlockPos pos, BlockState state) {
        super(VoltaicContent.CASTING_TABLE_BE.get(), pos, state);
    }

    public VFFluidTank getTank() {
        return tank;
    }

    public int getCoolingTime() {
        return coolingTime;
    }

    public int getMatchedRecipeAmountMb() {
        return matchedRecipeAmountMb;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CastingTableBlockEntity be) {
        boolean changed = false;

        if (be.coolingTime > 0) {
            be.coolingTime--;
            changed = true;
            if (be.coolingTime == 0) {
                be.finishCasting();
            }
        } else if (be.pendingResult.isEmpty()) {
            // Don't start casting while a faucet above is still actively pouring into us.
            if (!be.isFaucetAbovePouring(level, pos)) {
                changed = be.tryStartCasting(level);
            }
        }
        if (changed) {
            be.setChanged();
            be.syncToClient();
        }
    }

    public int fill(net.minecraft.world.level.material.Fluid fluid, int amountMb, boolean simulate) {
        int filled = tank.fill(fluid, amountMb, simulate);
        if (!simulate && filled > 0) {
            refreshMatchedRecipeAmount();
            setChanged();
            syncToClient();
        }
        return filled;
    }

    private boolean tryStartCasting(Level level) {
        ItemStack cast = getItem(SLOT_CAST);
        if (cast.isEmpty() || tank.isEmpty()) return false;

        CastingRecipeInput input = new CastingRecipeInput(cast, tank.getFluid(), tank.getAmountMb());
        Optional<RecipeHolder<CastingRecipe>> recipe = level.getServer().getRecipeManager().getRecipeFor(
                VoltaicContent.CASTING_RECIPE_TYPE.get(),
                input,
                level
        );
        if (recipe.isEmpty()) return false;

        CastingRecipe castingRecipe = recipe.get().value();
        ItemStack result = castingRecipe.assemble(input);
        if (result.isEmpty() || !canAcceptOutput(result)) return false;

        int drained = tank.drain(tank.getFluid(), castingRecipe.fluid().amountMb(), false);
        if (drained != castingRecipe.fluid().amountMb()) return false;

        pendingResult = result;
        pendingConsumeCast = castingRecipe.consumeCast();
        coolingTime = COOLING_DURATION_TICKS;
        refreshMatchedRecipeAmount();
        return true;
    }

    private void finishCasting() {
        if (pendingResult.isEmpty()) return;
        ItemStack output = getItem(SLOT_OUTPUT);
        if (output.isEmpty()) {
            setItem(SLOT_OUTPUT, pendingResult.copy());
        } else if (ItemStack.isSameItemSameComponents(output, pendingResult)) {
            output.grow(pendingResult.getCount());
            setChanged();
            syncToClient();
        }
        if (pendingConsumeCast) {
            ItemStack cast = getItem(SLOT_CAST);
            if (!cast.isEmpty()) {
                cast.shrink(1);
                if (cast.isEmpty()) {
                    setItem(SLOT_CAST, ItemStack.EMPTY);
                }
            }
        }
        pendingResult = ItemStack.EMPTY;
        pendingConsumeCast = false;
    }

    private boolean canAcceptOutput(ItemStack result) {
        ItemStack output = getItem(SLOT_OUTPUT);
        if (output.isEmpty()) return true;
        if (!ItemStack.isSameItemSameComponents(output, result)) return false;
        return output.getCount() + result.getCount() <= output.getMaxStackSize();
    }

    @Override
    public int getContainerSize() {
        return SLOTS;
    }

    @Override
    public boolean isEmpty() {
        return items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack removed = ContainerHelper.removeItem(items, slot, amount);
        if (!removed.isEmpty()) {
            refreshMatchedRecipeAmount();
            setChanged();
            syncToClient();
        }
        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = items.get(slot);
        items.set(slot, ItemStack.EMPTY);
        if (!stack.isEmpty()) {
            refreshMatchedRecipeAmount();
            setChanged();
            syncToClient();
        }
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) stack.setCount(getMaxStackSize());
        refreshMatchedRecipeAmount();
        setChanged();
        syncToClient();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        items.clear();
        refreshMatchedRecipeAmount();
        setChanged();
        syncToClient();
    }

    private void refreshMatchedRecipeAmount() {
        if (level == null || level.isClientSide() || level.getServer() == null) return;

        ItemStack cast = getItem(SLOT_CAST);
        if (cast.isEmpty() || tank.isEmpty()) {
            matchedRecipeAmountMb = 0;
            return;
        }

        CastingRecipeInput input = new CastingRecipeInput(cast, tank.getFluid(), Integer.MAX_VALUE);
        Optional<RecipeHolder<CastingRecipe>> recipe = level.getServer().getRecipeManager().getRecipeFor(
                VoltaicContent.CASTING_RECIPE_TYPE.get(),
                input,
                level
        );
        matchedRecipeAmountMb = recipe.map(holder -> holder.value().fluid().amountMb()).orElse(0);
    }

    private boolean isFaucetAbovePouring(Level level, BlockPos pos) {
        BlockEntity above = level.getBlockEntity(pos.above());
        return above instanceof CastingFaucetBlockEntity faucet && faucet.isPouring();
    }

    private void syncToClient() {
        if (level == null || level.isClientSide()) return;
        BlockState state = getBlockState();
        level.sendBlockUpdated(worldPosition, state, state, 3);
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
    public Iterator<ItemStack> iterator() {
        return items.iterator();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, items);
        output.putInt("CoolingTime", coolingTime);
        output.putInt("MatchedRecipeAmountMb", matchedRecipeAmountMb);
        output.putBoolean("PendingConsumeCast", pendingConsumeCast);
        output.store("PendingResult", ItemStack.OPTIONAL_CODEC, pendingResult);
        output.putString("TankFluid", BuiltInRegistries.FLUID.getKey(tank.getFluid()).toString());
        output.putInt("TankAmount", tank.getAmountMb());
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        items.replaceAll(ignored -> ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, items);
        coolingTime = input.getIntOr("CoolingTime", 0);
        matchedRecipeAmountMb = input.getIntOr("MatchedRecipeAmountMb", 0);
        pendingConsumeCast = input.getBooleanOr("PendingConsumeCast", false);
        pendingResult = input.read("PendingResult", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);

        String fluidId = input.getStringOr("TankFluid", BuiltInRegistries.FLUID.getKey(Fluids.EMPTY).toString());
        int amount = input.getIntOr("TankAmount", 0);
        tank.set(BuiltInRegistries.FLUID.getValue(net.minecraft.resources.Identifier.parse(fluidId)), amount);
    }
}
