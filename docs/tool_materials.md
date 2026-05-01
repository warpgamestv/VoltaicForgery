# Tool Materials

Tool materials are data-driven through the `voltaicforgery:tool_materials` datapack registry.

Default generated material files live under:

`data/voltaicforgery/voltaicforgery/tool_materials/`

Example:

```json
{
  "material_id": "copper",
  "durability": 200,
  "mining_speed": 5.0,
  "tool_damage": 2.0,
  "weapon_damage": 2.0,
  "head_durability_multiplier": 1.0,
  "binding_durability_multiplier": 0.25,
  "handle_durability_multiplier": 0.5,
  "color": 15170646,
  "tool_type": "iron",
  "hardcoded_modifiers": [
    "magnetic"
  ],
  "material_type": "molten",
  "molten_fluid": "voltaicforgery:molten_copper",
  "repair_ingredient": "minecraft:copper_ingot"
}
```

Fields:

- `material_id`: the id stored on tool part item stacks.
- `durability`: base durability value used by each part contribution multiplier.
- `head_durability_multiplier`: how much of this material's durability counts when used as a head. Defaults to `1.0`.
- `binding_durability_multiplier`: how much of this material's durability counts when used as a binding. Defaults to `0.25`.
- `handle_durability_multiplier`: how much of this material's durability counts when used as a handle. Defaults to `0.5`.
- `mining_speed`: mining speed used by tool heads.
- `tool_damage`: damage bonus for tool-shaped weapons such as pickaxes, axes, and shovels.
- `weapon_damage`: damage bonus for weapon-shaped items such as swords.
- `color`: decimal RGB color used to tint parts and modular tool layers.
- `tool_type`: mining tier/type label for material logic. Modular pickaxe, axe, and shovel harvest rules read this value instead of the material id.
- `hardcoded_modifiers`: built-in material traits such as `magnetic`.
- `material_type`: production route/category. Defaults use `part_table`, `molten`, or `solid`.
- `molten_fluid`: optional molten fluid id for materials made through casting.
- `repair_ingredient`: optional ingredient used by the modification station repair flow.

Pattern Table support is still controlled by `voltaicforgery:pattern_tool_part` recipes. A material with `material_type: "part_table"` still needs pattern recipes before it can be made in the Pattern Table.

Generated default material files may omit the durability multiplier fields when they match the defaults above. Datapacks can still add any of the multiplier fields to change how strongly that material contributes as a head, binding, or handle.
