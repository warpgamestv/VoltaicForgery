package com.warpgames.voltaicforgery.voltaic.tool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public record ToolModifierState(
        String trait,
        int activeLevel,
        int startedTier,
        int progress,
        int requiredValue,
        float activeEffectValue,
        float startedEffectValue
) {

    public static final Codec<ToolModifierState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("trait").forGetter(ToolModifierState::trait),
            Codec.INT.optionalFieldOf("active_level", 0).forGetter(ToolModifierState::activeLevel),
            Codec.INT.optionalFieldOf("started_tier", 0).forGetter(ToolModifierState::startedTier),
            Codec.INT.optionalFieldOf("progress", 0).forGetter(ToolModifierState::progress),
            Codec.INT.optionalFieldOf("required_value", 0).forGetter(ToolModifierState::requiredValue),
            Codec.FLOAT.optionalFieldOf("active_effect_value", 0.0F).forGetter(ToolModifierState::activeEffectValue),
            Codec.FLOAT.optionalFieldOf("started_effect_value", 0.0F).forGetter(ToolModifierState::startedEffectValue)
    ).apply(instance, ToolModifierState::new));

    public static final Codec<List<ToolModifierState>> LIST_CODEC = CODEC.listOf()
            .xmap(List::copyOf, List::copyOf);

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static final StreamCodec<RegistryFriendlyByteBuf, List<ToolModifierState>> LIST_STREAM_CODEC =
            (StreamCodec) ByteBufCodecs.fromCodec(LIST_CODEC);

    public ToolModifierState {
        trait = normalize(trait);
        activeLevel = Math.max(0, activeLevel);
        startedTier = Math.max(0, startedTier);
        progress = Math.max(0, progress);
        requiredValue = Math.max(0, requiredValue);
    }

    public ToolModifierState(String trait, int activeLevel, int startedTier, int progress) {
        this(trait, activeLevel, startedTier, progress, 0);
    }

    public ToolModifierState(String trait, int activeLevel, int startedTier, int progress, int requiredValue) {
        this(trait, activeLevel, startedTier, progress, requiredValue, 0.0F, 0.0F);
    }

    public boolean hasStartedTier() {
        return startedTier > activeLevel;
    }

    public ToolModifierState withStartedTier(int tier) {
        return withStartedTier(tier, 0, startedEffectValue);
    }

    public ToolModifierState withStartedTier(int tier, int requiredValue) {
        return withStartedTier(tier, requiredValue, startedEffectValue);
    }

    public ToolModifierState withStartedTier(int tier, int requiredValue, float effectValue) {
        return new ToolModifierState(trait, activeLevel, tier, 0, requiredValue, activeEffectValue, effectValue);
    }

    public ToolModifierState withProgress(int nextProgress, int requiredValue) {
        int normalizedRequired = Math.max(1, requiredValue);
        int capped = Math.min(Math.max(0, nextProgress), normalizedRequired);
        int nextActiveLevel = capped >= normalizedRequired ? Math.max(activeLevel, startedTier) : activeLevel;
        float nextActiveEffectValue = capped >= normalizedRequired ? startedEffectValue : activeEffectValue;
        return new ToolModifierState(trait, nextActiveLevel, startedTier, capped, normalizedRequired, nextActiveEffectValue, startedEffectValue);
    }

    public float progressFraction() {
        if (requiredValue <= 0) {
            return activeLevel >= startedTier && startedTier > 0 ? 1.0F : 0.0F;
        }
        return Math.min(1.0F, (float) progress / (float) requiredValue);
    }

    public static Optional<ToolModifierState> find(List<ToolModifierState> states, String trait) {
        String normalized = normalize(trait);
        return states.stream()
                .filter(state -> state.trait().equals(normalized))
                .findFirst();
    }

    public static List<ToolModifierState> replace(List<ToolModifierState> states, ToolModifierState next) {
        boolean replaced = false;
        java.util.ArrayList<ToolModifierState> out = new java.util.ArrayList<>();
        for (ToolModifierState state : states) {
            if (state.trait().equals(next.trait())) {
                if (!replaced) {
                    out.add(next);
                    replaced = true;
                }
            } else {
                out.add(state);
            }
        }
        if (!replaced) {
            out.add(next);
        }
        return List.copyOf(out);
    }

    private static String normalize(String id) {
        return id == null ? "" : id.trim().toLowerCase(Locale.ROOT);
    }
}
