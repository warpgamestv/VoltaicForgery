package com.warpgames.voltaicforgery.compat.jei;

import com.warpgames.voltaicforgery.voltaic.recipe.CastingRecipe;
import com.warpgames.voltaicforgery.voltaic.recipe.MeltingRecipe;
import com.warpgames.voltaicforgery.voltaic.recipe.PatternToolPartRecipe;
import com.warpgames.voltaicforgery.voltaic.tool.ToolModifierDisplay;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.resources.Identifier;

public final class VoltaicJeiTypes {
    private VoltaicJeiTypes() {
    }

    public static final IRecipeType<CastingRecipe> CASTING =
            IRecipeType.create(Identifier.fromNamespaceAndPath("voltaicforgery", "casting"), CastingRecipe.class);

    public static final IRecipeType<MeltingRecipe> MELTING =
            IRecipeType.create(Identifier.fromNamespaceAndPath("voltaicforgery", "melting"), MeltingRecipe.class);

    public static final IRecipeType<PatternToolPartRecipe> PATTERN_TOOL_PART =
            IRecipeType.create(Identifier.fromNamespaceAndPath("voltaicforgery", "pattern_tool_part"), PatternToolPartRecipe.class);

    public static final IRecipeType<ToolModifierDisplay> TOOL_MODIFIER =
            IRecipeType.create(Identifier.fromNamespaceAndPath("voltaicforgery", "tool_modifier"), ToolModifierDisplay.class);
}

