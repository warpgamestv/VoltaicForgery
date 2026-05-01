package com.warpgames.voltaicforgery.voltaic.menu;

import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import com.warpgames.voltaicforgery.voltaic.blockentity.SolidFuelDynamoBlockEntity;
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

public class SolidFuelDynamoMenu extends AbstractContainerMenu {

    private static final int DATA_COUNT = 6;
    private static final int WORD_MASK = 0xFFFF;
    private static final int DYNAMO_SLOT_COUNT = SolidFuelDynamoBlockEntity.INVENTORY_SLOTS;
    private static final int PLAYER_INVENTORY_START = DYNAMO_SLOT_COUNT;
    private static final int PLAYER_INVENTORY_END = PLAYER_INVENTORY_START + 27;
    private static final int HOTBAR_START = PLAYER_INVENTORY_END;
    private static final int HOTBAR_END = HOTBAR_START + 9;

    private final Container dynamoInventory;
    private final ContainerData data;
    private final ContainerLevelAccess access;

    public SolidFuelDynamoMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(SolidFuelDynamoBlockEntity.INVENTORY_SLOTS), new SimpleContainerData(DATA_COUNT), ContainerLevelAccess.NULL);
    }

    public SolidFuelDynamoMenu(int containerId, Inventory playerInventory, SolidFuelDynamoBlockEntity blockEntity) {
        this(containerId, playerInventory, blockEntity, blockEntity.getData(), ContainerLevelAccess.create(playerInventory.player.level(), blockEntity.getBlockPos()));
    }

    private SolidFuelDynamoMenu(int containerId, Inventory playerInventory, Container dynamoInventory, ContainerData data, ContainerLevelAccess access) {
        super(VoltaicContent.SOLID_FUEL_DYNAMO_MENU.get(), containerId);
        this.dynamoInventory = dynamoInventory;
        this.data = data;
        this.access = access;

        checkContainerSize(dynamoInventory, SolidFuelDynamoBlockEntity.INVENTORY_SLOTS);
        checkContainerDataCount(data, DATA_COUNT);
        dynamoInventory.startOpen(playerInventory.player);

        addSlot(new FuelSlot(dynamoInventory, SolidFuelDynamoBlockEntity.SLOT_FUEL, 82, 44));
        addSlot(new ChargeSlot(dynamoInventory, SolidFuelDynamoBlockEntity.SLOT_CHARGE, 138, 44));
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
        addDataSlots(data);
    }

    public int getEnergyStored() {
        return joinWords(data.get(0), data.get(1));
    }

    public int getMaxEnergyStored() {
        return joinWords(data.get(2), data.get(3));
    }

    public int getBurnTime() {
        return data.get(4);
    }

    public int getBurnTimeTotal() {
        return data.get(5);
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
        if (index < DYNAMO_SLOT_COUNT) {
            if (!moveItemStackTo(stack, PLAYER_INVENTORY_START, HOTBAR_END, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            // Player -> Machine: prefer fuel slot first, then charge slot.
            if (!moveItemStackTo(stack, SolidFuelDynamoBlockEntity.SLOT_FUEL, SolidFuelDynamoBlockEntity.SLOT_FUEL + 1, false)
                    && !moveItemStackTo(stack, SolidFuelDynamoBlockEntity.SLOT_CHARGE, SolidFuelDynamoBlockEntity.SLOT_CHARGE + 1, false)) {
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
        return stillValid(access, player, VoltaicContent.SOLID_FUEL_DYNAMO.get());
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        dynamoInventory.stopOpen(player);
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

    private static class FuelSlot extends Slot {

        private FuelSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }
    }

    private static class ChargeSlot extends Slot {

        private ChargeSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }
    }

    private static int joinWords(int low, int high) {
        return (low & WORD_MASK) | ((high & WORD_MASK) << 16);
    }
}
