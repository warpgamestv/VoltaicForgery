package com.warpgames.voltaicforgery.voltaic;

import com.warpgames.voltaicforgery.Constants;
import com.warpgames.voltaicforgery.platform.Services;
import com.warpgames.voltaicforgery.platform.services.RegistryEntry;
import com.warpgames.voltaicforgery.voltaic.tool.ToolAssembly;
import com.warpgames.voltaicforgery.voltaic.tool.ToolModifierState;
import com.warpgames.voltaicforgery.voltaic.block.AssemblyStationBlock;
import com.warpgames.voltaicforgery.voltaic.block.CastingFaucetBlock;
import com.warpgames.voltaicforgery.voltaic.block.CastingTableBlock;
import com.warpgames.voltaicforgery.voltaic.block.CrucibleBricksBlock;
import com.warpgames.voltaicforgery.voltaic.block.InductionCrucibleBlock;
import com.warpgames.voltaicforgery.voltaic.block.ModificationStationBlock;
import com.warpgames.voltaicforgery.voltaic.block.PatternTableBlock;
import com.warpgames.voltaicforgery.voltaic.block.SolidFuelDynamoBlock;
import com.warpgames.voltaicforgery.voltaic.blockentity.AssemblyStationBlockEntity;
import com.warpgames.voltaicforgery.voltaic.blockentity.CastingTableBlockEntity;
import com.warpgames.voltaicforgery.voltaic.blockentity.CastingFaucetBlockEntity;
import com.warpgames.voltaicforgery.voltaic.blockentity.InductionCrucibleBlockEntity;
import com.warpgames.voltaicforgery.voltaic.blockentity.ModificationStationBlockEntity;
import com.warpgames.voltaicforgery.voltaic.blockentity.PatternTableBlockEntity;
import com.warpgames.voltaicforgery.voltaic.blockentity.SolidFuelDynamoBlockEntity;
import com.warpgames.voltaicforgery.voltaic.fluid.MoltenMetalFluid;
import com.warpgames.voltaicforgery.voltaic.item.CoilTier;
import com.warpgames.voltaicforgery.voltaic.item.CoilUpgradeItem;
import com.warpgames.voltaicforgery.voltaic.item.component.ToolEnergyStorageComponent;
import com.warpgames.voltaicforgery.voltaic.item.ModularAxeItem;
import com.warpgames.voltaicforgery.voltaic.item.ModularPickaxeItem;
import com.warpgames.voltaicforgery.voltaic.item.ModularShovelItem;
import com.warpgames.voltaicforgery.voltaic.item.ModularSwordItem;
import com.warpgames.voltaicforgery.voltaic.menu.AssemblyStationMenu;
import com.warpgames.voltaicforgery.voltaic.menu.InductionCrucibleMenu;
import com.warpgames.voltaicforgery.voltaic.menu.ModificationStationMenu;
import com.warpgames.voltaicforgery.voltaic.menu.PatternTableMenu;
import com.warpgames.voltaicforgery.voltaic.menu.SolidFuelDynamoMenu;
import com.warpgames.voltaicforgery.voltaic.recipe.AssemblyRecipe;
import com.warpgames.voltaicforgery.voltaic.recipe.CastingRecipe;
import com.warpgames.voltaicforgery.voltaic.recipe.MeltingRecipe;
import com.warpgames.voltaicforgery.voltaic.recipe.PatternToolPartRecipe;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;

import java.util.List;
import java.util.function.Supplier;

public class VoltaicContent {

    public static final String MOD_ID = Constants.MOD_ID;

    // -- Registry Entries --
    public static RegistryEntry<Block> SOLID_FUEL_DYNAMO;
    public static RegistryEntry<Block> INDUCTION_CRUCIBLE;
    public static RegistryEntry<Block> CASTING_FAUCET;
    public static RegistryEntry<Block> CASTING_TABLE;
    public static RegistryEntry<Block> ASSEMBLY_STATION;
    public static RegistryEntry<Block> MODIFICATION_STATION;
    public static RegistryEntry<Block> PATTERN_TABLE;
    public static RegistryEntry<Block> CRUCIBLE_BRICKS;

    public static RegistryEntry<Item> UNFIRED_CRUCIBLE_BRICK;
    public static RegistryEntry<Item> CRUCIBLE_BRICK;
    public static RegistryEntry<Item> BLANK_PATTERN;
    public static RegistryEntry<Item> PICKAXE_HEAD_CAST;
    public static RegistryEntry<Item> AXE_HEAD_CAST;
    public static RegistryEntry<Item> SHOVEL_HEAD_CAST;
    public static RegistryEntry<Item> SWORD_HEAD_CAST;
    public static RegistryEntry<Item> TOOL_BINDING_CAST;
    public static RegistryEntry<Item> TOOL_HANDLE_CAST;

    public static RegistryEntry<Item> PICKAXE_HEAD_PATTERN;
    public static RegistryEntry<Item> AXE_HEAD_PATTERN;
    public static RegistryEntry<Item> SHOVEL_HEAD_PATTERN;
    public static RegistryEntry<Item> SWORD_HEAD_PATTERN;
    public static RegistryEntry<Item> TOOL_BINDING_PATTERN;
    public static RegistryEntry<Item> TOOL_HANDLE_PATTERN;

    public static RegistryEntry<Item> PICKAXE_HEAD_PART;
    public static RegistryEntry<Item> AXE_HEAD_PART;
    public static RegistryEntry<Item> SHOVEL_HEAD_PART;
    public static RegistryEntry<Item> SWORD_HEAD_PART;
    public static RegistryEntry<Item> TOOL_BINDING_PART;
    public static RegistryEntry<Item> TOOL_HANDLE_PART;

    public static RegistryEntry<Item> MODULAR_PICKAXE;
    public static RegistryEntry<Item> MODULAR_AXE;
    public static RegistryEntry<Item> MODULAR_SHOVEL;
    public static RegistryEntry<Item> MODULAR_SWORD;

    public static RegistryEntry<Item> COIL_UPGRADE_BASIC;
    public static RegistryEntry<Item> COIL_UPGRADE_ADVANCED;
    public static RegistryEntry<Item> COIL_UPGRADE_ELITE;

    public static RegistryEntry<Fluid> MOLTEN_COPPER;
    public static RegistryEntry<Fluid> FLOWING_MOLTEN_COPPER;
    public static RegistryEntry<Fluid> MOLTEN_IRON;
    public static RegistryEntry<Fluid> FLOWING_MOLTEN_IRON;
    public static RegistryEntry<Fluid> MOLTEN_GOLD;
    public static RegistryEntry<Fluid> FLOWING_MOLTEN_GOLD;
    public static RegistryEntry<Fluid> MOLTEN_TIN;
    public static RegistryEntry<Fluid> FLOWING_MOLTEN_TIN;
    public static RegistryEntry<Fluid> MOLTEN_LEAD;
    public static RegistryEntry<Fluid> FLOWING_MOLTEN_LEAD;
    public static RegistryEntry<Fluid> MOLTEN_SILVER;
    public static RegistryEntry<Fluid> FLOWING_MOLTEN_SILVER;
    public static RegistryEntry<Fluid> MOLTEN_NICKEL;
    public static RegistryEntry<Fluid> FLOWING_MOLTEN_NICKEL;
    public static RegistryEntry<Fluid> MOLTEN_BRONZE;
    public static RegistryEntry<Fluid> FLOWING_MOLTEN_BRONZE;
    public static RegistryEntry<Fluid> MOLTEN_ELECTRUM;
    public static RegistryEntry<Fluid> FLOWING_MOLTEN_ELECTRUM;

    public static RegistryEntry<BlockEntityType<SolidFuelDynamoBlockEntity>> SOLID_FUEL_DYNAMO_BE;
    public static RegistryEntry<BlockEntityType<InductionCrucibleBlockEntity>> INDUCTION_CRUCIBLE_BE;
    public static RegistryEntry<BlockEntityType<CastingFaucetBlockEntity>> CASTING_FAUCET_BE;
    public static RegistryEntry<BlockEntityType<CastingTableBlockEntity>> CASTING_TABLE_BE;
    public static RegistryEntry<BlockEntityType<AssemblyStationBlockEntity>> ASSEMBLY_STATION_BE;
    public static RegistryEntry<BlockEntityType<ModificationStationBlockEntity>> MODIFICATION_STATION_BE;
    public static RegistryEntry<BlockEntityType<PatternTableBlockEntity>> PATTERN_TABLE_BE;

    public static RegistryEntry<MenuType<SolidFuelDynamoMenu>> SOLID_FUEL_DYNAMO_MENU;
    public static RegistryEntry<MenuType<InductionCrucibleMenu>> INDUCTION_CRUCIBLE_MENU;
    public static RegistryEntry<MenuType<AssemblyStationMenu>> ASSEMBLY_STATION_MENU;
    public static RegistryEntry<MenuType<ModificationStationMenu>> MODIFICATION_STATION_MENU;
    public static RegistryEntry<MenuType<PatternTableMenu>> PATTERN_TABLE_MENU;

    @SuppressWarnings("unchecked")
    public static RegistryEntry<RecipeType<MeltingRecipe>> MELTING_RECIPE_TYPE;
    @SuppressWarnings("unchecked")
    public static RegistryEntry<RecipeSerializer<MeltingRecipe>> MELTING_RECIPE_SERIALIZER;
    @SuppressWarnings("unchecked")
    public static RegistryEntry<RecipeType<CastingRecipe>> CASTING_RECIPE_TYPE;
    @SuppressWarnings("unchecked")
    public static RegistryEntry<RecipeSerializer<CastingRecipe>> CASTING_RECIPE_SERIALIZER;
    @SuppressWarnings("unchecked")
    public static RegistryEntry<RecipeType<AssemblyRecipe>> ASSEMBLY_RECIPE_TYPE;
    @SuppressWarnings("unchecked")
    public static RegistryEntry<RecipeSerializer<AssemblyRecipe>> ASSEMBLY_RECIPE_SERIALIZER;
    @SuppressWarnings("unchecked")
    public static RegistryEntry<RecipeType<PatternToolPartRecipe>> PATTERN_TOOL_PART_RECIPE_TYPE;
    @SuppressWarnings("unchecked")
    public static RegistryEntry<RecipeSerializer<PatternToolPartRecipe>> PATTERN_TOOL_PART_RECIPE_SERIALIZER;

    public static RegistryEntry<RecipeBookCategory> MELTING_RECIPE_BOOK_CATEGORY;
    public static RegistryEntry<RecipeBookCategory> CASTING_RECIPE_BOOK_CATEGORY;
    public static RegistryEntry<RecipeBookCategory> PATTERN_TOOL_PART_RECIPE_BOOK_CATEGORY;

    public static RegistryEntry<DataComponentType<String>> TOOL_PART_MATERIAL;
    public static RegistryEntry<DataComponentType<String>> TOOL_PART_TYPE;
    public static RegistryEntry<DataComponentType<ToolEnergyStorageComponent>> TOOL_ENERGY;
    public static RegistryEntry<DataComponentType<ToolAssembly>> ASSEMBLED_TOOL;
    public static RegistryEntry<DataComponentType<List<String>>> TOOL_TRAITS;
    public static RegistryEntry<DataComponentType<Integer>> TOOL_MODIFIER_SLOTS;
    public static RegistryEntry<DataComponentType<List<ToolModifierState>>> TOOL_MODIFIERS;

    public static RegistryEntry<CreativeModeTab> CREATIVE_TAB;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void init() {
        // --- Blocks ---
        SOLID_FUEL_DYNAMO = registerBlock("solid_fuel_dynamo", SolidFuelDynamoBlock::new);
        INDUCTION_CRUCIBLE = registerBlock("induction_crucible", InductionCrucibleBlock::new);
        CASTING_FAUCET = registerBlock("casting_faucet", () -> new CastingFaucetBlock(
                net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
                        .setId(ResourceKey.create(Registries.BLOCK, id("casting_faucet")))
                        .strength(3.5F)
                        .noOcclusion()));
        CASTING_TABLE = registerBlock("casting_table", CastingTableBlock::new);
        ASSEMBLY_STATION = registerBlock("assembly_station", AssemblyStationBlock::new);
        MODIFICATION_STATION = registerBlock("modification_station", ModificationStationBlock::new);
        PATTERN_TABLE = registerBlock("pattern_table", PatternTableBlock::new);
        CRUCIBLE_BRICKS = registerBlock("crucible_bricks", CrucibleBricksBlock::new);

        // --- Data Components ---
        TOOL_PART_MATERIAL = Services.REGISTRAR.registerDataComponentType(id("tool_part_material"), () -> DataComponentType.<String>builder().persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8).build());
        TOOL_PART_TYPE = Services.REGISTRAR.registerDataComponentType(id("tool_part_type"), () -> DataComponentType.<String>builder().persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8).build());
        TOOL_ENERGY = Services.REGISTRAR.registerDataComponentType(id("tool_energy"), () -> DataComponentType.<ToolEnergyStorageComponent>builder().persistent(ToolEnergyStorageComponent.CODEC).networkSynchronized(ToolEnergyStorageComponent.STREAM_CODEC).build());
        ASSEMBLED_TOOL = Services.REGISTRAR.registerDataComponentType(id("assembled_tool"), () -> DataComponentType.<ToolAssembly>builder().persistent(ToolAssembly.CODEC).networkSynchronized(ToolAssembly.STREAM_CODEC).build());
        TOOL_TRAITS = Services.REGISTRAR.registerDataComponentType(id("tool_traits"), () -> DataComponentType.<List<String>>builder().persistent(Codec.STRING.listOf()).networkSynchronized(ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list())).build());
        TOOL_MODIFIER_SLOTS = Services.REGISTRAR.registerDataComponentType(id("tool_modifier_slots"), () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build());
        TOOL_MODIFIERS = Services.REGISTRAR.registerDataComponentType(id("tool_modifiers"), () -> DataComponentType.<List<ToolModifierState>>builder().persistent(ToolModifierState.LIST_CODEC).networkSynchronized(ToolModifierState.LIST_STREAM_CODEC).build());

        // --- Items ---
        UNFIRED_CRUCIBLE_BRICK = registerItem("unfired_crucible_brick", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id("unfired_crucible_brick")))));
        CRUCIBLE_BRICK = registerItem("crucible_brick", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id("crucible_brick")))));
        BLANK_PATTERN = registerItem("blank_pattern", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id("blank_pattern")))));
        PICKAXE_HEAD_CAST = registerItem("pickaxe_head_cast", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id("pickaxe_head_cast")))));
        AXE_HEAD_CAST = registerItem("axe_head_cast", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id("axe_head_cast")))));
        SHOVEL_HEAD_CAST = registerItem("shovel_head_cast", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id("shovel_head_cast")))));
        SWORD_HEAD_CAST = registerItem("sword_head_cast", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id("sword_head_cast")))));
        TOOL_BINDING_CAST = registerItem("tool_binding_cast", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id("tool_binding_cast")))));
        TOOL_HANDLE_CAST = registerItem("tool_handle_cast", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id("tool_handle_cast")))));

        PICKAXE_HEAD_PATTERN = registerItem("pickaxe_head_pattern", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id("pickaxe_head_pattern")))));
        AXE_HEAD_PATTERN = registerItem("axe_head_pattern", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id("axe_head_pattern")))));
        SHOVEL_HEAD_PATTERN = registerItem("shovel_head_pattern", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id("shovel_head_pattern")))));
        SWORD_HEAD_PATTERN = registerItem("sword_head_pattern", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id("sword_head_pattern")))));
        TOOL_BINDING_PATTERN = registerItem("tool_binding_pattern", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id("tool_binding_pattern")))));
        TOOL_HANDLE_PATTERN = registerItem("tool_handle_pattern", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id("tool_handle_pattern")))));

        PICKAXE_HEAD_PART = registerItem("pickaxe_head_part", () -> new com.warpgames.voltaicforgery.voltaic.item.ToolPartItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id("pickaxe_head_part"))), "generic", "pickaxe_head"));
        AXE_HEAD_PART = registerItem("axe_head_part", () -> new com.warpgames.voltaicforgery.voltaic.item.ToolPartItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id("axe_head_part"))), "generic", "axe_head"));
        SHOVEL_HEAD_PART = registerItem("shovel_head_part", () -> new com.warpgames.voltaicforgery.voltaic.item.ToolPartItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id("shovel_head_part"))), "generic", "shovel_head"));
        SWORD_HEAD_PART = registerItem("sword_head_part", () -> new com.warpgames.voltaicforgery.voltaic.item.ToolPartItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id("sword_head_part"))), "generic", "sword_head"));
        TOOL_BINDING_PART = registerItem("tool_binding_part", () -> new com.warpgames.voltaicforgery.voltaic.item.ToolPartItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id("tool_binding_part"))), "generic", "tool_binding"));
        TOOL_HANDLE_PART = registerItem("tool_handle_part", () -> new com.warpgames.voltaicforgery.voltaic.item.ToolPartItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id("tool_handle_part"))), "generic", "tool_handle"));

        MODULAR_PICKAXE = registerItem("modular_pickaxe", () -> new ModularPickaxeItem(new Item.Properties().stacksTo(1).setId(ResourceKey.create(Registries.ITEM, id("modular_pickaxe")))));
        MODULAR_AXE = registerItem("modular_axe", () -> new ModularAxeItem(new Item.Properties().stacksTo(1).setId(ResourceKey.create(Registries.ITEM, id("modular_axe")))));
        MODULAR_SHOVEL = registerItem("modular_shovel", () -> new ModularShovelItem(new Item.Properties().stacksTo(1).setId(ResourceKey.create(Registries.ITEM, id("modular_shovel")))));
        MODULAR_SWORD = registerItem("modular_sword", () -> new ModularSwordItem(new Item.Properties().stacksTo(1).setId(ResourceKey.create(Registries.ITEM, id("modular_sword")))));

        COIL_UPGRADE_BASIC = registerItem("coil_upgrade_basic", () -> new CoilUpgradeItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id("coil_upgrade_basic"))), CoilTier.BASIC));
        COIL_UPGRADE_ADVANCED = registerItem("coil_upgrade_advanced", () -> new CoilUpgradeItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id("coil_upgrade_advanced"))), CoilTier.ADVANCED));
        COIL_UPGRADE_ELITE = registerItem("coil_upgrade_elite", () -> new CoilUpgradeItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id("coil_upgrade_elite"))), CoilTier.ELITE));

        // --- Fluids ---
        RegistryEntry<Fluid>[] pair;
        pair = registerMoltenFluidPair("copper"); MOLTEN_COPPER = pair[0]; FLOWING_MOLTEN_COPPER = pair[1];
        pair = registerMoltenFluidPair("iron"); MOLTEN_IRON = pair[0]; FLOWING_MOLTEN_IRON = pair[1];
        pair = registerMoltenFluidPair("gold"); MOLTEN_GOLD = pair[0]; FLOWING_MOLTEN_GOLD = pair[1];
        pair = registerMoltenFluidPair("tin"); MOLTEN_TIN = pair[0]; FLOWING_MOLTEN_TIN = pair[1];
        pair = registerMoltenFluidPair("lead"); MOLTEN_LEAD = pair[0]; FLOWING_MOLTEN_LEAD = pair[1];
        pair = registerMoltenFluidPair("silver"); MOLTEN_SILVER = pair[0]; FLOWING_MOLTEN_SILVER = pair[1];
        pair = registerMoltenFluidPair("nickel"); MOLTEN_NICKEL = pair[0]; FLOWING_MOLTEN_NICKEL = pair[1];
        pair = registerMoltenFluidPair("bronze"); MOLTEN_BRONZE = pair[0]; FLOWING_MOLTEN_BRONZE = pair[1];
        pair = registerMoltenFluidPair("electrum"); MOLTEN_ELECTRUM = pair[0]; FLOWING_MOLTEN_ELECTRUM = pair[1];

        // --- Block Entities ---
        SOLID_FUEL_DYNAMO_BE = Services.REGISTRAR.registerBlockEntityType(id("solid_fuel_dynamo"), SolidFuelDynamoBlockEntity::new, () -> SOLID_FUEL_DYNAMO.get());
        INDUCTION_CRUCIBLE_BE = Services.REGISTRAR.registerBlockEntityType(id("induction_crucible"), InductionCrucibleBlockEntity::new, () -> INDUCTION_CRUCIBLE.get());
        CASTING_FAUCET_BE = Services.REGISTRAR.registerBlockEntityType(id("casting_faucet"), CastingFaucetBlockEntity::new, () -> CASTING_FAUCET.get());
        CASTING_TABLE_BE = Services.REGISTRAR.registerBlockEntityType(id("casting_table"), CastingTableBlockEntity::new, () -> CASTING_TABLE.get());
        ASSEMBLY_STATION_BE = Services.REGISTRAR.registerBlockEntityType(id("assembly_station"), AssemblyStationBlockEntity::new, () -> ASSEMBLY_STATION.get());
        MODIFICATION_STATION_BE = Services.REGISTRAR.registerBlockEntityType(id("modification_station"), ModificationStationBlockEntity::new, () -> MODIFICATION_STATION.get());
        PATTERN_TABLE_BE = Services.REGISTRAR.registerBlockEntityType(id("pattern_table"), PatternTableBlockEntity::new, () -> PATTERN_TABLE.get());

        // --- Menus ---
        SOLID_FUEL_DYNAMO_MENU = Services.REGISTRAR.registerMenuType(id("solid_fuel_dynamo"), SolidFuelDynamoMenu::new);
        INDUCTION_CRUCIBLE_MENU = Services.REGISTRAR.registerMenuType(id("induction_crucible"), InductionCrucibleMenu::new);
        ASSEMBLY_STATION_MENU = Services.REGISTRAR.registerMenuType(id("assembly_station"), AssemblyStationMenu::new);
        MODIFICATION_STATION_MENU = Services.REGISTRAR.registerMenuType(id("modification_station"), ModificationStationMenu::new);
        PATTERN_TABLE_MENU = Services.REGISTRAR.registerMenuType(id("pattern_table"), PatternTableMenu::new);

        // --- Recipe Types & Serializers ---
        // RecipeType is an interface; RecipeSerializer is a record in 26.1.2.
        // BuiltInRegistries use wildcard generics, so we must cast through raw types.
        MELTING_RECIPE_TYPE = (RegistryEntry) Services.REGISTRAR.register(BuiltInRegistries.RECIPE_TYPE, id("melting"), () -> new RecipeType<MeltingRecipe>() {
            @Override public String toString() { return "voltaicforgery:melting"; }
        });
        MELTING_RECIPE_SERIALIZER = (RegistryEntry) Services.REGISTRAR.register(BuiltInRegistries.RECIPE_SERIALIZER, id("melting"), () -> new RecipeSerializer<>(MeltingRecipe.CODEC, MeltingRecipe.STREAM_CODEC));

        CASTING_RECIPE_TYPE = (RegistryEntry) Services.REGISTRAR.register(BuiltInRegistries.RECIPE_TYPE, id("casting"), () -> new RecipeType<CastingRecipe>() {
            @Override public String toString() { return "voltaicforgery:casting"; }
        });
        CASTING_RECIPE_SERIALIZER = (RegistryEntry) Services.REGISTRAR.register(BuiltInRegistries.RECIPE_SERIALIZER, id("casting"), () -> new RecipeSerializer<>(CastingRecipe.CODEC, CastingRecipe.STREAM_CODEC));

        ASSEMBLY_RECIPE_TYPE = (RegistryEntry) Services.REGISTRAR.register(BuiltInRegistries.RECIPE_TYPE, id("assembly"), () -> new RecipeType<AssemblyRecipe>() {
            @Override public String toString() { return "voltaicforgery:assembly"; }
        });
        ASSEMBLY_RECIPE_SERIALIZER = (RegistryEntry) Services.REGISTRAR.register(BuiltInRegistries.RECIPE_SERIALIZER, id("assembly"), () -> new RecipeSerializer<>(AssemblyRecipe.CODEC, AssemblyRecipe.STREAM_CODEC));

        PATTERN_TOOL_PART_RECIPE_TYPE = (RegistryEntry) Services.REGISTRAR.register(BuiltInRegistries.RECIPE_TYPE, id("pattern_tool_part"), () -> new RecipeType<PatternToolPartRecipe>() {
            @Override public String toString() { return "voltaicforgery:pattern_tool_part"; }
        });
        PATTERN_TOOL_PART_RECIPE_SERIALIZER = (RegistryEntry) Services.REGISTRAR.register(BuiltInRegistries.RECIPE_SERIALIZER, id("pattern_tool_part"), () -> new RecipeSerializer<>(PatternToolPartRecipe.CODEC, PatternToolPartRecipe.STREAM_CODEC));

        MELTING_RECIPE_BOOK_CATEGORY = Services.REGISTRAR.register(BuiltInRegistries.RECIPE_BOOK_CATEGORY, id("melting"), RecipeBookCategory::new);
        CASTING_RECIPE_BOOK_CATEGORY = Services.REGISTRAR.register(BuiltInRegistries.RECIPE_BOOK_CATEGORY, id("casting"), RecipeBookCategory::new);
        PATTERN_TOOL_PART_RECIPE_BOOK_CATEGORY = Services.REGISTRAR.register(BuiltInRegistries.RECIPE_BOOK_CATEGORY, id("pattern_tool_part"), RecipeBookCategory::new);

        CREATIVE_TAB = Services.REGISTRAR.register(
                BuiltInRegistries.CREATIVE_MODE_TAB,
                id("voltaic_forgery"),
                () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                        .title(Component.translatable("itemGroup.voltaicforgery.voltaic_forgery"))
                        .icon(() -> new ItemStack(PICKAXE_HEAD_CAST.get()))
                        .build()
        );
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    private static RegistryEntry<Block> registerBlock(String name, Supplier<Block> factory) {
        Identifier blockId = id(name);
        RegistryEntry<Block> block = Services.REGISTRAR.register(BuiltInRegistries.BLOCK, blockId, factory);
        registerItem(name, () -> new BlockItem(block.get(), new Item.Properties().setId(ResourceKey.create(Registries.ITEM, blockId))));
        return block;
    }

    private static RegistryEntry<Item> registerItem(String name, Supplier<Item> factory) {
        return Services.REGISTRAR.register(BuiltInRegistries.ITEM, id(name), factory);
    }

    @SuppressWarnings("unchecked")
    private static RegistryEntry<Fluid>[] registerMoltenFluidPair(String metal) {
        RegistryEntry<Fluid>[] pair = new RegistryEntry[2];
        pair[0] = Services.REGISTRAR.register(
                BuiltInRegistries.FLUID,
                id("molten_" + metal),
                () -> new MoltenMetalFluid.Source(() -> pair[0].get(), () -> pair[1].get())
        );
        pair[1] = Services.REGISTRAR.register(
                BuiltInRegistries.FLUID,
                id("flowing_molten_" + metal),
                () -> new MoltenMetalFluid.Flowing(() -> pair[0].get(), () -> pair[1].get())
        );
        return pair;
    }

}
