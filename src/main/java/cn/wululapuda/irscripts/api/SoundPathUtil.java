package cn.wululapuda.irscripts.api;

import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cam72cam.mod.resource.Identifier;

public final class SoundPathUtil {
    private SoundPathUtil() {
    }

    public static Identifier resolve(EntityRollingStock stock, String path) {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Sound path cannot be empty");
        }

        String trimmed = path.trim().replace('\\', '/');
        if (trimmed.contains(":")) {
            return new Identifier(trimmed);
        }

        String domain = resolveDomain(stock);
        return new Identifier(domain, normalizeRelativePath(trimmed));
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
        if (!normalized.startsWith("sounds/")) {
            normalized = "sounds/" + normalized;
        }
        if (!normalized.endsWith(".ogg")) {
            normalized = normalized + ".ogg";
        }
        return normalized;
    }
}
