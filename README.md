# Voltaic Forgery

Forge your tools with electricity, molten metal, and precision-cast parts. **Voltaic Forgery** takes the modular-tool fantasy of classic smeltery mods and gives it a high-tech pulse: FE-powered machines, custom molten fluids, dynamic materials, and tools that can run on energy instead of durability.

## Overview

Voltaic Forgery is a Minecraft **26.1+** multiloader mod built for both **NeoForge** and **Fabric**. It is heavily inspired by the part-crafting and material experimentation of Tinkers' Construct, but replaces lava vats and traditional furnaces with a fully powered metallurgy chain built around **Forge Energy-style FE**.

## Core Progression

Start with rough patterns, work your way into reusable casts, melt metals with power, and assemble custom tools from individually crafted parts.

- **Solid Fuel Dynamo**
  - Burns coal and other furnace fuels to generate FE.
  - Gives early-game players a simple way to bootstrap powered machinery.

- **Induction Crucible**
  - Uses FE to heat and flash-melt ores, ingots, and other inputs.
  - Outputs custom molten fluids such as molten copper, iron, and gold.

- **Casting Table**
  - Uses physical 3D casts to shape molten metal into tool parts.
  - Supports reusable casts and sacrificial mold progression.

- **Assembly Station**
  - Combines heads, bindings, and handles into modular tools.
  - Tools render dynamically with color-tinted layers based on the materials used.

## Modular Tools

Voltaic Forgery tools are assembled from parts instead of crafted as single items. Each part can carry its own material identity, color, stats, durability contribution, and traits.

Current base tools include:

- Pickaxe
- Axe
- Shovel
- Sword

Every assembled tool is built from real part data, so a copper head, stone binding, and wooden handle can produce a visually and mechanically distinct result.

## Modification Station

The **Modification Station** lets players upgrade assembled tools through a slot-based modifier system.

Examples of planned and supported modifier styles include:

- **Redstone** for Haste
- **Quartz** for Sharpness
- **Nether Stars** for extra modifier capacity
- Specialized components for advanced powered behavior

Modifiers use progressive tiers. Starting a new modifier or tier consumes a modifier slot immediately, then additional ingredients fill progress toward that tier's full effect.

## Flagship Feature: Energy Over Durability

Apply an **Induction Coil** modifier to turn a modular tool into a powered tool.

Once installed, the tool gains an internal FE battery. As long as it has charge, it consumes energy instead of durability when used. Your favorite pickaxe can keep working through long mining trips as long as you keep it powered.

## Built for Modpacks

Voltaic Forgery is designed to be **100% data-driven** wherever Minecraft allows it.

Modpack makers and addon authors can define or replace content through standard JSON-driven systems, including:

- Custom tool materials
- Hex material colors
- Durability, mining speed, weapon damage, and tool damage
- Material production routes such as pattern-table parts or molten metals
- Molten fluid associations
- Casting recipes
- Pattern Table recipes
- Assembly recipes
- Modifier ingredients, allowed tools, tier costs, and effect values
- Client-side render data for casting visuals and molten fluid colors

The mod uses modern **Data Components** for modular tool state, material identity, modifier progress, energy storage, and rendered part data. That means tools can carry rich, persistent information without relying on brittle hardcoded item variants.

## Loader Support

Voltaic Forgery is built on a multiloader architecture:

- **NeoForge**
- **Fabric**

Shared gameplay logic lives in the common module, with loader-specific API wiring handled separately for each platform.

## Current Focus

Voltaic Forgery is building toward a complete powered tool-forging foundation:

- Powered ore melting
- Reusable metal casting
- Modular tool assembly
- Data-driven tool materials
- Slot-based modifier progression
- Energy-powered tool durability replacement

More metals, modifiers, alloys, and progression layers are planned as the core systems mature.

