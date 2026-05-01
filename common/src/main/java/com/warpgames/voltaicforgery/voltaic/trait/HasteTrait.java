package com.warpgames.voltaicforgery.voltaic.trait;

import com.warpgames.voltaicforgery.voltaic.tool.ToolModifierHelper;
import com.warpgames.voltaicforgery.voltaic.tool.ToolModifierState;
import net.minecraft.world.item.ItemStack;

/**
 * Haste gradually increases mining speed as each started tier gains progress.
 */
public final class HasteTrait implements ToolTrait {

    public static float applyMiningSpeed(ItemStack stack, float baseSpeed) {
        return baseSpeed * multiplier(stack);
    }

    public static float multiplier(ItemStack stack) {
        return ToolModifierHelper.state(stack, ToolTraits.HASTE)
                .map(HasteTrait::multiplier)
                .orElse(1.0F);
    }

    public static float multiplier(ToolModifierState state) {
        if (state.startedEffectValue() > 0.0F) {
            float previous = state.activeEffectValue() > 0.0F ? state.activeEffectValue() : fallbackMultiplier(Math.max(0, state.startedTier() - 1));
            return previous + ((state.startedEffectValue() - previous) * state.progressFraction());
        }
        if (state.activeEffectValue() > 0.0F) {
            return state.activeEffectValue();
        }
        if (state.startedTier() <= 0) {
            return fallbackMultiplier(state.activeLevel());
        }
        float previous = fallbackMultiplier(Math.max(0, state.startedTier() - 1));
        float target = fallbackMultiplier(state.startedTier());
        return previous + ((target - previous) * state.progressFraction());
    }

    public static float fallbackMultiplier(int level) {
        return switch (Math.max(0, level)) {
            case 0 -> 1.0F;
            case 1 -> 1.10F;
            case 2 -> 1.20F;
            case 3 -> 1.35F;
            default -> 1.35F + ((level - 3) * 0.10F);
        };
    }
}
