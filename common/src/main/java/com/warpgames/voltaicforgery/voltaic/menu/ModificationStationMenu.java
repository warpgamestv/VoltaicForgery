package com.warpgames.voltaicforgery.voltaic.menu;

import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import com.warpgames.voltaicforgery.voltaic.blockentity.ModificationStationBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ModificationStationMenu extends AbstractContainerMenu {

    private static final int STATION_SLOTS = ModificationStationBlockEntity.SLOTS;
    private static final int PLAYER_INVENTORY_START = STATION_SLOTS;
    private static final int PLAYER_INVENTORY_END = PLAYER_INVENTORY_START + 27;
    private static final int HOTBAR_START = PLAYER_INVENTORY_END;
    private static final int HOTBAR_END = HOTBAR_START + 9;

    private final Container stationInventory;
    private final ContainerLevelAccess access;

    public ModificationStationMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(ModificationStationBlockEntity.SLOTS), ContainerLevelAccess.NULL);
    }

    public ModificationStationMenu(int containerId, Inventory playerInventory, ModificationStationBlockEntity blockEntity) {
        this(containerId, playerInventory, blockEntity, ContainerLevelAccess.create(playerInventory.player.level(), blockEntity.getBlockPos()));
    }

    private ModificationStationMenu(int containerId, Inventory playerInventory, Container stationInventory, ContainerLevelAccess access) {
        super(VoltaicContent.MODIFICATION_STATION_MENU.get(), containerId);
        this.stationInventory = stationInventory;
        this.access = access;

        checkContainerSize(stationInventory, STATION_SLOTS);
        stationInventory.startOpen(playerInventory.player);

        addSlot(new Slot(stationInventory, ModificationStationBlockEntity.SLOT_TOOL, 48, 44));
        addSlot(new Slot(stationInventory, ModificationStationBlockEntity.SLOT_INPUT, 100, 44));
        addSlot(new ResultSlot(stationInventory, ModificationStationBlockEntity.SLOT_OUTPUT, 154, 44));

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack original = slot.getItem().copy();
        ItemStack stack = slot.getItem();

        if (index == ModificationStationBlockEntity.SLOT_OUTPUT) {
            if (!this.moveItemStackTo(stack, PLAYER_INVENTORY_START, HOTBAR_END, true)) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, stack);
        } else if (index < STATION_SLOTS) {
            if (!this.moveItemStackTo(stack, PLAYER_INVENTORY_START, HOTBAR_END, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < PLAYER_INVENTORY_END) {
            if (!this.moveItemStackTo(stack, ModificationStationBlockEntity.SLOT_TOOL, ModificationStationBlockEntity.SLOT_INPUT + 1, false)) {
                if (!this.moveItemStackTo(stack, HOTBAR_START, HOTBAR_END, false)) {
                    return ItemStack.EMPTY;
                }
            }
        } else if (!this.moveItemStackTo(stack, PLAYER_INVENTORY_START, PLAYER_INVENTORY_END, false)) {
            return ItemStack.EMPTY;
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
        return stillValid(access, player, VoltaicContent.MODIFICATION_STATION.get());
    }

    public ItemStack getToolStack() {
        return stationInventory.getItem(ModificationStationBlockEntity.SLOT_TOOL);
    }

    public ItemStack getInputStack() {
        return stationInventory.getItem(ModificationStationBlockEntity.SLOT_INPUT);
    }

    public ItemStack getOutputStack() {
        return stationInventory.getItem(ModificationStationBlockEntity.SLOT_OUTPUT);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.stationInventory.stopOpen(player);
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(playerInventory, column + row * 9 + 9, 30 + column * 18, 108 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(playerInventory, column, 30 + column * 18, 166));
        }
    }

    private static class ResultSlot extends Slot {

        private ResultSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public void onTake(Player player, ItemStack stack) {
            super.onTake(player, stack);
            if (container instanceof ModificationStationBlockEntity be) {
                be.onCrafted();
            }
        }
    }
}
