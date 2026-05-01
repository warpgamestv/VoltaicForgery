package com.warpgames.voltaicforgery.voltaic.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record PatternToolPartRecipeInput(ItemStack pattern, ItemStack material, String partType) implements RecipeInput {
    @Override
    public ItemStack getItem(int index) {
        return switch (index) {
            case 0 -> pattern;
            case 1 -> material;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return pattern.isEmpty() && material.isEmpty();
    }
}
