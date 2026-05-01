package com.warpgames.voltaicforgery.compat.jei;

import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import com.warpgames.voltaicforgery.voltaic.recipe.CastingRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class VoltaicCastingJeiCategory implements IRecipeCategory<CastingRecipe> {

    private final IDrawable icon;

    public VoltaicCastingJeiCategory(IGuiHelper gui) {
        this.icon = gui.createDrawableItemStack(new ItemStack(VoltaicContent.CASTING_TABLE.get()));
    }

    @Override
    public IRecipeType<CastingRecipe> getRecipeType() {
        return VoltaicJeiTypes.CASTING;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.voltaicforgery.casting");
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
    public void setRecipe(IRecipeLayoutBuilder builder, CastingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 10, 12)
                .add(recipe.cast());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 120, 12)
                .add(recipe.resultStack());
    }
}

