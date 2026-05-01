package com.warpgames.voltaicforgery.voltaic.menu;

import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import com.warpgames.voltaicforgery.voltaic.blockentity.AssemblyStationBlockEntity;
import com.warpgames.voltaicforgery.voltaic.item.ToolPartItem;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class AssemblyStationMenu extends AbstractContainerMenu {

    private static final int ASSEMBLY_SLOT_COUNT = AssemblyStationBlockEntity.SLOTS;
    private static final int PLAYER_INVENTORY_START = ASSEMBLY_SLOT_COUNT;
    private static final int PLAYER_INVENTORY_END = PLAYER_INVENTORY_START + 27;
    private static final int HOTBAR_START = PLAYER_INVENTORY_END;
    private static final int HOTBAR_END = HOTBAR_START + 9;

    private final Container assemblyInventory;
    private final ContainerLevelAccess access;

    public AssemblyStationMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(AssemblyStationBlockEntity.SLOTS), ContainerLevelAccess.NULL);
    }

    public AssemblyStationMenu(int containerId, Inventory playerInventory, AssemblyStationBlockEntity blockEntity) {
        this(containerId, playerInventory, blockEntity, ContainerLevelAccess.create(playerInventory.player.level(), blockEntity.getBlockPos()));
    }

    private AssemblyStationMenu(int containerId, Inventory playerInventory, Container assemblyInventory, ContainerLevelAccess access) {
        super(VoltaicContent.ASSEMBLY_STATION_MENU.get(), containerId);
        this.assemblyInventory = assemblyInventory;
        this.access = access;

        checkContainerSize(assemblyInventory, AssemblyStationBlockEntity.SLOTS);
        assemblyInventory.startOpen(playerInventory.player);

        addSlot(new HeadSlot(assemblyInventory, AssemblyStationBlockEntity.SLOT_HEAD, 68, 44));
        addSlot(new BindingSlot(assemblyInventory, AssemblyStationBlockEntity.SLOT_BINDING, 92, 44));
        addSlot(new HandleSlot(assemblyInventory, AssemblyStationBlockEntity.SLOT_HANDLE, 116, 44));
        addSlot(new OutputSlot(assemblyInventory, AssemblyStationBlockEntity.SLOT_OUTPUT, 166, 44));

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
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

        if (index == AssemblyStationBlockEntity.SLOT_OUTPUT) {
            if (!moveItemStackTo(stack, PLAYER_INVENTORY_START, HOTBAR_END, true)) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, stack);
        } else if (index < ASSEMBLY_SLOT_COUNT) {
            if (!moveItemStackTo(stack, PLAYER_INVENTORY_START, HOTBAR_END, false)) {
                return ItemStack.EMPTY;
            }
        } else if (AssemblyStationBlockEntity.isToolPart(stack)) {
            // Try the 3 dedicated input slots in order: head, binding, handle.
            if (!moveItemStackTo(stack, AssemblyStationBlockEntity.SLOT_HEAD, AssemblyStationBlockEntity.SLOT_HEAD + 1, false)
                    && !moveItemStackTo(stack, AssemblyStationBlockEntity.SLOT_BINDING, AssemblyStationBlockEntity.SLOT_BINDING + 1, false)
                    && !moveItemStackTo(stack, AssemblyStationBlockEntity.SLOT_HANDLE, AssemblyStationBlockEntity.SLOT_HANDLE + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < PLAYER_INVENTORY_END) {
            if (!moveItemStackTo(stack, HOTBAR_START, HOTBAR_END, false)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(stack, PLAYER_INVENTORY_START, PLAYER_INVENTORY_END, false)) {
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
        return stillValid(access, player, VoltaicContent.ASSEMBLY_STATION.get());
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        assemblyInventory.stopOpen(player);
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

    private static class HeadSlot extends Slot {
        private HeadSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return AssemblyStationBlockEntity.isToolPart(stack)
                    && (ToolPartItem.isPartType(stack, ToolPartItem.PICKAXE_HEAD)
                    || ToolPartItem.isPartType(stack, ToolPartItem.AXE_HEAD)
                    || ToolPartItem.isPartType(stack, ToolPartItem.SHOVEL_HEAD)
                    || ToolPartItem.isPartType(stack, ToolPartItem.SWORD_HEAD));
        }
    }

    private static class BindingSlot extends Slot {
        private BindingSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return AssemblyStationBlockEntity.isToolPart(stack, ToolPartItem.TOOL_BINDING);
        }
    }

    private static class HandleSlot extends Slot {
        private HandleSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return AssemblyStationBlockEntity.isToolPart(stack, ToolPartItem.TOOL_HANDLE);
        }
    }

    private static class OutputSlot extends Slot {

        private OutputSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public void onTake(Player player, ItemStack stack) {
            super.onTake(player, stack);
            if (container instanceof AssemblyStationBlockEntity assemblyStation) {
                assemblyStation.consumeInputsForCraft();
            }
        }
    }
}

