package com.warpgames.voltaicforgery.voltaic.tool;

import com.warpgames.voltaicforgery.voltaic.registry.VoltaicRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public final class ToolModifierQuery {

    private ToolModifierQuery() {}

    public record Match(ToolModifierEntry entry, int value) {
    }

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
                        .map(ingredient -> new Match(entry, ingredient.value())))
                .findFirst();
    }
}
