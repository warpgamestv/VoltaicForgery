package com.warpgames.voltaicforgery.voltaic.blockentity;

import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import com.warpgames.voltaicforgery.voltaic.menu.PatternTableMenu;
import com.warpgames.voltaicforgery.voltaic.recipe.PatternToolPartRecipe;
import com.warpgames.voltaicforgery.voltaic.recipe.PatternToolPartRecipeInput;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
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
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PatternTableBlockEntity extends BlockEntity implements Container, MenuProvider {

    public static final int SLOT_PATTERN = 0;
    public static final int SLOT_MATERIAL = 1;
    public static final int SLOT_OUTPUT = 2;
    public static final int SLOTS = 3;

    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return index == 0 ? selectedPart : 0;
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) {
                setSelectedPart(value);
            }
        }

        @Override
        public int getCount() {
            return 1;
        }
    };
    private boolean updatingOutput;
    private int selectedPart;

    public PatternTableBlockEntity(BlockPos pos, BlockState state) {
        super(VoltaicContent.PATTERN_TABLE_BE.get(), pos, state);
    }

    public ContainerData getData() {
        return data;
    }

    public int getSelectedPart() {
        return selectedPart;
    }

    public void setSelectedPart(int selectedPart) {
        int clamped = Math.max(0, Math.min(partCount(level) - 1, selectedPart));
        if (this.selectedPart == clamped) return;
        this.selectedPart = clamped;
        updateOutput();
        setChanged();
    }

    public void consumeInputsForCraft() {
        if (getItem(SLOT_OUTPUT).isEmpty()) return;
        removeItem(SLOT_PATTERN, 1);
        if (!getItem(SLOT_MATERIAL).isEmpty()) {
            removeItem(SLOT_MATERIAL, 1);
        }
        updateOutput();
        setChanged();
    }

    public void updateOutput() {
        if (updatingOutput) return;
        updatingOutput = true;
        items.set(SLOT_OUTPUT, createResult());
        updatingOutput = false;
    }

    private ItemStack createResult() {
        ItemStack pattern = getItem(SLOT_PATTERN);
        if (pattern.isEmpty()) return ItemStack.EMPTY;
        if (!pattern.is(VoltaicContent.BLANK_PATTERN.get())) return ItemStack.EMPTY;

        ItemStack material = getItem(SLOT_MATERIAL);
        if (material.isEmpty() || level == null || level.isClientSide() || level.getServer() == null) {
            return ItemStack.EMPTY;
        }

        PatternToolPartRecipeInput input = new PatternToolPartRecipeInput(pattern, material, partType(level, selectedPart));
        Optional<RecipeHolder<PatternToolPartRecipe>> recipe = level.getServer().getRecipeManager().getRecipeFor(
                VoltaicContent.PATTERN_TOOL_PART_RECIPE_TYPE.get(),
                input,
                level
        );
        return recipe.map(holder -> holder.value().assemble(input)).orElse(ItemStack.EMPTY);
    }

    public static int partCount(Level level) {
        return partChoices(level).size();
    }

    public static String partType(Level level, int selectedPart) {
        List<PartChoice> choices = partChoices(level);
        int clamped = Math.max(0, Math.min(choices.size() - 1, selectedPart));
        return choices.get(clamped).part();
    }

    public static List<PartChoice> partChoices(Level level) {
        if (level == null || level.getServer() == null) {
            return fallbackChoices();
        }
        return partChoices(level.getServer().getRecipeManager());
    }

    public static List<PartChoice> partChoices(RecipeManager recipeManager) {
        if (recipeManager == null) {
            return fallbackChoices();
        }
        Map<String, PartChoice> choices = new LinkedHashMap<>();
        patternRecipes(recipeManager).stream()
                .sorted(Comparator
                        .comparingInt((PatternToolPartRecipe recipe) -> preferredOrder(recipe.part()))
                        .thenComparing(PatternToolPartRecipe::part)
                        .thenComparing(recipe -> recipe.result().id().toString()))
                .forEach(recipe -> choices.putIfAbsent(recipe.part(), new PartChoice(recipe.part(), recipe.icon().orElse(recipe.result().id()))));

        if (choices.isEmpty()) {
            return fallbackChoices();
        }
        return List.copyOf(choices.values());
    }

    private static List<PatternToolPartRecipe> patternRecipes(RecipeManager recipeManager) {
        RecipeType<PatternToolPartRecipe> type = VoltaicContent.PATTERN_TOOL_PART_RECIPE_TYPE.get();
        for (Method method : RecipeManager.class.getMethods()) {
            if (method.getParameterCount() != 1 || !RecipeType.class.isAssignableFrom(method.getParameterTypes()[0])) continue;
            try {
                List<PatternToolPartRecipe> recipes = extractPatternRecipes(method.invoke(recipeManager, type), type);
                if (!recipes.isEmpty()) return recipes;
            } catch (ReflectiveOperationException ignored) {
            }
        }
        for (String methodName : List.of("getRecipes", "recipes")) {
            try {
                Method method = RecipeManager.class.getMethod(methodName);
                List<PatternToolPartRecipe> recipes = extractPatternRecipes(method.invoke(recipeManager), type);
                if (!recipes.isEmpty()) return recipes;
            } catch (ReflectiveOperationException ignored) {
            }
        }
        return List.of();
    }

    private static List<PatternToolPartRecipe> extractPatternRecipes(Object source, RecipeType<PatternToolPartRecipe> type) {
        if (source == null) return List.of();
        List<PatternToolPartRecipe> out = new ArrayList<>();
        if (source instanceof Map<?, ?> map) {
            for (Object value : map.values()) {
                out.addAll(extractPatternRecipes(value, type));
            }
            return out;
        }
        if (source instanceof Iterable<?> iterable) {
            for (Object value : iterable) {
                if (value instanceof RecipeHolder<?> holder) {
                    Recipe<?> recipe = holder.value();
                    if (recipe.getType() == type && recipe instanceof PatternToolPartRecipe patternRecipe) {
                        out.add(patternRecipe);
                    }
                } else if (value instanceof PatternToolPartRecipe patternRecipe) {
                    out.add(patternRecipe);
                }
            }
        }
        return out;
    }

    private static List<PartChoice> fallbackChoices() {
        List<PartChoice> choices = new ArrayList<>();
        choices.add(new PartChoice("pickaxe_head", BuiltInRegistries.ITEM.getKey(VoltaicContent.PICKAXE_HEAD_PATTERN.get())));
        choices.add(new PartChoice("axe_head", BuiltInRegistries.ITEM.getKey(VoltaicContent.AXE_HEAD_PATTERN.get())));
        choices.add(new PartChoice("shovel_head", BuiltInRegistries.ITEM.getKey(VoltaicContent.SHOVEL_HEAD_PATTERN.get())));
        choices.add(new PartChoice("sword_head", BuiltInRegistries.ITEM.getKey(VoltaicContent.SWORD_HEAD_PATTERN.get())));
        choices.add(new PartChoice("tool_binding", BuiltInRegistries.ITEM.getKey(VoltaicContent.TOOL_BINDING_PATTERN.get())));
        choices.add(new PartChoice("tool_handle", BuiltInRegistries.ITEM.getKey(VoltaicContent.TOOL_HANDLE_PATTERN.get())));
        return List.copyOf(choices);
    }

    private static int preferredOrder(String part) {
        return switch (part) {
            case "pickaxe_head" -> 0;
            case "axe_head" -> 1;
            case "shovel_head" -> 2;
            case "sword_head" -> 3;
            case "tool_binding" -> 4;
            case "tool_handle" -> 5;
            default -> 100;
        };
    }

    public record PartChoice(String part, Identifier icon) {}

    public static boolean isPatternInput(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.is(VoltaicContent.BLANK_PATTERN.get());
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
        return Component.translatable("container.voltaicforgery.pattern_table");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new PatternTableMenu(containerId, playerInventory, this);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, items);
        output.putInt("SelectedPart", selectedPart);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        ContainerHelper.loadAllItems(input, items);
        selectedPart = Math.max(0, Math.min(partCount(level) - 1, input.getIntOr("SelectedPart", 0)));
        updateOutput();
    }
}
