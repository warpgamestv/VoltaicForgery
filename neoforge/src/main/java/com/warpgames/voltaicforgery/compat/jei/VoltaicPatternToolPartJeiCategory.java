package com.warpgames.voltaicforgery.compat.jei;

import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import com.warpgames.voltaicforgery.voltaic.recipe.PatternToolPartRecipe;
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

public final class VoltaicPatternToolPartJeiCategory implements IRecipeCategory<PatternToolPartRecipe> {

    private final IDrawable icon;

    public VoltaicPatternToolPartJeiCategory(IGuiHelper gui) {
        this.icon = gui.createDrawableItemStack(new ItemStack(VoltaicContent.PATTERN_TABLE.get()));
    }

    @Override
    public IRecipeType<PatternToolPartRecipe> getRecipeType() {
        return VoltaicJeiTypes.PATTERN_TOOL_PART;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.voltaicforgery.pattern_tool_part");
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
    public void setRecipe(IRecipeLayoutBuilder builder, PatternToolPartRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 10, 12)
                .add(recipe.pattern());

        builder.addSlot(RecipeIngredientRole.INPUT, 34, 12)
                .add(recipe.material());

        builder.addSlot(RecipeIngredientRole.INPUT, 72, 12)
                .add(new ItemStack(BuiltInRegistries.ITEM.getValue(recipe.icon().orElse(recipe.result().id()))));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 120, 12)
                .add(recipe.resultStack());
    }
}
