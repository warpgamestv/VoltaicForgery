package com.warpgames.voltaicforgery.voltaic.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record AssemblyRecipeInput(ItemStack head, ItemStack binding, ItemStack handle) implements RecipeInput {
    @Override
    public ItemStack getItem(int index) {
        return switch (index) {
            case 0 -> head;
            case 1 -> binding;
            case 2 -> handle;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public int size() {
        return 3;
    }
}
