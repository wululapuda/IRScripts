package cn.wululapuda.irscripts.api;

import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cam72cam.mod.resource.Identifier;

public final class ParticlePathUtil {
    private ParticlePathUtil() {
    }

    public static Identifier resolve(EntityRollingStock stock, String path) {
        String trimmed = path.trim().replace('\\', '/');
        if (trimmed.contains(":")) {
            return new Identifier(trimmed);
        }
        return new Identifier(resolveDomain(stock), trimmed);
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
}
