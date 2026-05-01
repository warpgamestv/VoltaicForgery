package com.warpgames.voltaicforgery.voltaic.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.material.Fluid;

public record CastingRecipeInput(ItemStack cast, Fluid fluid, int amountMb) implements RecipeInput {

    @Override
    public ItemStack getItem(int index) {
        return index == 0 ? cast : ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return 1;
    }
}
