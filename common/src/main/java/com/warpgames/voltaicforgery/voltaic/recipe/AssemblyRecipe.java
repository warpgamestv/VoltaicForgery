package com.warpgames.voltaicforgery.voltaic.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import com.warpgames.voltaicforgery.voltaic.blockentity.ModificationStationBlockEntity;
import com.warpgames.voltaicforgery.voltaic.item.ModularAxeItem;
import com.warpgames.voltaicforgery.voltaic.item.ModularPickaxeItem;
import com.warpgames.voltaicforgery.voltaic.item.ModularShovelItem;
import com.warpgames.voltaicforgery.voltaic.item.ModularSwordItem;
import com.warpgames.voltaicforgery.voltaic.item.ToolPartItem;
import com.warpgames.voltaicforgery.voltaic.tool.ToolAssembly;
import com.warpgames.voltaicforgery.voltaic.tool.ToolMaterialResolver;
import com.warpgames.voltaicforgery.voltaic.tool.ToolMaterialStat;
import com.warpgames.voltaicforgery.voltaic.trait.ToolTraits;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public record AssemblyRecipe(
        String group,
        Ingredient headPart,
        Ingredient bindingPart,
        Ingredient handlePart,
        Identifier resultId,
        int modifierSlots
) implements Recipe<AssemblyRecipeInput> {

    public static final MapCodec<AssemblyRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            com.mojang.serialization.Codec.STRING.optionalFieldOf("group", "").forGetter(AssemblyRecipe::group),
            Ingredient.CODEC.fieldOf("head").forGetter(AssemblyRecipe::headPart),
            Ingredient.CODEC.fieldOf("binding").forGetter(AssemblyRecipe::bindingPart),
            Ingredient.CODEC.fieldOf("handle").forGetter(AssemblyRecipe::handlePart),
            Identifier.CODEC.fieldOf("result").forGetter(AssemblyRecipe::resultId),
            com.mojang.serialization.Codec.INT.optionalFieldOf("modifier_slots", ModificationStationBlockEntity.DEFAULT_MODIFIER_SLOTS).forGetter(AssemblyRecipe::modifierSlots)
    ).apply(instance, AssemblyRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AssemblyRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, AssemblyRecipe::group,
            Ingredient.CONTENTS_STREAM_CODEC, AssemblyRecipe::headPart,
            Ingredient.CONTENTS_STREAM_CODEC, AssemblyRecipe::bindingPart,
            Ingredient.CONTENTS_STREAM_CODEC, AssemblyRecipe::handlePart,
            Identifier.STREAM_CODEC, AssemblyRecipe::resultId,
            ByteBufCodecs.VAR_INT, AssemblyRecipe::modifierSlots,
            AssemblyRecipe::new
    );

    @Override
    public boolean matches(AssemblyRecipeInput input, Level level) {
        return headPart.test(input.head()) && bindingPart.test(input.binding()) && handlePart.test(input.handle());
    }

    public ItemStack assemble(AssemblyRecipeInput input) {
        return assemble(input, null);
    }

    public ItemStack assemble(AssemblyRecipeInput input, HolderLookup.Provider registries) {
        ItemStack head = input.head();
        ItemStack binding = input.binding();
        ItemStack handle = input.handle();

        if (head.isEmpty() || binding.isEmpty() || handle.isEmpty()) {
            return ItemStack.EMPTY;
        }

        Item resultItem = BuiltInRegistries.ITEM.getValue(resultId);
        if (resultItem == net.minecraft.world.item.Items.AIR) {
            return ItemStack.EMPTY;
        }

        ToolAssembly assembly = new ToolAssembly(
                ToolPartItem.materialId(head),
                ToolPartItem.materialId(binding),
                ToolPartItem.materialId(handle)
        );

        ItemStack result = new ItemStack(resultItem);
        result.set(VoltaicContent.ASSEMBLED_TOOL.get(), assembly);
        result.set(VoltaicContent.TOOL_TRAITS.get(), collectTraits(assembly, registries));
        result.set(VoltaicContent.TOOL_MODIFIER_SLOTS.get(), Math.max(0, modifierSlots));
        result.set(VoltaicContent.TOOL_MODIFIERS.get(), List.of());
        applyDerivedComponentsForTool(result, registries);
        return result;
    }

    private List<String> collectTraits(ToolAssembly assembly, HolderLookup.Provider registries) {
        Set<String> traits = new LinkedHashSet<>();
        addMaterialTraits(traits, ToolMaterialResolver.resolve(registries, assembly.headMaterial()));
        addMaterialTraits(traits, ToolMaterialResolver.resolve(registries, assembly.bindingMaterial()));
        addMaterialTraits(traits, ToolMaterialResolver.resolve(registries, assembly.handleMaterial()));
        return List.copyOf(ToolTraits.normalizeIds(List.copyOf(traits)));
    }

    private static void addMaterialTraits(Set<String> traits, ToolMaterialStat material) {
        traits.addAll(material.traits());
    }

    private void applyDerivedComponentsForTool(ItemStack result, HolderLookup.Provider registries) {
        if (result.getItem() instanceof ModularPickaxeItem) {
            ModularPickaxeItem.applyDerivedComponents(result, registries);
        } else if (result.getItem() instanceof ModularAxeItem) {
            ModularAxeItem.applyDerivedComponents(result, registries);
        } else if (result.getItem() instanceof ModularShovelItem) {
            ModularShovelItem.applyDerivedComponents(result, registries);
        } else if (result.getItem() instanceof ModularSwordItem) {
            ModularSwordItem.applyDerivedComponents(result, registries);
        }
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public RecipeSerializer<? extends Recipe<AssemblyRecipeInput>> getSerializer() {
        return VoltaicContent.ASSEMBLY_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<AssemblyRecipeInput>> getType() {
        return VoltaicContent.ASSEMBLY_RECIPE_TYPE.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(List.of(headPart, bindingPart, handlePart));
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return VoltaicContent.CASTING_RECIPE_BOOK_CATEGORY.get();
    }
}
