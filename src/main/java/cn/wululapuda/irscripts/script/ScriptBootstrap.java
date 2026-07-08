package cn.wululapuda.irscripts.script;

import cam72cam.immersiverailroading.registry.DefinitionManager;
import cam72cam.immersiverailroading.registry.EntityRollingStockDefinition;
import cam72cam.immersiverailroading.util.DataBlock;
import cam72cam.mod.resource.Identifier;
import cn.wululapuda.irscripts.util.ScriptLog;

import java.util.HashSet;
import java.util.Set;

/**
 * Scans IR stock JSON resources for {@code scripts} blocks.
 * Does not rely on mixins - works even when MixinBooter skips irscripts config.
 */
public final class ScriptBootstrap {
    private static boolean scanned = false;

    private ScriptBootstrap() {
    }

    public static void scanAllDefinitions() {
        if (DefinitionManager.getDefinitionNames() == null || DefinitionManager.getDefinitionNames().isEmpty()) {
            ScriptLog.bootstrapWaiting("DefinitionManager has no definitions yet");
            return;
        }

        StockScriptRegistry.clear();
        int defsChecked = 0;
        int defsWithScripts = 0;
        Set<String> seen = new HashSet<>();

        for (String defId : DefinitionManager.getDefinitionNames()) {
            if (!seen.add(defId)) {
                continue;
            }
            defsChecked++;
            if (scanDefinition(defId)) {
                defsWithScripts++;
            }
        }

        scanned = true;
        ScriptLog.bootstrapComplete(defsChecked, defsWithScripts, StockScriptRegistry.registeredDefinitionCount());
    }

    public static void ensureScanned() {
        if (!scanned) {
            scanAllDefinitions();
        }
    }

    private static boolean scanDefinition(String defId) {
        try {
            Identifier resource = resolveDefinitionResource(defId);
            if (resource == null || !resource.canLoad()) {
                ScriptLog.bootstrapMissing(defId);
                return false;
            }

            DataBlock block = DataBlock.load(resource);
            int before = StockScriptRegistry.registeredDefinitionCount();
            StockScriptRegistry.parseFromDefinition(defId, block);
            return StockScriptRegistry.registeredDefinitionCount() > before;
        } catch (Exception ex) {
            ScriptLog.bootstrapFailed(defId, ex);
            return false;
        }
    }

    private static Identifier resolveDefinitionResource(String defId) {
        EntityRollingStockDefinition definition = DefinitionManager.getDefinition(defId);
        if (definition != null && definition.modelLoc != null) {
            Identifier fromModel = new Identifier(definition.modelLoc.getDomain(), defId);
            if (fromModel.canLoad()) {
                return fromModel;
            }
            Identifier caml = new Identifier(definition.modelLoc.getDomain(), defId.replace(".json", ".caml"));
            if (caml.canLoad()) {
                return caml;
            }
        }

        Identifier resource = new Identifier("immersiverailroading", defId);
        if (resource.canLoad()) {
            return resource;
        }
        Identifier caml = new Identifier("immersiverailroading", defId.replace(".json", ".caml"));
        return caml.canLoad() ? caml : resource;
    }
}
