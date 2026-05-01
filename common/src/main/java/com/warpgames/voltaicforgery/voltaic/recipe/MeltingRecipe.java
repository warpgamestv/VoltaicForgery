package com.warpgames.voltaicforgery.voltaic.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
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
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public record MeltingRecipe(
        String group,
        Ingredient input,
        Identifier resultFluidId,
        int resultAmountMb,
        int requiredHeat,
        int time
) implements Recipe<MeltingRecipeInput> {

    public static final MapCodec<MeltingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            com.mojang.serialization.Codec.STRING.optionalFieldOf("group", "").forGetter(MeltingRecipe::group),
            Ingredient.CODEC.fieldOf("input").forGetter(MeltingRecipe::input),
            Identifier.CODEC.fieldOf("result_fluid").forGetter(MeltingRecipe::resultFluidId),
            com.mojang.serialization.Codec.INT.fieldOf("result_amount_mb").forGetter(MeltingRecipe::resultAmountMb),
            com.mojang.serialization.Codec.INT.optionalFieldOf("required_heat", 0).forGetter(MeltingRecipe::requiredHeat),
            com.mojang.serialization.Codec.INT.optionalFieldOf("time", 100).forGetter(MeltingRecipe::time)
    ).apply(instance, MeltingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, MeltingRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, MeltingRecipe::group,
            Ingredient.CONTENTS_STREAM_CODEC, MeltingRecipe::input,
            Identifier.STREAM_CODEC, MeltingRecipe::resultFluidId,
            ByteBufCodecs.VAR_INT, MeltingRecipe::resultAmountMb,
            ByteBufCodecs.VAR_INT, MeltingRecipe::requiredHeat,
            ByteBufCodecs.VAR_INT, MeltingRecipe::time,
            MeltingRecipe::new
    );

    @Override
    public boolean matches(MeltingRecipeInput input, Level level) {
        return this.input.test(input.input());
    }

    public ItemStack assemble(MeltingRecipeInput input) {
        return ItemStack.EMPTY;
    }

    public ItemStack assemble(MeltingRecipeInput input, net.minecraft.core.HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    public Fluid resultFluid() {
        Fluid f = BuiltInRegistries.FLUID.getOptional(resultFluidId).orElse(Fluids.EMPTY);
        return f == null ? Fluids.EMPTY : f;
    }

    @Override
    public RecipeSerializer<? extends Recipe<MeltingRecipeInput>> getSerializer() {
        return VoltaicContent.MELTING_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<MeltingRecipeInput>> getType() {
        return VoltaicContent.MELTING_RECIPE_TYPE.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(input);
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return VoltaicContent.MELTING_RECIPE_BOOK_CATEGORY.get();
    }
}
