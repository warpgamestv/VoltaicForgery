package com.warpgames.voltaicforgery.voltaic.menu;

import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import com.warpgames.voltaicforgery.voltaic.blockentity.PatternTableBlockEntity;
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

public class PatternTableMenu extends AbstractContainerMenu {

    private static final int DATA_COUNT = 1;
    private static final int TABLE_SLOT_COUNT = PatternTableBlockEntity.SLOTS;
    private static final int PLAYER_INVENTORY_START = TABLE_SLOT_COUNT;
    private static final int PLAYER_INVENTORY_END = PLAYER_INVENTORY_START + 27;
    private static final int HOTBAR_START = PLAYER_INVENTORY_END;
    private static final int HOTBAR_END = HOTBAR_START + 9;

    private final Container tableInventory;
    private final ContainerData data;
    private final ContainerLevelAccess access;

    public PatternTableMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(PatternTableBlockEntity.SLOTS), new SimpleContainerData(DATA_COUNT), ContainerLevelAccess.NULL);
    }

    public PatternTableMenu(int containerId, Inventory playerInventory, PatternTableBlockEntity blockEntity) {
        this(containerId, playerInventory, blockEntity, blockEntity.getData(), ContainerLevelAccess.create(playerInventory.player.level(), blockEntity.getBlockPos()));
    }

    private PatternTableMenu(int containerId, Inventory playerInventory, Container tableInventory, ContainerData data, ContainerLevelAccess access) {
        super(VoltaicContent.PATTERN_TABLE_MENU.get(), containerId);
        this.tableInventory = tableInventory;
        this.data = data;
        this.access = access;

        checkContainerSize(tableInventory, PatternTableBlockEntity.SLOTS);
        checkContainerDataCount(data, DATA_COUNT);
        tableInventory.startOpen(playerInventory.player);

        addSlot(new PatternSlot(tableInventory, PatternTableBlockEntity.SLOT_PATTERN, 89, 35));
        addSlot(new Slot(tableInventory, PatternTableBlockEntity.SLOT_MATERIAL, 123, 35));
        addSlot(new ResultSlot(tableInventory, PatternTableBlockEntity.SLOT_OUTPUT, 169, 35));

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
        addDataSlots(data);
    }

    public int getSelectedPart() {
        return data.get(0);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        int partCount = tableInventory instanceof PatternTableBlockEntity patternTable
                ? PatternTableBlockEntity.partCount(patternTable.getLevel())
                : PatternTableBlockEntity.partCount(player.level());
        if (id < 0 || id >= partCount) {
            return false;
        }
        if (tableInventory instanceof PatternTableBlockEntity patternTable) {
            patternTable.setSelectedPart(id);
        } else {
            data.set(0, id);
        }
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack original = slot.getItem().copy();
        ItemStack stack = slot.getItem();

        if (index == PatternTableBlockEntity.SLOT_OUTPUT) {
            if (!moveItemStackTo(stack, PLAYER_INVENTORY_START, HOTBAR_END, true)) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, stack);
        } else if (index < TABLE_SLOT_COUNT) {
            if (!moveItemStackTo(stack, PLAYER_INVENTORY_START, HOTBAR_END, false)) {
                return ItemStack.EMPTY;
            }
        } else if (PatternTableBlockEntity.isPatternInput(stack)) {
            if (!moveItemStackTo(stack, PatternTableBlockEntity.SLOT_PATTERN, PatternTableBlockEntity.SLOT_PATTERN + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(stack, PatternTableBlockEntity.SLOT_MATERIAL, PatternTableBlockEntity.SLOT_MATERIAL + 1, false)) {
            if (index < PLAYER_INVENTORY_END) {
                if (!moveItemStackTo(stack, HOTBAR_START, HOTBAR_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stack, PLAYER_INVENTORY_START, PLAYER_INVENTORY_END, false)) {
                return ItemStack.EMPTY;
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
        return stillValid(access, player, VoltaicContent.PATTERN_TABLE.get());
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        tableInventory.stopOpen(player);
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInventory, column + row * 9 + 9, 22 + column * 18, 104 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(playerInventory, column, 22 + column * 18, 162));
        }
    }

    private static class PatternSlot extends Slot {
        private PatternSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return PatternTableBlockEntity.isPatternInput(stack);
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
            if (container instanceof PatternTableBlockEntity patternTable) {
                patternTable.consumeInputsForCraft();
            }
        }
    }
}
