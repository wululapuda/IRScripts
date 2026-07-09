package cn.wululapuda.irscripts.api;

import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cam72cam.mod.resource.Identifier;

public final class AnimPathUtil {
    private AnimPathUtil() {
    }

    public static Identifier resolve(EntityRollingStock stock, String path) {
        String trimmed = path.replace('\\', '/');
        if (trimmed.contains(":")) {
            return new Identifier(trimmed);
        }
        return new Identifier(resolveDomain(stock), normalizeRelativePath(trimmed));
    }

    private static String resolveDomain(EntityRollingStock stock) {
        if (stock == null) {
            return "immersiverailroading";
        }
        String defId = stock.getDefinitionID();
        if (defId != null && defId.contains(":")) {
            return defId.substring(0, defId.indexOf(':'));
        }
        return "immersiverailroading";
    }

    private static String normalizeRelativePath(String path) {
        String normalized = path;
        if (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        if (!normalized.endsWith(".anim")) {
            normalized = normalized + ".anim";
        }
        return normalized;
    }
}
