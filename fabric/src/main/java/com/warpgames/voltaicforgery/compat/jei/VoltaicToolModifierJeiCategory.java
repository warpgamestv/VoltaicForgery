package com.warpgames.voltaicforgery.compat.jei;

import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import com.warpgames.voltaicforgery.voltaic.tool.ToolModifierDisplay;
import com.warpgames.voltaicforgery.voltaic.tool.ToolModifierEntry;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class VoltaicToolModifierJeiCategory implements IRecipeCategory<ToolModifierDisplay> {

    private final IDrawable icon;

    public VoltaicToolModifierJeiCategory(IGuiHelper gui) {
        this.icon = gui.createDrawableItemStack(new ItemStack(VoltaicContent.MODIFICATION_STATION.get()));
    }

    @Override
    public IRecipeType<ToolModifierDisplay> getRecipeType() {
        return VoltaicJeiTypes.TOOL_MODIFIER;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.voltaicforgery.tool_modifier");
    }

    @Override
    public int getWidth() {
        return 170;
    }

    @Override
    public int getHeight() {
        return 64;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ToolModifierDisplay recipe, IFocusGroup focuses) {
        List<ToolModifierEntry.IngredientValue> ingredients = recipe.entry().ingredients();
        for (int i = 0; i < Math.min(ingredients.size(), 4); i++) {
            builder.addSlot(RecipeIngredientRole.INPUT, 8 + (i * 22), 12)
                    .add(ingredients.get(i).ingredient());
        }

        builder.addSlot(RecipeIngredientRole.CRAFTING_STATION, 144, 12)
                .add(new ItemStack(VoltaicContent.MODIFICATION_STATION.get()));
    }

    @Override
    public void draw(ToolModifierDisplay recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        var font = Minecraft.getInstance().font;
        ToolModifierEntry entry = recipe.entry();
        guiGraphics.text(font, Component.translatable("trait.voltaicforgery." + entry.trait()), 8, 2, 0xFF55D6FF, false);
        guiGraphics.text(font, Component.literal("Tools: " + tools(entry.allowedTools())), 8, 34, 0xFFAAAAAA, false);
        guiGraphics.text(font, Component.literal("Tiers: " + tiers(entry.tiers())), 8, 46, 0xFFE0B84E, false);
        drawIngredientValues(guiGraphics, entry.ingredients());
    }

    private static void drawIngredientValues(GuiGraphicsExtractor guiGraphics, List<ToolModifierEntry.IngredientValue> ingredients) {
        var font = Minecraft.getInstance().font;
        for (int i = 0; i < Math.min(ingredients.size(), 4); i++) {
            int value = ingredients.get(i).value();
            if (value > 1) {
                guiGraphics.text(font, Component.literal("+" + value), 10 + (i * 22), 28, 0xFFFFFFFF, false);
            }
        }
    }

    private static String tools(List<String> allowedTools) {
        return allowedTools.isEmpty() ? "all" : String.join(", ", allowedTools);
    }

    private static String tiers(List<ToolModifierEntry.Tier> tiers) {
        return tiers.stream()
                .map(tier -> tier.level() + "=" + tier.requiredValue())
                .reduce((left, right) -> left + ", " + right)
                .orElse("none");
    }
}
