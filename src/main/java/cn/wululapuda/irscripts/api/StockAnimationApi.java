package cn.wululapuda.irscripts.api;

import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cam72cam.immersiverailroading.gui.overlay.Readouts;
import cam72cam.immersiverailroading.registry.EntityRollingStockDefinition;
import cam72cam.mod.resource.Identifier;
import cn.wululapuda.irscripts.util.ScriptLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Play IR rolling-stock {@code .anim} animations via control groups.
 * <p>
 * Primary entry: {@link #play(String, String, String, boolean, double)}.
 */
public final class StockAnimationApi {
    private final EntityRollingStock stock;

    public StockAnimationApi(EntityRollingStock stock) {
        this.stock = stock;
    }

    /**
     * @param animFile           animatrix path, e.g. {@code immersiverailroading:amin/1/left.anim}; empty skips file lookup
     * @param controlOrReadout   control group name, or readout name used in JSON
     * @param playMode           IR mode: {@code VALUE}, {@code PLAY_FORWARD}, {@code PLAY_REVERSE}, {@code PLAY_BOTH},
     *                           {@code LOOP}, {@code LOOP_SPEED}
     * @param reverse            flip {@code initialValue} ({@code 1 - value}) before applying
     * @param initialValue       control value {@code 0.0~1.0} written to the control group
     */
    public void play(String animFile, String controlOrReadout, String playMode, boolean reverse, double initialValue) {
        if (!requireServer("play")) {
            return;
        }
        if (!isValidName(controlOrReadout)) {
            ScriptLog.apiWarn("animation", "play", "control group / readout name is empty");
            return;
        }

        EntityRollingStockDefinition.AnimationDefinition definition = resolveDefinition(animFile, controlOrReadout);
        if (definition != null && !playModeMatches(definition, playMode)) {
            ScriptLog.apiWarn("animation", "play", "playMode differs from JSON definition", playMode);
        }

        EntityRollingStockDefinition.AnimationDefinition.AnimationMode mode = parsePlayMode(playMode);
        if (mode == null) {
            ScriptLog.apiWarn("animation", "play", "unknown playMode", playMode);
            return;
        }

        String controlGroup = resolveControlGroup(definition, controlOrReadout);
        if (controlGroup == null) {
            ScriptLog.apiWarn("animation", "play", "readout-driven animation cannot be triggered from script", controlOrReadout);
            return;
        }

        float value = computeControlValue(mode, reverse, initialValue);
        stock.setControlPosition(controlGroup, value);
    }

    public double get(String controlGroup) {
        if (!isValidName(controlGroup)) {
            ScriptLog.apiWarn("animation", "get", "control group name is empty");
            return 0.0D;
        }
        return stock.getControlPosition(controlGroup);
    }

    public List<String> list() {
        List<EntityRollingStockDefinition.AnimationDefinition> animations = stock.getDefinition().animations;
        if (animations == null || animations.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> names = new ArrayList<>();
        for (EntityRollingStockDefinition.AnimationDefinition definition : animations) {
            if (definition.control_group != null && !names.contains(definition.control_group)) {
                names.add(definition.control_group);
            }
        }
        return names;
    }

    private EntityRollingStockDefinition.AnimationDefinition resolveDefinition(String animFile, String controlOrReadout) {
        EntityRollingStockDefinition.AnimationDefinition byName = findByControlOrReadout(controlOrReadout);
        if (!isValidName(animFile)) {
            return byName;
        }

        EntityRollingStockDefinition.AnimationDefinition byFile = findByAnimPath(animFile);
        if (byFile == null) {
            ScriptLog.apiWarn("animation", "play", "anim file not registered on this stock", animFile);
            return byName;
        }
        if (byName != null && byName != byFile) {
            ScriptLog.apiWarn("animation", "play", "anim file and control/readout refer to different entries", animFile);
        }
        return byFile;
    }

    private EntityRollingStockDefinition.AnimationDefinition findByControlOrReadout(String name) {
        List<EntityRollingStockDefinition.AnimationDefinition> animations = stock.getDefinition().animations;
        if (animations == null) {
            return null;
        }
        for (EntityRollingStockDefinition.AnimationDefinition definition : animations) {
            if (name.equals(definition.control_group)) {
                return definition;
            }
            if (definition.readout != null && name.equalsIgnoreCase(definition.readout.name())) {
                return definition;
            }
        }
        return null;
    }

    private EntityRollingStockDefinition.AnimationDefinition findByAnimPath(String path) {
        Identifier identifier = AnimPathUtil.resolve(stock, path.trim());
        List<EntityRollingStockDefinition.AnimationDefinition> animations = stock.getDefinition().animations;
        if (animations == null) {
            return null;
        }
        for (EntityRollingStockDefinition.AnimationDefinition definition : animations) {
            if (definition.animatrix != null && definition.animatrix.equals(identifier)) {
                return definition;
            }
        }
        String identifierText = identifier.toString();
        for (EntityRollingStockDefinition.AnimationDefinition definition : animations) {
            if (definition.animatrix != null && definition.animatrix.toString().equals(identifierText)) {
                return definition;
            }
        }
        return null;
    }

    private static String resolveControlGroup(
            EntityRollingStockDefinition.AnimationDefinition definition,
            String controlOrReadout
    ) {
        if (definition != null) {
            if (definition.control_group != null) {
                return definition.control_group;
            }
            return null;
        }
        if (isReadoutName(controlOrReadout)) {
            return null;
        }
        return controlOrReadout.trim();
    }

    private static boolean playModeMatches(
            EntityRollingStockDefinition.AnimationDefinition definition,
            String playMode
    ) {
        EntityRollingStockDefinition.AnimationDefinition.AnimationMode mode = parsePlayMode(playMode);
        return mode != null && mode == definition.mode;
    }

    private static EntityRollingStockDefinition.AnimationDefinition.AnimationMode parsePlayMode(String playMode) {
        if (!isValidName(playMode)) {
            return null;
        }
        String normalized = playMode.trim().toUpperCase(Locale.ROOT).replace('-', '_');
        try {
            return EntityRollingStockDefinition.AnimationDefinition.AnimationMode.valueOf(normalized);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    /**
     * Maps script parameters to the control-group value IR expects for each mode.
     * {@code initialValue} is always supplied by the script; {@code reverse} flips it when applicable.
     */
    private static float computeControlValue(
            EntityRollingStockDefinition.AnimationDefinition.AnimationMode mode,
            boolean reverse,
            double initialValue
    ) {
        float base = clamp01(initialValue);
        switch (mode) {
            case VALUE:
                return reverse ? clamp01(1.0F - base) : base;
            case LOOP_SPEED:
                if (reverse) {
                    return 0.0F;
                }
                return base <= 0.0F ? 1.0F : base;
            case PLAY_FORWARD:
                if (reverse) {
                    return 0.0F;
                }
                return base >= 0.95F ? base : 1.0F;
            case PLAY_REVERSE:
                if (reverse) {
                    return 1.0F;
                }
                return base <= 0.05F ? base : 0.0F;
            case PLAY_BOTH:
                if (reverse) {
                    return clamp01(1.0F - (base > 0.0F ? base : 1.0F));
                }
                return base >= 0.95F ? base : (base > 0.0F ? base : 1.0F);
            case LOOP:
                if (reverse) {
                    return 0.0F;
                }
                return base >= 0.95F ? base : (base > 0.0F ? base : 1.0F);
            default:
                return reverse ? clamp01(1.0F - base) : base;
        }
    }

    private static boolean isReadoutName(String name) {
        try {
            Readouts.valueOf(name.trim().toUpperCase(Locale.ROOT));
            return true;
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }

    private static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty();
    }

    private static float clamp01(double value) {
        return (float) Math.max(0.0D, Math.min(1.0D, value));
    }

    private boolean requireServer(String action) {
        if (stock.getWorld().isServer) {
            return true;
        }
        ScriptLog.apiIgnored("animation", action, "server-side only");
        return false;
    }
}
