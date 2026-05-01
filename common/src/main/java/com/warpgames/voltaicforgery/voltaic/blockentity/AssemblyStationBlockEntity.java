package com.warpgames.voltaicforgery.voltaic.blockentity;

import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import com.warpgames.voltaicforgery.voltaic.item.ModularAxeItem;
import com.warpgames.voltaicforgery.voltaic.item.ModularPickaxeItem;
import com.warpgames.voltaicforgery.voltaic.item.ModularShovelItem;
import com.warpgames.voltaicforgery.voltaic.item.ModularSwordItem;
import com.warpgames.voltaicforgery.voltaic.item.ToolPartItem;
import com.warpgames.voltaicforgery.voltaic.menu.AssemblyStationMenu;
import com.warpgames.voltaicforgery.voltaic.tool.ToolMaterialResolver;
import com.warpgames.voltaicforgery.voltaic.tool.ToolMaterialStat;
import com.warpgames.voltaicforgery.voltaic.tool.ToolAssembly;
import com.warpgames.voltaicforgery.voltaic.trait.ToolTraits;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.world.item.Item;

public class AssemblyStationBlockEntity extends BlockEntity implements Container, MenuProvider {

    public static final int SLOT_HEAD = 0;
    public static final int SLOT_BINDING = 1;
    public static final int SLOT_HANDLE = 2;
    public static final int SLOT_OUTPUT = 3;
    public static final int SLOTS = 4;

    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
    private boolean updatingOutput;

    public AssemblyStationBlockEntity(BlockPos pos, BlockState state) {
        super(VoltaicContent.ASSEMBLY_STATION_BE.get(), pos, state);
    }

    public void consumeInputsForCraft() {
        removeItem(SLOT_HEAD, 1);
        removeItem(SLOT_BINDING, 1);
        removeItem(SLOT_HANDLE, 1);
        updateOutput();
        setChanged();
    }

    public void updateOutput() {
        if (updatingOutput) return;
        updatingOutput = true;

        ItemStack result = createResult();
        items.set(SLOT_OUTPUT, result);

        updatingOutput = false;
    }

    private ItemStack createResult() {
        ItemStack head = items.get(SLOT_HEAD);
        ItemStack binding = items.get(SLOT_BINDING);
        ItemStack handle = items.get(SLOT_HANDLE);

        if (head.isEmpty() || binding.isEmpty() || handle.isEmpty()) {
            return ItemStack.EMPTY;
        }

        if (level == null || level.isClientSide()) {
            return ItemStack.EMPTY; // Need server level for recipes, or just fallback if needed. But let's try.
        }

        com.warpgames.voltaicforgery.voltaic.recipe.AssemblyRecipeInput input = new com.warpgames.voltaicforgery.voltaic.recipe.AssemblyRecipeInput(head, binding, handle);
        java.util.Optional<net.minecraft.world.item.crafting.RecipeHolder<com.warpgames.voltaicforgery.voltaic.recipe.AssemblyRecipe>> recipe = level.getServer().getRecipeManager().getRecipeFor(
                VoltaicContent.ASSEMBLY_RECIPE_TYPE.get(),
                input,
                level
        );

        if (recipe.isEmpty()) {
            return createFallbackResult(input);
        }

        return recipe.get().value().assemble(input, level.registryAccess());
    }

    private ItemStack createFallbackResult(com.warpgames.voltaicforgery.voltaic.recipe.AssemblyRecipeInput input) {
        ItemStack head = input.head();
        if (head.isEmpty() || !(head.getItem() instanceof ToolPartItem)) {
            return ItemStack.EMPTY;
        }

        Item resultItem;
        if (ToolPartItem.isPartType(head, ToolPartItem.PICKAXE_HEAD)) {
            resultItem = VoltaicContent.MODULAR_PICKAXE.get();
        } else if (ToolPartItem.isPartType(head, ToolPartItem.AXE_HEAD)) {
            resultItem = VoltaicContent.MODULAR_AXE.get();
        } else if (ToolPartItem.isPartType(head, ToolPartItem.SHOVEL_HEAD)) {
            resultItem = VoltaicContent.MODULAR_SHOVEL.get();
        } else if (ToolPartItem.isPartType(head, ToolPartItem.SWORD_HEAD)) {
            resultItem = VoltaicContent.MODULAR_SWORD.get();
        } else {
            return ItemStack.EMPTY;
        }

        // Fabric can occasionally miss custom assembly recipes at runtime; fallback keeps station functional.
        ToolAssembly assembly = new ToolAssembly(
                ToolPartItem.materialId(input.head()),
                ToolPartItem.materialId(input.binding()),
                ToolPartItem.materialId(input.handle())
        );
        ItemStack result = new ItemStack(resultItem);
        result.set(VoltaicContent.ASSEMBLED_TOOL.get(), assembly);

        Set<String> traits = new LinkedHashSet<>();
        addMaterialTraits(traits, ToolMaterialResolver.resolve(level.registryAccess(), assembly.headMaterial()));
        addMaterialTraits(traits, ToolMaterialResolver.resolve(level.registryAccess(), assembly.bindingMaterial()));
        addMaterialTraits(traits, ToolMaterialResolver.resolve(level.registryAccess(), assembly.handleMaterial()));
        result.set(VoltaicContent.TOOL_TRAITS.get(), List.copyOf(ToolTraits.normalizeIds(List.copyOf(traits))));
        result.set(VoltaicContent.TOOL_MODIFIER_SLOTS.get(), ModificationStationBlockEntity.DEFAULT_MODIFIER_SLOTS);
        result.set(VoltaicContent.TOOL_MODIFIERS.get(), List.of());

        if (resultItem instanceof ModularPickaxeItem) {
            ModularPickaxeItem.applyDerivedComponents(result, level.registryAccess());
        } else if (resultItem instanceof ModularAxeItem) {
            ModularAxeItem.applyDerivedComponents(result, level.registryAccess());
        } else if (resultItem instanceof ModularShovelItem) {
            ModularShovelItem.applyDerivedComponents(result, level.registryAccess());
        } else if (resultItem instanceof ModularSwordItem) {
            ModularSwordItem.applyDerivedComponents(result, level.registryAccess());
        }
        return result;
    }

    private static void addMaterialTraits(Set<String> traits, ToolMaterialStat material) {
        traits.addAll(material.traits());
    }

    public static boolean isToolPart(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof ToolPartItem && stack.has(VoltaicContent.TOOL_PART_MATERIAL.get());
    }

    public static boolean isToolPart(ItemStack stack, String partType) {
        return isToolPart(stack) && ToolPartItem.isPartType(stack, partType);
    }

    @Override
    public int getContainerSize() {
        return SLOTS;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack removed = ContainerHelper.removeItem(items, slot, amount);
        if (!removed.isEmpty()) {
            if (slot != SLOT_OUTPUT) updateOutput();
            setChanged();
        }
        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = items.get(slot);
        items.set(slot, ItemStack.EMPTY);
        if (slot != SLOT_OUTPUT) updateOutput();
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        if (slot != SLOT_OUTPUT) updateOutput();
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        items.clear();
        updateOutput();
    }

    @Override
    public Iterator<ItemStack> iterator() {
        return items.iterator();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.voltaicforgery.assembly_station");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new AssemblyStationMenu(containerId, playerInventory, this);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, items);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        ContainerHelper.loadAllItems(input, items);
        updateOutput();
    }
}

