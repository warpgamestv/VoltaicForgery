package com.warpgames.voltaicforgery.voltaic.menu;

import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import com.warpgames.voltaicforgery.voltaic.blockentity.InductionCrucibleBlockEntity;
import com.warpgames.voltaicforgery.voltaic.item.CoilUpgradeItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class InductionCrucibleMenu extends AbstractContainerMenu {

    private static final int DATA_COUNT = 10;
    private static final int CRUCIBLE_SLOT_COUNT = InductionCrucibleBlockEntity.SLOTS;
    private static final int PLAYER_INVENTORY_START = CRUCIBLE_SLOT_COUNT;
    private static final int PLAYER_INVENTORY_END = PLAYER_INVENTORY_START + 27;
    private static final int HOTBAR_START = PLAYER_INVENTORY_END;
    private static final int HOTBAR_END = HOTBAR_START + 9;

    private final Container crucibleInventory;
    private final ContainerData data;
    private final ContainerLevelAccess access;

    public InductionCrucibleMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(InductionCrucibleBlockEntity.SLOTS), new SimpleContainerData(DATA_COUNT), ContainerLevelAccess.NULL);
    }

    public InductionCrucibleMenu(int containerId, Inventory playerInventory, InductionCrucibleBlockEntity blockEntity) {
        this(containerId, playerInventory, blockEntity, blockEntity.getData(), ContainerLevelAccess.create(playerInventory.player.level(), blockEntity.getBlockPos()));
    }

    private InductionCrucibleMenu(int containerId, Inventory playerInventory, Container crucibleInventory, ContainerData data, ContainerLevelAccess access) {
        super(VoltaicContent.INDUCTION_CRUCIBLE_MENU.get(), containerId);
        this.crucibleInventory = crucibleInventory;
        this.data = data;
        this.access = access;

        checkContainerSize(crucibleInventory, InductionCrucibleBlockEntity.SLOTS);
        checkContainerDataCount(data, DATA_COUNT);
        crucibleInventory.startOpen(playerInventory.player);

        addSlot(new InputSlot(crucibleInventory, InductionCrucibleBlockEntity.SLOT_INPUT, 95, 44));
        addSlot(new CoilSlot(crucibleInventory, InductionCrucibleBlockEntity.SLOT_COIL, 119, 44));
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
        addDataSlots(data);
    }

    public int getEnergyStored() {
        return data.get(0);
    }

    public int getMaxEnergyStored() {
        return data.get(1);
    }

    public int getCurrentHeat() {
        return data.get(2);
    }

    public int getMaxHeat() {
        return data.get(3);
    }

    public Fluid getFluid() {
        Fluid fluid = BuiltInRegistries.FLUID.byId(data.get(4));
        return fluid == null ? Fluids.EMPTY : fluid;
    }

    public Identifier getFluidId() {
        return BuiltInRegistries.FLUID.getKey(getFluid());
    }

    public int getFluidAmount() {
        return data.get(5);
    }

    public int getFluidCapacity() {
        return data.get(6);
    }

    public int getMeltProgress() {
        return data.get(7);
    }

    public int getMeltTimeTotal() {
        return data.get(8);
    }

    public int getStatusCode() {
        return data.get(9);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack original = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        original = stack.copy();

        // Machine -> Player
        if (index < CRUCIBLE_SLOT_COUNT) {
            if (!moveItemStackTo(stack, PLAYER_INVENTORY_START, HOTBAR_END, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            // Player -> Machine
            if (stack.getItem() instanceof CoilUpgradeItem) {
                if (!moveItemStackTo(stack, InductionCrucibleBlockEntity.SLOT_COIL, InductionCrucibleBlockEntity.SLOT_COIL + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stack, InductionCrucibleBlockEntity.SLOT_INPUT, InductionCrucibleBlockEntity.SLOT_INPUT + 1, false)) {
                // Player inventory <-> hotbar swap
                if (index < PLAYER_INVENTORY_END) {
                    if (!moveItemStackTo(stack, HOTBAR_START, HOTBAR_END, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!moveItemStackTo(stack, PLAYER_INVENTORY_START, PLAYER_INVENTORY_END, false)) {
                    return ItemStack.EMPTY;
                }
            }
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return original;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, VoltaicContent.INDUCTION_CRUCIBLE.get());
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        crucibleInventory.stopOpen(player);
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInventory, column + row * 9 + 9, 30 + column * 18, 108 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(playerInventory, column, 30 + column * 18, 166));
        }
    }

    private static class InputSlot extends Slot {
        private InputSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }
    }

    private static class CoilSlot extends Slot {

        private CoilSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.getItem() instanceof CoilUpgradeItem;
        }
    }
}
