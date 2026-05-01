package com.warpgames.voltaicforgery.voltaic.trait;

import com.warpgames.voltaicforgery.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public final class ToolTraits {

    public static final String AUTO_SMELT = "auto_smelt";
    public static final String MAGNETIC = "magnetic";
    public static final String HASTE = "haste";
    public static final String POWERED = "powered";

    private static final Map<String, ToolTrait> TRAITS = new LinkedHashMap<>();

    static {
        register(AUTO_SMELT, new AutoSmeltTrait());
        register(MAGNETIC, new MagneticTrait());
        register(HASTE, new HasteTrait());
        register(POWERED, new ToolTrait() {});
    }

    private ToolTraits() {}

    public static void register(String id, ToolTrait trait) {
        TRAITS.put(normalize(id), trait);
    }

    public static Optional<ToolTrait> get(String id) {
        return Optional.ofNullable(TRAITS.get(normalize(id)));
    }

    public static List<String> normalizeIds(List<String> traitIds) {
        return traitIds.stream()
                .map(ToolTraits::normalize)
                .filter(id -> !id.isBlank())
                .distinct()
                .toList();
    }

    public static void executeBlockBreak(List<String> traitIds, Level level, BlockPos pos, BlockState state, Player player, ItemStack tool) {
        for (String traitId : normalizeIds(traitIds)) {
            get(traitId).ifPresentOrElse(
                    trait -> safeBlockBreak(traitId, trait, level, pos, state, player, tool),
                    () -> Constants.LOG.warn("Skipping missing Voltaic Forgery tool trait '{}'", traitId)
            );
        }
    }

    public static void executeHitEntity(List<String> traitIds, ItemStack stack, LivingEntity target, LivingEntity attacker) {
        for (String traitId : normalizeIds(traitIds)) {
            get(traitId).ifPresentOrElse(
                    trait -> safeHitEntity(traitId, trait, stack, target, attacker),
                    () -> Constants.LOG.warn("Skipping missing Voltaic Forgery tool trait '{}'", traitId)
            );
        }
    }

    private static void safeBlockBreak(String traitId, ToolTrait trait, Level level, BlockPos pos, BlockState state, Player player, ItemStack tool) {
        try {
            trait.onBlockBreak(level, pos, state, player, tool);
        } catch (RuntimeException exception) {
            Constants.LOG.error("Voltaic Forgery tool trait '{}' failed during block break", traitId, exception);
        }
    }

    private static void safeHitEntity(String traitId, ToolTrait trait, ItemStack stack, LivingEntity target, LivingEntity attacker) {
        try {
            trait.onHitEntity(stack, target, attacker);
        } catch (RuntimeException exception) {
            Constants.LOG.error("Voltaic Forgery tool trait '{}' failed during entity hit", traitId, exception);
        }
    }

    private static String normalize(String id) {
        return id == null ? "" : id.trim().toLowerCase(Locale.ROOT);
    }
}
