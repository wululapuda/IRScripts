package cn.wululapuda.irscripts.script;

import cam72cam.immersiverailroading.util.DataBlock;
import cn.wululapuda.irscripts.IRScripts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class StockScriptRegistry {
    private static final Map<String, List<StockScriptFile>> SCRIPTS_BY_DEF = new ConcurrentHashMap<>();

    private StockScriptRegistry() {
    }

    public static void clear() {
        SCRIPTS_BY_DEF.clear();
        ScriptSourceLoader.clearCache();
    }

    public static List<StockScriptFile> getScripts(String defId) {
        List<StockScriptFile> scripts = SCRIPTS_BY_DEF.get(defId);
        return scripts == null ? Collections.emptyList() : scripts;
    }

    public static List<ScriptButtonEntry> getButtonEntries(String defId) {
        List<ScriptButtonEntry> buttons = new ArrayList<>();
        for (StockScriptFile file : getScripts(defId)) {
            for (StockScriptEntry entry : file.getEntries()) {
                if (entry.mode == ScriptMode.BUTTON) {
                    buttons.add(new ScriptButtonEntry(file.path, entry.functionName));
                }
            }
        }
        return buttons;
    }

    public static void parseFromDefinition(String defId, DataBlock data) {
        List<DataBlock> scriptBlocks = data.getBlocks("scripts");
        if (scriptBlocks == null || scriptBlocks.isEmpty()) {
            return;
        }

        List<StockScriptFile> parsed = new ArrayList<>();
        for (DataBlock scriptBlock : scriptBlocks) {
            StockScriptFile file = parseScriptBlock(scriptBlock);
            if (file != null) {
                parsed.add(file);
            }
        }

        if (!parsed.isEmpty()) {
            SCRIPTS_BY_DEF.put(defId, Collections.unmodifiableList(parsed));
            IRScripts.logger.info("Registered {} script file(s) for {}", parsed.size(), defId);
        }
    }

    private static StockScriptFile parseScriptBlock(DataBlock scriptBlock) {
        DataBlock.Value pathValue = scriptBlock.getValue("path");
        String path = pathValue.asString();
        if (path == null || path.isEmpty()) {
            IRScripts.logger.warn("Skipping script entry with missing path");
            return null;
        }

        DataBlock functions = scriptBlock.getBlock("functions");
        if (functions == null) {
            IRScripts.logger.warn("Skipping script {} with no functions block", path);
            return null;
        }

        List<StockScriptEntry> entries = new ArrayList<>();
        for (Map.Entry<String, DataBlock.Value> entry : functions.getValueMap().entrySet()) {
            String functionName = entry.getKey();
            ScriptMode mode = ScriptMode.fromString(entry.getValue().asString());
            if (mode == null) {
                IRScripts.logger.warn("Unknown script mode for {} in {} (function {})", entry.getValue().asString(), path, functionName);
                continue;
            }
            entries.add(new StockScriptEntry(functionName, mode));
        }

        if (entries.isEmpty()) {
            return null;
        }

        return new StockScriptFile(path.toLowerCase(Locale.ROOT), entries);
    }
}
