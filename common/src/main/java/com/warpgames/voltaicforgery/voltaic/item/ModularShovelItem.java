package com.warpgames.voltaicforgery.voltaic.item;

import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import com.warpgames.voltaicforgery.voltaic.tool.ToolAssembly;
import com.warpgames.voltaicforgery.voltaic.tool.ToolDurabilityResolver;
import com.warpgames.voltaicforgery.voltaic.tool.VanillaToolComponentResolver;
import com.warpgames.voltaicforgery.voltaic.tool.ToolMaterialResolver;
import com.warpgames.voltaicforgery.voltaic.tool.ToolMaterialStat;
import com.warpgames.voltaicforgery.voltaic.trait.HasteTrait;
import com.warpgames.voltaicforgery.voltaic.trait.ToolTraits;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;

public class ModularShovelItem extends Item implements IModularTool {

    private static final Identifier ATTACK_DAMAGE_ID = Identifier.fromNamespaceAndPath("voltaicforgery", "modular_shovel_attack_damage");
    private static final Identifier ATTACK_SPEED_ID = Identifier.fromNamespaceAndPath("voltaicforgery", "modular_shovel_attack_speed");
    private static final float SHOVEL_ATTACK_SPEED = -3.0F;

    public ModularShovelItem(Properties properties) {
        super(properties
                .stacksTo(1)
                .component(VoltaicContent.ASSEMBLED_TOOL.get(), ToolAssembly.DEFAULT)
                .component(VoltaicContent.TOOL_TRAITS.get(), List.of())
                .component(VoltaicContent.TOOL_MODIFIER_SLOTS.get(), 3)
                .component(VoltaicContent.TOOL_MODIFIERS.get(), List.of())
                .component(DataComponents.MAX_DAMAGE, calculateMaxDamage(ToolAssembly.DEFAULT, null))
                .attributes(createAttributes(ToolAssembly.DEFAULT, null))
        );
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        applyDerivedComponents(stack);
        return stack;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        applyDerivedComponents(stack, level.registryAccess());
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return calculateMiningSpeed(stack, null);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
        if (miningEntity instanceof net.minecraft.world.entity.player.Player player) {
            ToolTraits.executeBlockBreak(ModularPickaxeItem.traits(stack), level, pos, state, player, stack);
        }
        ModularToolDurability.damageForMining(stack, level, state, pos, miningEntity);
        return true;
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        ToolTraits.executeHitEntity(ModularPickaxeItem.traits(stack), stack, target, attacker);
        ModularToolDurability.damageForAttack(stack, attacker);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltip, flag);
        ModularToolHelper.appendTooltip(stack, tooltip);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return ModularToolHelper.isBarVisible(stack, super.isBarVisible(stack));
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return ModularToolHelper.getBarWidth(stack, super.getBarWidth(stack));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return ModularToolHelper.getBarColor(stack, super.getBarColor(stack));
    }

    public static ToolAssembly assembly(ItemStack stack) {
        return stack.getOrDefault(VoltaicContent.ASSEMBLED_TOOL.get(), ToolAssembly.DEFAULT);
    }

    public static void applyDerivedComponents(ItemStack stack) {
        applyDerivedComponents(stack, null);
    }

    public static void applyDerivedComponents(ItemStack stack, HolderLookup.Provider registries) {
        ToolAssembly assembly = assembly(stack);
        stack.set(DataComponents.MAX_DAMAGE, calculateMaxDamage(assembly, registries));
        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(assembly, registries));
        ToolMaterialStat head = material(registries, assembly.headMaterial());
        Tool vanillaTool = VanillaToolComponentResolver.resolveToolComponent(
                VanillaToolComponentResolver.ToolKind.SHOVEL,
                head.toolType()
        );
        if (vanillaTool != null) {
            stack.set(DataComponents.TOOL, vanillaTool);
        }
    }

    private static int calculateMaxDamage(ToolAssembly assembly, HolderLookup.Provider registries) {
        ToolMaterialStat head = material(registries, assembly.headMaterial());
        ToolMaterialStat binding = material(registries, assembly.bindingMaterial());
        ToolMaterialStat handle = material(registries, assembly.handleMaterial());
        return ToolDurabilityResolver.calculate(head, binding, handle);
    }

    private static float calculateMiningSpeed(ItemStack stack, HolderLookup.Provider registries) {
        float baseSpeed = material(registries, assembly(stack).headMaterial()).miningSpeed();
        return HasteTrait.applyMiningSpeed(stack, baseSpeed);
    }

    private static float calculateAttackDamage(ToolAssembly assembly, HolderLookup.Provider registries) {
        return 1.5F + material(registries, assembly.headMaterial()).toolDamage()
                + (material(registries, assembly.bindingMaterial()).toolDamage() * 0.25F);
    }

    private static ItemAttributeModifiers createAttributes(ToolAssembly assembly, HolderLookup.Provider registries) {
        return ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(ATTACK_DAMAGE_ID, calculateAttackDamage(assembly, registries), AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(ATTACK_SPEED_ID, SHOVEL_ATTACK_SPEED, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .build();
    }

    private static ToolMaterialStat material(HolderLookup.Provider registries, String materialId) {
        return ToolMaterialResolver.resolve(registries, materialId);
    }
}

