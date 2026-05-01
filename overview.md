# Voltaic Forgery — Project Overview (current state)

This document summarizes **everything implemented so far**, **everything registered**, and **what is partial/missing** in the current workspace.

---

## Namespace

- **Mod ID / namespace**: `voltaicforgery`

---

## Registered content (in code registries)

Source of truth: `common/src/main/java/com/warpgames/voltaicforgery/voltaic/VoltaicContent.java`

### Blocks (`Registries.BLOCK`)

- `voltaicforgery:solid_fuel_dynamo`
- `voltaicforgery:induction_crucible`
- `voltaicforgery:assembly_station`
- `voltaicforgery:casting_table`
- `voltaicforgery:modification_station`
- `voltaicforgery:crucible_bricks`

### Block items (`Registries.ITEM`)

Each block above has a `BlockItem` registered with the same ID:

- `voltaicforgery:solid_fuel_dynamo`
- `voltaicforgery:induction_crucible`
- `voltaicforgery:assembly_station`
- `voltaicforgery:casting_table`
- `voltaicforgery:modification_station`
- `voltaicforgery:crucible_bricks`

### Items (`Registries.ITEM`)

**Upgrades / misc**

- `voltaicforgery:coil_upgrade_basic`
- `voltaicforgery:coil_upgrade_advanced`
- `voltaicforgery:coil_upgrade_elite`
- `voltaicforgery:crucible_brick`
- `voltaicforgery:induction_coil`

**Tool parts**

- `voltaicforgery:iron_tool_part`
- `voltaicforgery:copper_tool_part`
- `voltaicforgery:steel_tool_part`
- `voltaicforgery:pickaxe_head_part`
- `voltaicforgery:axe_head_part`
- `voltaicforgery:shovel_head_part`
- `voltaicforgery:sword_head_part`
- `voltaicforgery:tool_binding_part`
- `voltaicforgery:tool_handle_part`

**Modular tools**

- `voltaicforgery:modular_pickaxe`
- `voltaicforgery:modular_axe`
- `voltaicforgery:modular_shovel`
- `voltaicforgery:modular_sword`

**Casts**

- `voltaicforgery:pickaxe_head_cast`
- `voltaicforgery:axe_head_cast`
- `voltaicforgery:shovel_head_cast`
- `voltaicforgery:sword_head_cast`
- `voltaicforgery:tool_binding_cast`
- `voltaicforgery:tool_handle_cast`

### Block entity types (`Registries.BLOCK_ENTITY_TYPE`)

- `voltaicforgery:solid_fuel_dynamo`
- `voltaicforgery:induction_crucible`
- `voltaicforgery:assembly_station`
- `voltaicforgery:casting_table`
- `voltaicforgery:modification_station`

### Menu types (`Registries.MENU`)

- `voltaicforgery:assembly_station`
- `voltaicforgery:solid_fuel_dynamo`
- `voltaicforgery:induction_crucible`
- `voltaicforgery:modification_station`

### Recipe type + serializer + recipe book category

- `voltaicforgery:casting` (type)
- `voltaicforgery:casting` (serializer)
- `voltaicforgery:casting` (recipe book category)

### Data component types (`Registries.DATA_COMPONENT_TYPE`)

- `voltaicforgery:tool_part_material`
- `voltaicforgery:tool_part_type`
- `voltaicforgery:assembled_tool`
- `voltaicforgery:tool_energy`
- `voltaicforgery:tool_traits`
- `voltaicforgery:tool_modifier_slots`

### Creative tab (`Registries.CREATIVE_MODE_TAB`)

- `voltaicforgery:voltaic_forgery`

Loader-specific population hooks:

- Fabric: `fabric/src/main/java/com/warpgames/voltaicforgery/VoltaicForgery.java`
- NeoForge: `neoforge/src/main/java/com/warpgames/voltaicforgery/platform/NeoForgeVoltaicCreativeTabs.java`

---

## Custom datapack registries (synced dynamic registries)

Source: `common/src/main/java/com/warpgames/voltaicforgery/voltaic/registry/VoltaicRegistries.java`

### Registry keys (the registries themselves)

- `voltaicforgery:tool_materials`
- `voltaicforgery:tool_modifiers`

### Tool materials (bootstrapped entries)

- `voltaicforgery:wood`
- `voltaicforgery:stone`
- `voltaicforgery:copper`
- `voltaicforgery:iron`
- `voltaicforgery:gold`
- `voltaicforgery:diamond`
- `voltaicforgery:netherite`

### Tool modifiers (bootstrapped entries)

- `voltaicforgery:redstone_haste`
- `voltaicforgery:induction_coil`

Loader wiring:

- Fabric: dynamic registry registration in `VoltaicForgery.java`
- NeoForge: datapack registry event in `platform/NeoForgeVoltaicDatapackRegistries.java`

---

## Data/asset content currently in the repo (high-signal)

### GUI textures

- `common/src/main/resources/assets/voltaicforgery/textures/gui/solid_fuel_dynamo.png`
- `common/src/main/resources/assets/voltaicforgery/textures/gui/assembly_station.png`

### Crafting recipes (JSON)

Located in `common/src/main/resources/data/voltaicforgery/recipe/`:

- `solid_fuel_dynamo.json`
- `induction_crucible.json`
- `casting_table.json`
- `assembly_station.json`
- `modification_station.json`
- `crucible_bricks.json`

### Loot tables (blocks drop themselves)

Located in `common/src/main/resources/data/voltaicforgery/loot_table/blocks/`:

- `solid_fuel_dynamo.json`
- `induction_crucible.json`
- `casting_table.json`
- `assembly_station.json`
- `modification_station.json`
- `crucible_bricks.json`

### Block tags

Located in `common/src/main/resources/data/minecraft/tags/blocks/`:

- `mineable/pickaxe.json`
- `needs_stone_tool.json`

---

## Implemented features (what works today)

### Modular tools (pickaxe/axe/shovel/sword)

- 4 modular tools exist and store part composition in the `assembled_tool` data component.
- Tool stats are derived from the assembly:
  - head material determines harvest tier (vanilla `DataComponents.TOOL` resolution)
  - durability is additive (head + binding/handle bonus)
- Tooltips now show the assembly materials (head/binding/handle).

Key files:

- `common/src/main/java/.../item/ModularPickaxeItem.java`
- `common/src/main/java/.../item/ModularAxeItem.java`
- `common/src/main/java/.../item/ModularShovelItem.java`
- `common/src/main/java/.../item/ModularSwordItem.java`
- `common/src/main/java/.../tool/VanillaToolComponentResolver.java`

### Tool traits framework

- Trait IDs are string-based and executed on:
  - block break
  - entity hit
- Known trait IDs:
  - `auto_smelt`, `magnetic`, `haste`, `powered`

Key file:

- `common/src/main/java/.../trait/ToolTraits.java`

### Energy conversion (powered tools)

- `tool_energy` data component exists, including persistence + networking.
- Modular pickaxe consumes energy instead of durability when powered.
- Mixins intercept some durability break flows for attacks.
- NeoForge item energy capability wrapper exists for charging interoperability.

Key files:

- `common/src/main/java/.../item/component/ToolEnergyStorageComponent.java`
- `common/src/main/java/.../api/energy/IEnergyTool.java`
- `common/src/main/java/.../api/energy/VoltaicItemEnergy.java`
- `common/src/main/java/.../item/ModularPickaxeItem.java`
- `common/src/main/java/.../mixin/ItemStackPostHurtEnemyMixin.java`
- `neoforge/src/main/java/.../platform/NeoForgeVoltaicCapabilities.java`
- `neoforge/src/main/java/.../platform/NeoForgeModularPickaxeItemEnergyHandler.java`

### Solid Fuel Dynamo (block + BE + menu + screen)

- 2-slot inventory: fuel input + charge/output slot.
- `LIT` toggles while burning.
- GUI renders:
  - background texture
  - energy bar fill
  - animated furnace flame
  - numeric energy readout + hover tooltip
- **Note**: the **charge/output slot is not wired yet** (no item charging/transfer logic implemented in the BE tick), and **shift-click is not implemented** in the menu.

Key files:

- `common/src/main/java/.../block/SolidFuelDynamoBlock.java`
- `common/src/main/java/.../blockentity/SolidFuelDynamoBlockEntity.java`
- `common/src/main/java/.../menu/SolidFuelDynamoMenu.java`
- `common/src/main/java/.../client/SolidFuelDynamoScreen.java`

### Casting Table (interaction + rendering + sync)

- Right-click insert/extract logic exists.
- Cast item renders in-world with the desired 14x14 scale + 90° rotation.
- Block entity sync to client is implemented to update rendering.

Key files:

- `common/src/main/java/.../block/CastingTableBlock.java`
- `common/src/main/java/.../blockentity/CastingTableBlockEntity.java`
- `common/src/main/java/.../client/CastingTableBlockEntityRenderer.java`

### Induction Crucible (registered; partially implemented)

- Block + BE + menu + screen exist.
- Coil upgrades affect heating/tiers.
- Fluid tank scaffolding exists (serialization is currently acknowledged as “to be expanded once molten metal fluids are defined”).
- **Note**: processing/melting into real molten fluids is not finished; screen is still a placeholder-style UI; shift-click is not implemented in the menu.

Key files:

- `common/src/main/java/.../block/InductionCrucibleBlock.java`
- `common/src/main/java/.../blockentity/InductionCrucibleBlockEntity.java`
- `common/src/main/java/.../menu/InductionCrucibleMenu.java`
- `common/src/main/java/.../client/InductionCrucibleScreen.java`

### Modification Station (registered; partially implemented)

- Block + BE + menu + screen exist.
- Tool modifier datapack registry exists (`tool_modifiers`).
- **Note**: UI is still placeholder-style, and the modifier logic is currently focused on modular pickaxe workflows.

Key files:

- `common/src/main/java/.../blockentity/ModificationStationBlockEntity.java`
- `common/src/main/java/.../menu/ModificationStationMenu.java`
- `common/src/main/java/.../client/ModificationStationScreen.java`
- `common/src/main/java/.../tool/ToolModifierEntry.java`
- `common/src/main/java/.../tool/ToolModifierQuery.java`

### Assembly Station (3 inputs + output)

- Inventory layout refactored from a 3x3 grid to:
  - head slot
  - binding slot
  - handle slot
  - output slot
- Output tool matches head type (pickaxe/axe/shovel/sword head).
- GUI texture is rendered.
- Player inventory slot positions are vanilla-aligned.

Key files:

- `common/src/main/java/.../blockentity/AssemblyStationBlockEntity.java`
- `common/src/main/java/.../menu/AssemblyStationMenu.java`
- `common/src/main/java/.../client/AssemblyStationScreen.java`

### Recipes/tags/loot for machine blocks + crucible bricks

- Crafting recipes for machines and `crucible_bricks` exist.
- All machine blocks and `crucible_bricks` have loot tables and are tagged:
  - mineable with pickaxe
  - needs stone tool

---

## Not fully implemented / known gaps

### Tool part “material variants” are not actually registered as separate item IDs

Only *single* items like `voltaicforgery:pickaxe_head_part` exist (and are currently constructed with default material `"copper"`).

IDs like `voltaicforgery:pickaxe_head_part_iron` / `..._wood` / etc are **not registered** right now.

Impact:

- `/give @p voltaicforgery:pickaxe_head_part_iron` will not work until those are registered (or a different approach is used).

### Casting datagen references a molten fluid that is not registered in this repo

The casting recipe generator references:

- `voltaicforgery:molten_copper`

But there is no fluid registration found for this ID in the current workspace.

Impact:

- Casting recipes may be present but not usable until fluid content exists (or IDs are corrected).

### Unregistered item asset: `blank_cast`

There is an item model:

- `common/src/main/resources/assets/voltaicforgery/models/item/blank_cast.json`

But there is **no** `voltaicforgery:blank_cast` item registered.

Impact:

- Asset exists but cannot be obtained/used as an item without registration.

### Blockstates: common vs generated

- Some blockstates/models are produced via datagen (notably under `neoforge/src/generated/resources/...`).
- If you want the project to be “complete” without relying on generated outputs being present, you’ll want to ensure **final blockstates/models** are committed under `common/src/main/resources/assets/voltaicforgery/`.

### Induction Crucible and Modification Station may be partial

They are registered (block + BE + menus), but the project still needs a full “end-to-end” pass to confirm:

- complete processing logic (recipes/ticking/output rules)
- GUI completeness (screens for crucible/mod station if desired)
- transfer/capability behavior (energy/fluid/item automation)

### Fabric-side cross-mod energy charging interoperability

NeoForge exposes the item energy capability wrapper for charging stations.

Fabric currently documents the lack of a direct equivalent; full “chargeable in other mods’ stations” parity likely requires adopting/bridging a Fabric energy API used by target mods.

### Missing / placeholder textures

Any missing textures/models will show as missing in-game (you previously mentioned sword textures weren’t a priority yet).

---

## Current work (active focus)

- Assembly Station:
  - GUI layout alignment (input slot spacing, player inventory alignment)
  - verifying correct output for all head types
  - validating material tooltips for parts + tools

---

## Appendix: Assembly Station slot coordinates (current)

From `AssemblyStationMenu`:

- Head: `(40, 38)`
- Binding: `(61, 38)`
- Handle: `(81, 38)`
- Output: `(134, 38)`

Player inventory (vanilla container layout):

- Inventory rows start at `y = 84`
- Hotbar at `y = 142`

