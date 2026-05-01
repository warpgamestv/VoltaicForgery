package com.warpgames.voltaicforgery.voltaic.registry;

import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import com.warpgames.voltaicforgery.voltaic.item.component.ToolEnergyStorageComponent;
import com.warpgames.voltaicforgery.voltaic.trait.ToolTraits;
import com.warpgames.voltaicforgery.voltaic.tool.ToolMaterialStat;
import com.warpgames.voltaicforgery.voltaic.tool.ToolModifierEntry;
import com.warpgames.voltaicforgery.voltaic.tool.VoltaicToolMaterials;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Optional;

public final class VoltaicRegistries {

    public static final ResourceKey<Registry<ToolMaterialStat>> TOOL_MATERIALS =
            ResourceKey.createRegistryKey(VoltaicContent.id("tool_materials"));

    public static final ResourceKey<Registry<ToolModifierEntry>> TOOL_MODIFIERS =
            ResourceKey.createRegistryKey(VoltaicContent.id("tool_modifiers"));

    public static final ResourceKey<ToolMaterialStat> COPPER =
            materialKey("copper");
    public static final ResourceKey<ToolMaterialStat> WOOD =
            materialKey("wood");
    public static final ResourceKey<ToolMaterialStat> STONE =
            materialKey("stone");
    public static final ResourceKey<ToolMaterialStat> IRON =
            materialKey("iron");
    public static final ResourceKey<ToolMaterialStat> GOLD =
            materialKey("gold");
    public static final ResourceKey<ToolMaterialStat> DIAMOND =
            materialKey("diamond");
    public static final ResourceKey<ToolMaterialStat> NETHERITE =
            materialKey("netherite");

    public static final ResourceKey<ToolModifierEntry> REDSTONE_HASTE =
            ResourceKey.create(TOOL_MODIFIERS, VoltaicContent.id("redstone_haste"));
    public static final ResourceKey<ToolModifierEntry> INDUCTION_COIL =
            ResourceKey.create(TOOL_MODIFIERS, VoltaicContent.id("induction_coil"));
    public static final ResourceKey<Item> INDUCTION_COIL_ITEM =
            ResourceKey.create(Registries.ITEM, VoltaicContent.id("induction_coil"));

    private VoltaicRegistries() {}

    public static ResourceKey<ToolMaterialStat> materialKey(String materialId) {
        return ResourceKey.create(TOOL_MATERIALS, VoltaicContent.id(VoltaicToolMaterials.normalize(materialId)));
    }

    public static ResourceKey<ToolMaterialStat> materialKey(Identifier id) {
        return ResourceKey.create(TOOL_MATERIALS, id);
    }

    public static void bootstrapToolMaterials(BootstrapContext<ToolMaterialStat> context) {
        context.register(WOOD, new ToolMaterialStat(
                "wood", 59, 2.0F, 0.0F, 0.0F,
                1.0F, 0.25F, 0.5F, 0xA77E4B,
                "wood",
                List.of(),
                "part_table",
                "",
                Optional.of(Ingredient.of(Items.OAK_PLANKS))
        ));
        context.register(STONE, new ToolMaterialStat(
                "stone", 131, 4.0F, 1.0F, 1.0F,
                1.0F, 0.25F, 0.5F, 0x808080,
                "stone",
                List.of(),
                "part_table",
                "",
                Optional.of(Ingredient.of(Items.COBBLESTONE))
        ));
        context.register(COPPER, new ToolMaterialStat(
                "copper", 200, 5.0F, 2.0F, 2.0F,
                1.0F, 0.25F, 0.5F, 0xE77C56,
                "iron",
                List.of("magnetic"),
                "molten",
                "voltaicforgery:molten_copper",
                Optional.of(Ingredient.of(Items.COPPER_INGOT))
        ));
        context.register(IRON, new ToolMaterialStat(
                "iron", 250, 6.0F, 2.0F, 2.0F,
                1.0F, 0.25F, 0.5F, 0xD8D8D8,
                "iron",
                List.of(),
                "molten",
                "voltaicforgery:molten_iron",
                Optional.of(Ingredient.of(Items.IRON_INGOT))
        ));
        context.register(GOLD, new ToolMaterialStat(
                "gold", 32, 12.0F, 0.0F, 0.0F,
                1.0F, 0.25F, 0.5F, 0xFCE97D,
                "gold",
                List.of(),
                "molten",
                "voltaicforgery:molten_gold",
                Optional.of(Ingredient.of(Items.GOLD_INGOT))
        ));
        context.register(DIAMOND, new ToolMaterialStat(
                "diamond", 1561, 8.0F, 3.0F, 3.0F,
                1.0F, 0.25F, 0.5F, 0x33E7D6,
                "diamond",
                List.of(),
                "solid",
                "",
                Optional.of(Ingredient.of(Items.DIAMOND))
        ));
        context.register(NETHERITE, new ToolMaterialStat(
                "netherite", 2031, 9.0F, 4.0F, 4.0F,
                1.0F, 0.25F, 0.5F, 0x4A3B3B,
                "netherite",
                List.of(),
                "solid",
                "",
                Optional.of(Ingredient.of(Items.NETHERITE_INGOT))
        ));
    }

    public static void bootstrapToolModifiers(BootstrapContext<ToolModifierEntry> context) {
        HolderGetter<Item> items = context.lookup(Registries.ITEM);
        context.register(REDSTONE_HASTE, new ToolModifierEntry(
                ToolTraits.HASTE,
                List.of("pickaxe", "axe", "shovel"),
                List.of(
                        new ToolModifierEntry.IngredientValue(Ingredient.of(Items.REDSTONE), 1),
                        new ToolModifierEntry.IngredientValue(Ingredient.of(Items.REDSTONE_BLOCK), 8)
                ),
                List.of(
                        new ToolModifierEntry.Tier(1, 64, 1, 1.10F),
                        new ToolModifierEntry.Tier(2, 128, 1, 1.20F),
                        new ToolModifierEntry.Tier(3, 192, 1, 1.35F)
                )
        ));
        context.register(INDUCTION_COIL, new ToolModifierEntry(
                ToolTraits.POWERED,
                List.of("pickaxe", "axe", "shovel", "sword"),
                List.of(new ToolModifierEntry.IngredientValue(Ingredient.of(items.getOrThrow(INDUCTION_COIL_ITEM).value()), 1)),
                List.of(new ToolModifierEntry.Tier(1, 1, 1, ToolEnergyStorageComponent.DEFAULT_MAX_ENERGY))
        ));
    }
}
