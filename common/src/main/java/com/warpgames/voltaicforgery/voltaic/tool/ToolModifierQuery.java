package com.warpgames.voltaicforgery.voltaic.tool;

import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import com.warpgames.voltaicforgery.voltaic.registry.VoltaicRegistries;
import com.warpgames.voltaicforgery.voltaic.trait.ToolTraits;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public final class ToolModifierQuery {

    private ToolModifierQuery() {}

    /** Prefer {@link #findForToolAndMaterial} so tier-specific ingredients (coils) resolve correctly. */
    @Deprecated
    public record Match(ToolModifierEntry entry, int value) {}

    /**
     * Full match including tier state; use from modification station and client validation.
     */
    public record ModifyMatch(ToolModifierEntry entry, int ingredientValue, ToolModifierEntry.Tier tier, ToolModifierState state, boolean startingTier) {}

    /**
     * Finds a modifier application where {@code mat} is a valid ingredient for the tool's current tier step.
     */
    public static Optional<ModifyMatch> findForToolAndMaterial(HolderLookup.Provider registries, ItemStack tool, ItemStack mat) {
        if (registries == null || tool.isEmpty() || mat.isEmpty()) {
            return Optional.empty();
        }
        List<ToolModifierState> states = tool.getOrDefault(VoltaicContent.TOOL_MODIFIERS.get(), List.of());
        String toolType = ToolModifierHelper.toolType(tool);
        return registries.lookup(VoltaicRegistries.TOOL_MODIFIERS)
                .stream()
                .flatMap(HolderLookup.RegistryLookup::listElements)
                .map(Holder::value)
                .filter(entry -> entry.allowsTool(toolType))
                .flatMap(entry -> {
                    List<String> normalized = ToolTraits.normalizeIds(List.of(entry.trait()));
                    if (normalized.isEmpty()) {
                        return java.util.stream.Stream.empty();
                    }
                    String trait = normalized.get(0);
                    ToolModifierState state = ToolModifierState.find(states, trait).orElse(new ToolModifierState(trait, 0, 0, 0));
                    boolean startingTier = !state.hasStartedTier();
                    int tierLevel = startingTier ? state.activeLevel() + 1 : state.startedTier();
                    Optional<ToolModifierEntry.Tier> tierOpt = entry.tier(tierLevel);
                    if (tierOpt.isEmpty()) {
                        return java.util.stream.Stream.empty();
                    }
                    return entry.ingredients().stream()
                            .filter(ing -> ing.ingredient().test(mat))
                            .filter(ing -> ing.forTier() < 0 || ing.forTier() == tierLevel)
                            .map(ing -> new ModifyMatch(entry, ing.value(), tierOpt.get(), state, startingTier));
                })
                .findFirst();
    }

    /**
     * Legacy: match material without tier context (only safe when modifiers use ingredients with {@code for_tier &lt; 0} only).
     */
    public static Optional<Match> findForStack(HolderLookup.Provider registries, ItemStack stack) {
        if (registries == null || stack.isEmpty()) {
            return Optional.empty();
        }
        return registries.lookup(VoltaicRegistries.TOOL_MODIFIERS)
                .stream()
                .flatMap(HolderLookup.RegistryLookup::listElements)
                .map(Holder::value)
                .flatMap(entry -> entry.ingredients().stream()
                        .filter(ingredient -> ingredient.ingredient().test(stack))
                        .filter(ingredient -> ingredient.forTier() < 0)
                        .map(ingredient -> new Match(entry, ingredient.value())))
                .findFirst();
    }
}
