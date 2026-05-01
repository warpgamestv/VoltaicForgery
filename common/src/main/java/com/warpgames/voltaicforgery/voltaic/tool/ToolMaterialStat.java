package com.warpgames.voltaicforgery.voltaic.tool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Optional;

public record ToolMaterialStat(
        String materialId,
        int durability,
        float miningSpeed,
        float toolDamage,
        float weaponDamage,
        float headDurabilityMultiplier,
        float bindingDurabilityMultiplier,
        float handleDurabilityMultiplier,
        int tintColor,
        String toolType,
        List<String> hardcodedModifiers,
        String materialType,
        String moltenFluid,
        Optional<Ingredient> repairIngredient
) {

    private static final Codec<List<String>> MODIFIERS_CODEC = Codec.STRING.listOf()
            .xmap(List::copyOf, List::copyOf);

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static final StreamCodec<RegistryFriendlyByteBuf, List<String>> MODIFIERS_STREAM_CODEC =
            (StreamCodec) ByteBufCodecs.fromCodec(MODIFIERS_CODEC);

    public ToolMaterialStat {
        hardcodedModifiers = List.copyOf(hardcodedModifiers);
        toolType = normalizeOrFallback(toolType, "generic");
        materialType = normalizeOrFallback(materialType, "solid");
        moltenFluid = moltenFluid == null ? "" : moltenFluid;
        headDurabilityMultiplier = Math.max(0.0F, headDurabilityMultiplier);
        bindingDurabilityMultiplier = Math.max(0.0F, bindingDurabilityMultiplier);
        handleDurabilityMultiplier = Math.max(0.0F, handleDurabilityMultiplier);
    }

    public static final ToolMaterialStat FALLBACK = new ToolMaterialStat("copper", 200, 5.0F, 2.0F, 2.0F, 1.0F, 0.25F, 0.5F, 0xE77C56, "iron", List.of("magnetic"), "molten", "voltaicforgery:molten_copper", Optional.empty());

    public static final Codec<ToolMaterialStat> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("material_id").forGetter(ToolMaterialStat::materialId),
            Codec.INT.fieldOf("durability").forGetter(ToolMaterialStat::durability),
            Codec.FLOAT.fieldOf("mining_speed").forGetter(ToolMaterialStat::miningSpeed),
            Codec.FLOAT.optionalFieldOf("tool_damage", 0.0F).forGetter(ToolMaterialStat::toolDamage),
            Codec.FLOAT.optionalFieldOf("weapon_damage", 0.0F).forGetter(ToolMaterialStat::weaponDamage),
            Codec.FLOAT.optionalFieldOf("head_durability_multiplier", 1.0F).forGetter(ToolMaterialStat::headDurabilityMultiplier),
            Codec.FLOAT.optionalFieldOf("binding_durability_multiplier", 0.25F).forGetter(ToolMaterialStat::bindingDurabilityMultiplier),
            Codec.FLOAT.optionalFieldOf("handle_durability_multiplier", 0.5F).forGetter(ToolMaterialStat::handleDurabilityMultiplier),
            Codec.INT.fieldOf("color").forGetter(ToolMaterialStat::tintColor),
            Codec.STRING.optionalFieldOf("tool_type", "generic").forGetter(ToolMaterialStat::toolType),
            MODIFIERS_CODEC.optionalFieldOf("hardcoded_modifiers", List.of()).forGetter(ToolMaterialStat::hardcodedModifiers),
            Codec.STRING.optionalFieldOf("material_type", "solid").forGetter(ToolMaterialStat::materialType),
            Identifier.CODEC.xmap(Identifier::toString, Identifier::parse).optionalFieldOf("molten_fluid", "").forGetter(ToolMaterialStat::moltenFluid),
            Ingredient.CODEC.optionalFieldOf("repair_ingredient").forGetter(ToolMaterialStat::repairIngredient)
    ).apply(instance, ToolMaterialStat::new));

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static final StreamCodec<RegistryFriendlyByteBuf, ToolMaterialStat> STREAM_CODEC =
            (StreamCodec) ByteBufCodecs.fromCodec(CODEC);

    public float attackDamage() {
        return toolDamage;
    }

    public List<String> traits() {
        return hardcodedModifiers;
    }

    public boolean hasMoltenFluid() {
        return !moltenFluid.isBlank();
    }

    private static String normalizeOrFallback(String value, String fallback) {
        if (value == null || value.isBlank()) return fallback;
        return value.toLowerCase(java.util.Locale.ROOT);
    }
}
