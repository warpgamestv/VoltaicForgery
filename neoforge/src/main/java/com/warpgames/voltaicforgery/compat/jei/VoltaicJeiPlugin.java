package com.warpgames.voltaicforgery.compat.jei;

import com.warpgames.voltaicforgery.Constants;
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

import java.lang.reflect.Method;
import java.util.List;

@JeiPlugin
public final class VoltaicJeiPlugin implements IModPlugin {

    private static final Identifier UID = Identifier.fromNamespaceAndPath("voltaicforgery", "jei_plugin");

    public VoltaicJeiPlugin() {
        Constants.LOG.info("[JEI] VoltaicJeiPlugin constructed (NeoForge)");
    }

    @Override
    public Identifier getPluginUid() {
        Constants.LOG.info("[JEI] getPluginUid called (NeoForge)");
        return UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        Constants.LOG.info("[JEI] Registering Voltaic categories (NeoForge)");
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
        if (manager == null) {
            Constants.LOG.warn("[JEI] RecipeManager not available yet; skipping recipe registration (NeoForge)");
            return;
        }

        List<CastingRecipe> casting = VoltaicJeiRecipeUtil.getAllRecipesFor(manager, VoltaicContent.CASTING_RECIPE_TYPE.get());
        registration.addRecipes(VoltaicJeiTypes.CASTING, casting);

        List<MeltingRecipe> melting = VoltaicJeiRecipeUtil.getAllRecipesFor(manager, VoltaicContent.MELTING_RECIPE_TYPE.get());
        registration.addRecipes(VoltaicJeiTypes.MELTING, melting);

        List<PatternToolPartRecipe> patternToolParts = VoltaicJeiRecipeUtil.getAllRecipesFor(manager, VoltaicContent.PATTERN_TOOL_PART_RECIPE_TYPE.get());
        registration.addRecipes(VoltaicJeiTypes.PATTERN_TOOL_PART, patternToolParts);

        List<ToolModifierDisplay> modifiers = ToolModifierDisplay.all(Minecraft.getInstance().level == null ? null : Minecraft.getInstance().level.registryAccess());
        registration.addRecipes(VoltaicJeiTypes.TOOL_MODIFIER, modifiers);

        Constants.LOG.info("[JEI] Registered recipes (NeoForge): casting={}, melting={}, pattern_tool_part={}, tool_modifier={}", casting.size(), melting.size(), patternToolParts.size(), modifiers.size());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        Constants.LOG.info("[JEI] Registering Voltaic catalysts (NeoForge)");
        registration.addCraftingStation(VoltaicJeiTypes.PATTERN_TOOL_PART, VoltaicContent.PATTERN_TABLE.get());
        registration.addCraftingStation(VoltaicJeiTypes.CASTING, VoltaicContent.CASTING_TABLE.get());
        registration.addCraftingStation(VoltaicJeiTypes.MELTING, VoltaicContent.INDUCTION_CRUCIBLE.get());
        registration.addCraftingStation(VoltaicJeiTypes.TOOL_MODIFIER, VoltaicContent.MODIFICATION_STATION.get());
    }

    private static RecipeManager tryGetRecipeManager() {
        Minecraft mc = Minecraft.getInstance();

        // 1) If a level is loaded, pull from the level (works for both singleplayer & multiplayer clients)
        if (mc.level != null) {
            RecipeManager fromLevel = reflectRecipeManager(mc.level);
            if (fromLevel != null) {
                return fromLevel;
            }
        }

        // 2) If we're in a singleplayer menu but server is up, use integrated server
        if (mc.getSingleplayerServer() != null) {
            return mc.getSingleplayerServer().getRecipeManager();
        }

        // 3) As a last attempt, try the connection (method names differ across patches)
        if (mc.getConnection() != null) {
            RecipeManager fromConn = reflectRecipeManager(mc.getConnection());
            if (fromConn != null) {
                return fromConn;
            }
        }

        return null;
    }

    private static RecipeManager reflectRecipeManager(Object holder) {
        for (String name : List.of("getRecipeManager", "recipeManager", "recipes")) {
            try {
                Method m = holder.getClass().getMethod(name);
                Object result = m.invoke(holder);
                if (result instanceof RecipeManager rm) {
                    return rm;
                }
            } catch (ReflectiveOperationException ignored) {
            }
        }
        // brute-force: any no-arg method that returns RecipeManager
        for (Method m : holder.getClass().getMethods()) {
            if (m.getParameterCount() == 0 && RecipeManager.class.isAssignableFrom(m.getReturnType())) {
                try {
                    Object result = m.invoke(holder);
                    if (result instanceof RecipeManager rm) {
                        return rm;
                    }
                } catch (ReflectiveOperationException ignored) {
                }
            }
        }
        return null;
    }
}

