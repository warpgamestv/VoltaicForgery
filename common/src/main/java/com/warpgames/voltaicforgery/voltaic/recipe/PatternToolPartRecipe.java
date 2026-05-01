package com.warpgames.voltaicforgery.voltaic.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import com.warpgames.voltaicforgery.voltaic.item.ToolPartItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public record PatternToolPartRecipe(
        String group,
        String part,
        Ingredient pattern,
        Ingredient material,
        Result result,
        String resultMaterial,
        Optional<Identifier> icon
) implements Recipe<PatternToolPartRecipeInput> {

    public record Result(Identifier id, int count) {
        public static final com.mojang.serialization.Codec<Result> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("id").forGetter(Result::id),
                com.mojang.serialization.Codec.INT.optionalFieldOf("count", 1).forGetter(Result::count)
        ).apply(instance, Result::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Result> STREAM_CODEC = StreamCodec.composite(
                Identifier.STREAM_CODEC, Result::id,
                ByteBufCodecs.VAR_INT, Result::count,
                Result::new
        );
    }

    public static final MapCodec<PatternToolPartRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            com.mojang.serialization.Codec.STRING.optionalFieldOf("group", "").forGetter(PatternToolPartRecipe::group),
            com.mojang.serialization.Codec.STRING.fieldOf("part").forGetter(PatternToolPartRecipe::part),
            Ingredient.CODEC.fieldOf("pattern").forGetter(PatternToolPartRecipe::pattern),
            Ingredient.CODEC.fieldOf("material").forGetter(PatternToolPartRecipe::material),
            Result.CODEC.fieldOf("result").forGetter(PatternToolPartRecipe::result),
            com.mojang.serialization.Codec.STRING.fieldOf("result_material").forGetter(PatternToolPartRecipe::resultMaterial),
            Identifier.CODEC.optionalFieldOf("icon").forGetter(PatternToolPartRecipe::icon)
    ).apply(instance, PatternToolPartRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PatternToolPartRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, PatternToolPartRecipe::group,
            ByteBufCodecs.STRING_UTF8, PatternToolPartRecipe::part,
            Ingredient.CONTENTS_STREAM_CODEC, PatternToolPartRecipe::pattern,
            Ingredient.CONTENTS_STREAM_CODEC, PatternToolPartRecipe::material,
            Result.STREAM_CODEC, PatternToolPartRecipe::result,
            ByteBufCodecs.STRING_UTF8, PatternToolPartRecipe::resultMaterial,
            Identifier.STREAM_CODEC.apply(ByteBufCodecs::optional), PatternToolPartRecipe::icon,
            PatternToolPartRecipe::new
    );

    @Override
    public boolean matches(PatternToolPartRecipeInput input, Level level) {
        return part.equals(input.partType()) && pattern.test(input.pattern()) && material.test(input.material());
    }

    @Override
    public ItemStack assemble(PatternToolPartRecipeInput input) {
        ItemStack out = new ItemStack(BuiltInRegistries.ITEM.getValue(result.id()), Math.max(1, result.count()));
        if (out.getItem() instanceof ToolPartItem) {
            out = ToolPartItem.createStack(out.getItem(), resultMaterial);
            out.setCount(Math.max(1, result.count()));
        }
        return out;
    }

    public ItemStack resultStack() {
        return assemble(new PatternToolPartRecipeInput(ItemStack.EMPTY, ItemStack.EMPTY, part));
    }

    @Override
    public RecipeSerializer<? extends Recipe<PatternToolPartRecipeInput>> getSerializer() {
        return VoltaicContent.PATTERN_TOOL_PART_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<PatternToolPartRecipeInput>> getType() {
        return VoltaicContent.PATTERN_TOOL_PART_RECIPE_TYPE.get();
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(List.of(pattern, material));
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return VoltaicContent.PATTERN_TOOL_PART_RECIPE_BOOK_CATEGORY.get();
    }
}
