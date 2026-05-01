package com.warpgames.voltaicforgery.compat.jei;

import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import com.warpgames.voltaicforgery.voltaic.recipe.MeltingRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class VoltaicMeltingJeiCategory implements IRecipeCategory<MeltingRecipe> {

    private final IDrawable icon;

    public VoltaicMeltingJeiCategory(IGuiHelper gui) {
        this.icon = gui.createDrawableItemStack(new ItemStack(VoltaicContent.INDUCTION_CRUCIBLE.get()));
    }

    @Override
    public IRecipeType<MeltingRecipe> getRecipeType() {
        return VoltaicJeiTypes.MELTING;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.voltaicforgery.melting");
    }

    @Override
    public int getWidth() {
        return 150;
    }

    @Override
    public int getHeight() {
        return 40;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MeltingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 10, 12)
                .add(recipe.input());

        var fluid = BuiltInRegistries.FLUID.getOptional(recipe.resultFluidId()).orElse(null);
        if (fluid != null) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 120, 12)
                    .add(fluid, recipe.resultAmountMb())
                    .setFluidRenderer(4_000, true, 16, 16);
        }
    }
}

