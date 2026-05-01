# Pattern Table Tool Part Recipes

The Pattern Table uses these recipes as its material allow list.

Each recipe matches:

- the selected `part`
- a `blank_pattern` in the pattern slot
- the `material` ingredient in the material slot

If no `voltaicforgery:pattern_tool_part` recipe matches, the Pattern Table outputs nothing. This keeps metal materials out of pre-crucible progression unless a datapack or addon explicitly adds support.

The output part gets its color, durability, mining speed, tool damage, weapon damage, repair ingredient, and hardcoded modifiers from the matching entry in the `voltaicforgery:tool_materials` datapack registry. The recipe controls which input item or tag is allowed in the Pattern Table; the material registry controls what that material means after the part is made.

Default supported materials:

- `wood` via `#minecraft:planks`
- `stone` via `#minecraft:stone_tool_materials`

Example:

```json
{
  "type": "voltaicforgery:pattern_tool_part",
  "part": "pickaxe_head",
  "pattern": "voltaicforgery:blank_pattern",
  "material": "#minecraft:planks",
  "result": {
    "id": "voltaicforgery:pickaxe_head_part",
    "count": 1
  },
  "result_material": "wood",
  "icon": "voltaicforgery:pickaxe_head_pattern"
}
```

The Pattern Table discovers its selectable part buttons from loaded `pattern_tool_part` recipes. Add a new recipe with a new `part` id to expose another button. `icon` is optional and controls the button/JEI display item; if omitted, the recipe result item is used.

To add a new nonmetal material later:

1. Add a `voltaicforgery:tool_materials` entry for the new material id.
2. Add one `voltaicforgery:pattern_tool_part` recipe per supported part.
3. Set each recipe's `result_material` to the material id from step 1.
