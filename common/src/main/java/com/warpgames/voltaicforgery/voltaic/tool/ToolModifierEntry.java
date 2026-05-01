package com.warpgames.voltaicforgery.voltaic.tool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

/**
 * Data-driven progressive modifier definition.
 */
public record ToolModifierEntry(
        String trait,
        List<String> allowedTools,
        List<IngredientValue> ingredients,
        List<Tier> tiers
) {

    public record IngredientValue(Ingredient ingredient, int value) {
        public static final Codec<IngredientValue> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(IngredientValue::ingredient),
                Codec.INT.optionalFieldOf("value", 1).forGetter(IngredientValue::value)
        ).apply(instance, IngredientValue::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, IngredientValue> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, IngredientValue::ingredient,
                ByteBufCodecs.VAR_INT, IngredientValue::value,
                IngredientValue::new
        );

        public IngredientValue {
            value = Math.max(1, value);
        }
    }

    public record Tier(int level, int requiredValue, int slotCost, float effectValue) {
        public static final Codec<Tier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("level").forGetter(Tier::level),
                Codec.INT.fieldOf("required_value").forGetter(Tier::requiredValue),
                Codec.INT.optionalFieldOf("slot_cost", 1).forGetter(Tier::slotCost),
                Codec.FLOAT.optionalFieldOf("effect_value", 0.0F).forGetter(Tier::effectValue)
        ).apply(instance, Tier::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Tier> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, Tier::level,
                ByteBufCodecs.VAR_INT, Tier::requiredValue,
                ByteBufCodecs.VAR_INT, Tier::slotCost,
                ByteBufCodecs.FLOAT, Tier::effectValue,
                Tier::new
        );

        public Tier {
            level = Math.max(1, level);
            requiredValue = Math.max(1, requiredValue);
            slotCost = Math.max(1, slotCost);
        }

        public Tier(int level, int requiredValue, int slotCost) {
            this(level, requiredValue, slotCost, 0.0F);
        }
    }

    public static final Codec<ToolModifierEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("trait").forGetter(ToolModifierEntry::trait),
            Codec.STRING.listOf().optionalFieldOf("allowed_tools", List.of()).forGetter(ToolModifierEntry::allowedTools),
            IngredientValue.CODEC.listOf().fieldOf("ingredients").forGetter(ToolModifierEntry::ingredients),
            Tier.CODEC.listOf().fieldOf("tiers").forGetter(ToolModifierEntry::tiers)
    ).apply(instance, ToolModifierEntry::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ToolModifierEntry> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ToolModifierEntry::trait,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), ToolModifierEntry::allowedTools,
            IngredientValue.STREAM_CODEC.apply(ByteBufCodecs.list()), ToolModifierEntry::ingredients,
            Tier.STREAM_CODEC.apply(ByteBufCodecs.list()), ToolModifierEntry::tiers,
            ToolModifierEntry::new
    );

    public ToolModifierEntry {
        trait = trait == null ? "" : trait.trim().toLowerCase(java.util.Locale.ROOT);
        allowedTools = allowedTools.stream()
                .map(tool -> tool == null ? "" : tool.trim().toLowerCase(java.util.Locale.ROOT))
                .filter(tool -> !tool.isBlank())
                .distinct()
                .toList();
        ingredients = List.copyOf(ingredients);
        tiers = tiers.stream()
                .sorted(java.util.Comparator.comparingInt(Tier::level))
                .toList();
    }

    public java.util.Optional<Tier> tier(int level) {
        return tiers.stream().filter(tier -> tier.level() == level).findFirst();
    }

    public boolean allowsTool(String toolType) {
        if (allowedTools.isEmpty()) return true;
        String normalized = toolType == null ? "" : toolType.trim().toLowerCase(java.util.Locale.ROOT);
        return allowedTools.contains(normalized);
    }
}
