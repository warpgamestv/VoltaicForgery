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

public record CastingRecipe(
        String group,
        Ingredient cast,
        CastingFluidIngredient fluid,
        CastingResult result,
        String resultMaterialId,
        boolean consumeCast
) implements Recipe<CastingRecipeInput> {

    public record CastingResult(Identifier id, int count) {
        public static final com.mojang.serialization.Codec<CastingResult> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("id").forGetter(CastingResult::id),
                com.mojang.serialization.Codec.INT.optionalFieldOf("count", 1).forGetter(CastingResult::count)
        ).apply(instance, CastingResult::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CastingResult> STREAM_CODEC = StreamCodec.composite(
                Identifier.STREAM_CODEC, CastingResult::id,
                ByteBufCodecs.VAR_INT, CastingResult::count,
                CastingResult::new
        );
    }

    public static final MapCodec<CastingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            com.mojang.serialization.Codec.STRING.optionalFieldOf("group", "").forGetter(CastingRecipe::group),
            Ingredient.CODEC.fieldOf("cast").forGetter(CastingRecipe::cast),
            CastingFluidIngredient.CODEC.fieldOf("fluid").forGetter(CastingRecipe::fluid),
            CastingResult.CODEC.fieldOf("result").forGetter(CastingRecipe::result),
            com.mojang.serialization.Codec.STRING.optionalFieldOf("result_material", "").forGetter(CastingRecipe::resultMaterialId),
            com.mojang.serialization.Codec.BOOL.optionalFieldOf("consume_cast", false).forGetter(CastingRecipe::consumeCast)
    ).apply(instance, CastingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CastingRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, CastingRecipe::group,
            Ingredient.CONTENTS_STREAM_CODEC, CastingRecipe::cast,
            CastingFluidIngredient.STREAM_CODEC, CastingRecipe::fluid,
            CastingResult.STREAM_CODEC, CastingRecipe::result,
            ByteBufCodecs.STRING_UTF8, CastingRecipe::resultMaterialId,
            ByteBufCodecs.BOOL, CastingRecipe::consumeCast,
            CastingRecipe::new
    );

    @Override
    public boolean matches(CastingRecipeInput input, Level level) {
        return cast.test(input.cast()) && fluid.matches(input.fluid(), input.amountMb());
    }

    public ItemStack assemble(CastingRecipeInput input) {
        return assemble(input, null);
    }

    public ItemStack assemble(CastingRecipeInput input, net.minecraft.core.HolderLookup.Provider registries) {
        ItemStack out = new ItemStack(BuiltInRegistries.ITEM.getValue(result.id()), Math.max(1, result.count()));
        if (!resultMaterialId.isBlank() && out.getItem() instanceof ToolPartItem) {
            out = ToolPartItem.createStack(out.getItem(), resultMaterialId);
            out.setCount(Math.max(1, result.count()));
        }
        return out;
    }

    /**
     * Convenience for GUI/JEI to display the output stack.
     */
    public ItemStack resultStack() {
        return assemble(new CastingRecipeInput(ItemStack.EMPTY, net.minecraft.world.level.material.Fluids.EMPTY, 0), null);
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public RecipeSerializer<? extends Recipe<CastingRecipeInput>> getSerializer() {
        return VoltaicContent.CASTING_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<CastingRecipeInput>> getType() {
        return VoltaicContent.CASTING_RECIPE_TYPE.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(cast);
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return VoltaicContent.CASTING_RECIPE_BOOK_CATEGORY.get();
    }
}
