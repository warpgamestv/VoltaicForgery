package com.warpgames.voltaicforgery.voltaic.blockentity;

import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import com.warpgames.voltaicforgery.voltaic.api.energy.SimpleEnergyStorage;
import com.warpgames.voltaicforgery.voltaic.api.energy.IEnergyTool;
import com.warpgames.voltaicforgery.voltaic.api.energy.VFEnergyStorage;
import com.warpgames.voltaicforgery.platform.Services;
import com.warpgames.voltaicforgery.voltaic.block.SolidFuelDynamoBlock;
import com.warpgames.voltaicforgery.voltaic.menu.SolidFuelDynamoMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.FuelValues;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Iterator;

public class SolidFuelDynamoBlockEntity extends BlockEntity implements Container, MenuProvider {

    public static final int INVENTORY_SLOTS = 2;
    public static final int SLOT_FUEL = 0;
    public static final int SLOT_CHARGE = 1;

    // Balance knobs
    public static final int ENERGY_CAPACITY = 100_000;
    public static final int FE_PER_TICK = 40;
    public static final int CHARGE_FE_PER_TICK = 200;
    private static final int WORD_MASK = 0xFFFF;

    private final NonNullList<ItemStack> items = NonNullList.withSize(INVENTORY_SLOTS, ItemStack.EMPTY);
    private final SimpleEnergyStorage energy = new SimpleEnergyStorage(ENERGY_CAPACITY, 0, 2_000);
    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> lowWord(energy.getEnergyStored());
                case 1 -> highWord(energy.getEnergyStored());
                case 2 -> lowWord(energy.getMaxEnergyStored());
                case 3 -> highWord(energy.getMaxEnergyStored());
                case 4 -> burnTime;
                case 5 -> burnTimeTotal;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> energy.setEnergy(joinWords(value, highWord(energy.getEnergyStored())));
                case 1 -> energy.setEnergy(joinWords(lowWord(energy.getEnergyStored()), value));
                case 4 -> burnTime = value;
                case 5 -> burnTimeTotal = value;
                default -> {
                }
            }
        }

        @Override
        public int getCount() {
            return 6;
        }
    };

    private int burnTime;
    private int burnTimeTotal;

    public SolidFuelDynamoBlockEntity(BlockPos pos, BlockState state) {
        super(VoltaicContent.SOLID_FUEL_DYNAMO_BE.get(), pos, state);
    }

    public VFEnergyStorage getEnergy() {
        return energy;
    }

    public ContainerData getData() {
        return data;
    }

    public ItemStack getFuelStack() {
        return items.get(SLOT_FUEL);
    }

    public void setFuelStack(ItemStack stack) {
        setItem(SLOT_FUEL, stack);
    }

    public ItemStack getChargeStack() {
        return items.get(SLOT_CHARGE);
    }

    public void setChargeStack(ItemStack stack) {
        setItem(SLOT_CHARGE, stack);
    }

    @Override
    public int getContainerSize() {
        return INVENTORY_SLOTS;
    }

    @Override
    public boolean isEmpty() {
        return getFuelStack().isEmpty() && getChargeStack().isEmpty();
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
        return Component.translatable("container.voltaicforgery.solid_fuel_dynamo");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new SolidFuelDynamoMenu(containerId, playerInventory, this);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SolidFuelDynamoBlockEntity be) {
        boolean changed = false;
        boolean wasBurning = be.burnTime > 0;

        if (be.burnTime > 0) {
            be.burnTime--;
            // Dynamo is output-only, so "generation" is direct energy accumulation.
            int space = be.energy.getMaxEnergyStored() - be.energy.getEnergyStored();
            int gen = Math.min(space, FE_PER_TICK);
            if (gen > 0) {
                be.energy.setEnergy(be.energy.getEnergyStored() + gen);
                changed = true;
            }
        }

        boolean hasSpace = be.energy.getEnergyStored() < be.energy.getMaxEnergyStored();
        if (be.burnTime <= 0 && hasSpace) {
            ItemStack fuel = be.getFuelStack();
            int fuelTicks = getBurnTimeTicks(level.fuelValues(), fuel);
            if (fuelTicks > 0) {
                be.burnTime = fuelTicks;
                be.burnTimeTotal = fuelTicks;
                fuel.shrink(1);
                changed = true;
            }
        }

        boolean burning = be.burnTime > 0;
        if (wasBurning != burning && state.hasProperty(SolidFuelDynamoBlock.LIT)) {
            level.setBlock(pos, state.setValue(SolidFuelDynamoBlock.LIT, burning), 3);
            changed = true;
        }

        // Charge items in the charge/output slot from internal energy.
        if (be.energy.getEnergyStored() > 0) {
            ItemStack charge = be.getChargeStack();
            if (!charge.isEmpty()) {
                int available = be.energy.extractEnergy(CHARGE_FE_PER_TICK, true);
                if (available > 0) {
                    long accepted = receiveEnergy(charge, (long) available);
                    if (accepted > 0L) {
                        be.energy.extractEnergy((int) Math.min((long) Integer.MAX_VALUE, accepted), false);
                        changed = true;
                    }
                }
            }
        }

        if (changed) {
            be.setChanged();
        }
    }

    private static long receiveEnergy(ItemStack stack, long maxReceive) {
        if (maxReceive <= 0L) {
            return 0L;
        }
        // First: VF native energy tools (data-component-backed).
        if (stack.getItem() instanceof IEnergyTool tool) {
            return tool.receiveEnergy(stack, maxReceive, false);
        }
        // Second: loader-specific energy capability (NeoForge FE/RF item capability; Fabric bridge is Priority 3).
        return Services.CAPABILITIES.receiveEnergyToItem(stack, maxReceive, false);
    }

    private static int getBurnTimeTicks(FuelValues fuelValues, ItemStack stack) {
        if (stack == null || stack.isEmpty()) return 0;
        return fuelValues.burnDuration(stack);
    }

    private static int lowWord(int value) {
        return value & WORD_MASK;
    }

    private static int highWord(int value) {
        return (value >>> 16) & WORD_MASK;
    }

    private static int joinWords(int low, int high) {
        return (low & WORD_MASK) | ((high & WORD_MASK) << 16);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, items);
        output.putInt("Energy", energy.getEnergyStored());
        output.putInt("BurnTime", burnTime);
        output.putInt("BurnTimeTotal", burnTimeTotal);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        ContainerHelper.loadAllItems(input, items);
        energy.setEnergy(input.getIntOr("Energy", 0));
        burnTime = input.getIntOr("BurnTime", 0);
        burnTimeTotal = input.getIntOr("BurnTimeTotal", 0);
    }
}

