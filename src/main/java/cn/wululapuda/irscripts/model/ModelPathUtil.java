package cn.wululapuda.irscripts.model;

import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cam72cam.mod.resource.Identifier;

public final class ModelPathUtil {
    private ModelPathUtil() {
    }

    public static Identifier resolve(EntityRollingStock stock, String path) {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Model path cannot be empty");
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
        if (!normalized.startsWith("models/")) {
            normalized = "models/" + normalized;
        }
        if (!normalized.endsWith(".obj")) {
            normalized = normalized + ".obj";
        }
        return normalized;
    }
}
