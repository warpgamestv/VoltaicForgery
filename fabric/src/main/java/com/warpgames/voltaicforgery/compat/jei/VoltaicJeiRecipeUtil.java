package com.warpgames.voltaicforgery.compat.jei;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

final class VoltaicJeiRecipeUtil {
    private VoltaicJeiRecipeUtil() {
    }

    static <T extends Recipe<?>> List<T> getAllRecipesFor(RecipeManager manager, RecipeType<T> type) {
        for (Method m : RecipeManager.class.getMethods()) {
            if (m.getParameterCount() != 1) continue;
            if (!RecipeType.class.isAssignableFrom(m.getParameterTypes()[0])) continue;
            try {
                Object result = m.invoke(manager, type);
                List<T> extracted = extractRecipeValues(result);
                if (!extracted.isEmpty()) return extracted;
            } catch (ReflectiveOperationException ignored) {
            }
        }

        for (String methodName : List.of("getAllRecipesFor", "getAllRecipesForType")) {
            try {
                Method m = RecipeManager.class.getMethod(methodName, RecipeType.class);
                Object result = m.invoke(manager, type);
                List<T> extracted = extractRecipeValues(result);
                if (!extracted.isEmpty()) return extracted;
            } catch (ReflectiveOperationException ignored) {
            }
        }

        for (String methodName : List.of("getRecipes", "recipes")) {
            try {
                Method m = RecipeManager.class.getMethod(methodName);
                Object result = m.invoke(manager);
                if (result instanceof Map<?, ?> root) {
                    Object byType = root.get(type);
                    List<T> extracted = extractRecipeValues(byType);
                    if (!extracted.isEmpty()) return extracted;
                } else {
                    List<T> extracted = extractRecipeValues(result, type);
                    if (!extracted.isEmpty()) return extracted;
                }
            } catch (ReflectiveOperationException ignored) {
            }
        }

        return List.of();
    }

    private static <T extends Recipe<?>> List<T> extractRecipeValues(Object result) {
        return extractRecipeValues(result, null);
    }

    private static <T extends Recipe<?>> List<T> extractRecipeValues(Object result, RecipeType<T> expectedType) {
        if (result == null) return List.of();

        List<T> out = new ArrayList<>();

        if (result instanceof Map<?, ?> map) {
            for (Object v : map.values()) {
                out.addAll(extractRecipeValues(v, expectedType));
            }
            return out;
        }

        if (result instanceof Iterable<?> it) {
            for (Object o : it) {
                if (o instanceof RecipeHolder<?> rh) {
                    Recipe<?> recipe = rh.value();
                    if (expectedType == null || recipe.getType() == expectedType) {
                        @SuppressWarnings("unchecked")
                        T v = (T) recipe;
                        out.add(v);
                    }
                } else if (o instanceof Recipe<?> r) {
                    if (expectedType == null || r.getType() == expectedType) {
                        @SuppressWarnings("unchecked")
                        T v = (T) r;
                        out.add(v);
                    }
                }
            }
        }

        return out;
    }

    static Identifier syntheticId(String path) {
        return Identifier.fromNamespaceAndPath("voltaicforgery", path);
    }
}

