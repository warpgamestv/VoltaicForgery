package com.warpgames.voltaicforgery.voltaic.blockentity;

import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import com.warpgames.voltaicforgery.voltaic.api.energy.VoltaicItemEnergy;
import com.warpgames.voltaicforgery.voltaic.item.component.ToolEnergyStorageComponent;
import com.warpgames.voltaicforgery.voltaic.item.ModularAxeItem;
import com.warpgames.voltaicforgery.voltaic.item.ModularPickaxeItem;
import com.warpgames.voltaicforgery.voltaic.item.ModularShovelItem;
import com.warpgames.voltaicforgery.voltaic.item.ModularSwordItem;
import com.warpgames.voltaicforgery.voltaic.menu.ModificationStationMenu;
import com.warpgames.voltaicforgery.voltaic.tool.ToolAssembly;
import com.warpgames.voltaicforgery.voltaic.tool.ToolMaterialResolver;
import com.warpgames.voltaicforgery.voltaic.tool.ToolMaterialStat;
import com.warpgames.voltaicforgery.voltaic.tool.ToolModifierEntry;
import com.warpgames.voltaicforgery.voltaic.tool.ToolModifierHelper;
import com.warpgames.voltaicforgery.voltaic.tool.ToolModifierQuery;
import com.warpgames.voltaicforgery.voltaic.tool.ToolModifierState;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Slot 0: assembled modular tool; Slot 1: repair material or modifier item; Slot 2: result preview.
 */
public class ModificationStationBlockEntity extends BlockEntity implements Container, MenuProvider {

    public static final int SLOT_TOOL = 0;
    public static final int SLOT_INPUT = 1;
    public static final int SLOT_OUTPUT = 2;
    public static final int SLOTS = 3;

    public static final int DEFAULT_MODIFIER_SLOTS = 3;
    public static final double REPAIR_FRACTION = 0.25D;

    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
    private boolean updatingOutput;

    public ModificationStationBlockEntity(BlockPos pos, BlockState state) {
        super(VoltaicContent.MODIFICATION_STATION_BE.get(), pos, state);
    }

    public void updateResult() {
        if (updatingOutput) {
            return;
        }
        updatingOutput = true;
        try {
            items.set(SLOT_OUTPUT, createPreview());
        } finally {
            updatingOutput = false;
        }
        setChanged();
    }

    private ItemStack createPreview() {
        ItemStack tool = items.get(SLOT_TOOL);
        ItemStack mat = items.get(SLOT_INPUT);
        if (tool.isEmpty() || mat.isEmpty() || !isModularTool(tool)) {
            return ItemStack.EMPTY;
        }
        Optional<ItemStack> repair = tryRepairPreview(tool, mat);
        if (repair.isPresent()) {
            return repair.get();
        }
        return tryModifyPreview(tool, mat).orElse(ItemStack.EMPTY);
    }

    public void onCrafted() {
        if (isModularTool(items.get(SLOT_TOOL)) && !items.get(SLOT_INPUT).isEmpty()) {
            items.get(SLOT_INPUT).shrink(1);
            if (items.get(SLOT_INPUT).isEmpty()) {
                items.set(SLOT_INPUT, ItemStack.EMPTY);
            }
            items.set(SLOT_TOOL, ItemStack.EMPTY);
        }
        updateResult();
        setChanged();
    }

    private static boolean isModularTool(ItemStack stack) {
        return stack.getItem() instanceof com.warpgames.voltaicforgery.voltaic.item.IModularTool;
    }

    private Optional<ItemStack> tryRepairPreview(ItemStack tool, ItemStack mat) {
        if (!isModularTool(tool) || !tool.isDamageableItem()) {
            return Optional.empty();
        }
        if (!tool.isDamaged()) {
            return Optional.empty();
        }
        HolderLookup.Provider registries = level == null ? null : level.registryAccess();
        ToolAssembly assembly = tool.getOrDefault(VoltaicContent.ASSEMBLED_TOOL.get(), ToolAssembly.DEFAULT);
        ToolMaterialStat head = ToolMaterialResolver.resolve(registries, assembly.headMaterial());
        if (head.repairIngredient().filter(ing -> ing.test(mat)).isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(applyDurabilityChange(tool, repairAmount(tool)));
    }

    private int repairAmount(ItemStack tool) {
        int maxD = tool.getMaxDamage();
        if (maxD <= 0) {
            return 0;
        }
        return Math.max(1, (int) Math.floor(maxD * REPAIR_FRACTION));
    }

    private Optional<ItemStack> tryModifyPreview(ItemStack tool, ItemStack mat) {
        HolderLookup.Provider registries = level == null ? null : level.registryAccess();
        Optional<ToolModifierQuery.ModifyMatch> match = ToolModifierQuery.findForToolAndMaterial(registries, tool, mat);
        if (match.isEmpty()) {
            return Optional.empty();
        }
        ToolModifierQuery.ModifyMatch m = match.get();
        if (m.startingTier() && m.tier().slotCost() > getRemainingModifierSlots(tool)) {
            return Optional.empty();
        }
        List<String> normalized = ToolTraits.normalizeIds(List.of(m.entry().trait()));
        if (normalized.isEmpty()) {
            return Optional.empty();
        }
        String trait = normalized.get(0);
        return Optional.of(applyModifier(tool, trait, m.ingredientValue(), m.tier(), m.state(), m.startingTier(), registries));
    }

    private static List<String> currentTraits(ItemStack tool) {
        return List.copyOf(tool.getOrDefault(VoltaicContent.TOOL_TRAITS.get(), List.of()));
    }

    private static List<ToolModifierState> currentModifierStates(ItemStack tool) {
        return List.copyOf(tool.getOrDefault(VoltaicContent.TOOL_MODIFIERS.get(), List.of()));
    }

    private int getRemainingModifierSlots(ItemStack tool) {
        return tool.getOrDefault(VoltaicContent.TOOL_MODIFIER_SLOTS.get(), DEFAULT_MODIFIER_SLOTS);
    }

    private ItemStack applyModifier(
            ItemStack tool,
            String trait,
            int value,
            ToolModifierEntry.Tier tier,
            ToolModifierState state,
            boolean startingTier,
            HolderLookup.Provider registries
    ) {
        ItemStack out = tool.copy();
        ToolModifierState started = startingTier ? state.withStartedTier(tier.level(), tier.requiredValue(), tier.effectValue()) : state;
        ToolModifierState nextState = started.withProgress(started.progress() + value, tier.requiredValue());
        out.set(VoltaicContent.TOOL_MODIFIERS.get(), ToolModifierState.replace(currentModifierStates(out), nextState));
        if (startingTier) {
            int slotsLeft = getRemainingModifierSlots(out) - tier.slotCost();
            out.set(VoltaicContent.TOOL_MODIFIER_SLOTS.get(), Math.max(0, slotsLeft));
        }

        List<String> nextTraits = new ArrayList<>(currentTraits(out));
        if (nextState.activeLevel() > state.activeLevel() && nextTraits.stream().noneMatch(t -> t.equalsIgnoreCase(trait))) {
            nextTraits.add(trait);
            out.set(VoltaicContent.TOOL_TRAITS.get(), List.copyOf(ToolTraits.normalizeIds(nextTraits)));
        }
        if (ToolTraits.POWERED.equalsIgnoreCase(trait) && nextState.activeLevel() > state.activeLevel()) {
            long maxEnergy = tier.effectValue() > 0.0F ? Math.round(tier.effectValue()) : ToolEnergyStorageComponent.DEFAULT_MAX_ENERGY;
            VoltaicItemEnergy.setEnergyComponent(out, new ToolEnergyStorageComponent(0, maxEnergy));
        }
        applyDerivedComponents(out, registries);
        return out;
    }

    private static void applyDerivedComponents(ItemStack out, HolderLookup.Provider registries) {
        if (out.getItem() instanceof ModularPickaxeItem) {
            ModularPickaxeItem.applyDerivedComponents(out, registries);
        } else if (out.getItem() instanceof ModularAxeItem) {
            ModularAxeItem.applyDerivedComponents(out, registries);
        } else if (out.getItem() instanceof ModularShovelItem) {
            ModularShovelItem.applyDerivedComponents(out, registries);
        } else if (out.getItem() instanceof ModularSwordItem) {
            ModularSwordItem.applyDerivedComponents(out, registries);
        }
    }

    private static ItemStack applyDurabilityChange(ItemStack tool, int removeDamage) {
        ItemStack out = tool.copy();
        int current = out.getDamageValue();
        int next = Math.max(0, current - removeDamage);
        out.setDamageValue(next);
        return out;
    }

    @Override
    public int getContainerSize() {
        return SLOTS;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                return false;
            }
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
        if (!removed.isEmpty() && slot != SLOT_OUTPUT) {
            updateResult();
            setChanged();
        }
        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = items.get(slot);
        items.set(slot, ItemStack.EMPTY);
        if (slot != SLOT_OUTPUT) {
            updateResult();
        }
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        if (slot != SLOT_OUTPUT) {
            updateResult();
        }
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        items.clear();
        updateResult();
    }

    @Override
    public Iterator<ItemStack> iterator() {
        return items.iterator();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.voltaicforgery.modification_station");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new ModificationStationMenu(containerId, playerInventory, this);
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
        updateResult();
    }
}
