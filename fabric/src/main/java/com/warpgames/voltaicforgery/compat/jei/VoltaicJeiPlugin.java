package com.warpgames.voltaicforgery.compat.jei;

import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import com.warpgames.voltaicforgery.voltaic.recipe.CastingRecipe;
import com.warpgames.voltaicforgery.voltaic.recipe.MeltingRecipe;
import com.warpgames.voltaicforgery.voltaic.recipe.PatternToolPartRecipe;
import com.warpgames.voltaicforgery.voltaic.tool.ToolModifierDisplay;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

@JeiPlugin
public final class VoltaicJeiPlugin implements IModPlugin {

    private static final Identifier UID = Identifier.fromNamespaceAndPath("voltaicforgery", "jei_plugin_fabric");

    @Override
    public Identifier getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var gui = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new VoltaicPatternToolPartJeiCategory(gui),
                new VoltaicCastingJeiCategory(gui),
                new VoltaicMeltingJeiCategory(gui),
                new VoltaicToolModifierJeiCategory(gui)
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager manager = tryGetRecipeManager();
        if (manager == null) return;

        List<CastingRecipe> casting = VoltaicJeiRecipeUtil.getAllRecipesFor(manager, VoltaicContent.CASTING_RECIPE_TYPE.get());
        registration.addRecipes(VoltaicJeiTypes.CASTING, casting);

        List<MeltingRecipe> melting = VoltaicJeiRecipeUtil.getAllRecipesFor(manager, VoltaicContent.MELTING_RECIPE_TYPE.get());
        registration.addRecipes(VoltaicJeiTypes.MELTING, melting);

        List<PatternToolPartRecipe> patternToolParts = VoltaicJeiRecipeUtil.getAllRecipesFor(manager, VoltaicContent.PATTERN_TOOL_PART_RECIPE_TYPE.get());
        registration.addRecipes(VoltaicJeiTypes.PATTERN_TOOL_PART, patternToolParts);

        List<ToolModifierDisplay> modifiers = ToolModifierDisplay.all(Minecraft.getInstance().level == null ? null : Minecraft.getInstance().level.registryAccess());
        registration.addRecipes(VoltaicJeiTypes.TOOL_MODIFIER, modifiers);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(VoltaicJeiTypes.PATTERN_TOOL_PART, VoltaicContent.PATTERN_TABLE.get());
        registration.addCraftingStation(VoltaicJeiTypes.CASTING, VoltaicContent.CASTING_TABLE.get());
        registration.addCraftingStation(VoltaicJeiTypes.MELTING, VoltaicContent.INDUCTION_CRUCIBLE.get());
        registration.addCraftingStation(VoltaicJeiTypes.TOOL_MODIFIER, VoltaicContent.MODIFICATION_STATION.get());
    }

    private static RecipeManager tryGetRecipeManager() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getSingleplayerServer() != null) {
            return mc.getSingleplayerServer().getRecipeManager();
        }
        return null;
    }
}

