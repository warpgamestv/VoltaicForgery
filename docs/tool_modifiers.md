# Tool Modifiers

Tool modifiers are data-driven registry entries under `voltaicforgery:tool_modifiers`.
They define what item starts or progresses a modifier, which tools can use it, and how much progress each tier needs.

## Behavior

- Modular tools start with 3 modifier slots.
- Starting a new modifier tier immediately consumes that tier's `slot_cost`.
- Adding more valid items to an already-started tier only increases that tier's progress.
- Progress caps at the current tier requirement. Extra value does not spill into the next tier.
- Once a tier is completed, the modifier trait is added to the tool.
- Adding another valid item after completion starts the next tier, if one exists and the tool has enough slots.
- Haste interpolates mining speed toward each tier's `effect_value`. For example, `1.10` means the filled tier grants 1.10x mining speed.
- Powered uses tier `effect_value` as the tool's max stored energy when the tier completes. If omitted, it falls back to the built-in default.

## Schema

```json
{
  "trait": "haste",
  "allowed_tools": ["pickaxe", "axe", "shovel"],
  "ingredients": [
    {
      "ingredient": "minecraft:redstone",
      "value": 1
    },
    {
      "ingredient": "minecraft:redstone_block",
      "value": 8
    }
  ],
  "tiers": [
    {
      "level": 1,
      "required_value": 64,
      "slot_cost": 1,
      "effect_value": 1.1
    }
  ]
}
```

`allowed_tools` can be omitted or left empty to allow every modular tool type.
Current tool type ids are `pickaxe`, `axe`, `shovel`, and `sword`.
`effect_value` is trait-specific. For `haste`, it is the completed tier mining speed multiplier.
