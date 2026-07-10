package cn.wululapuda.irscripts.script;

import cam72cam.immersiverailroading.library.TrackItems;
import cam72cam.immersiverailroading.util.DataBlock;
import cam72cam.mod.resource.Identifier;
import cn.wululapuda.irscripts.util.ScriptLog;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class TrackScriptRegistry {
    private static final Map<String, Map<TrackScriptType, String>> SCRIPTS_BY_TRACK = new ConcurrentHashMap<>();

    private TrackScriptRegistry() {
    }

    public static void clear() {
        SCRIPTS_BY_TRACK.clear();
        ScriptLog.trackRegistryCleared();
    }

    public static int registeredTrackCount() {
        return SCRIPTS_BY_TRACK.size();
    }

    public static boolean hasScript(String trackId, TrackItems type) {
        return getScriptPath(trackId, type) != null;
    }

    public static boolean usesNative(String trackId, TrackItems type) {
        return getScriptPath(trackId, type) == null;
    }

    public static String getScriptPath(String trackId, TrackItems type) {
        TrackScriptType scriptType = TrackScriptType.fromTrackItem(type);
        if (scriptType == null || trackId == null) {
            return null;
        }
        Map<TrackScriptType, String> scripts = SCRIPTS_BY_TRACK.get(normalizeTrackId(trackId));
        if (scripts == null) {
            return null;
        }
        return scripts.get(scriptType);
    }

    public static String getScriptPath(String trackId, String typeKey) {
        TrackScriptType scriptType = TrackScriptType.fromJsonKey(typeKey);
        if (scriptType == null) {
            return null;
        }
        Map<TrackScriptType, String> scripts = SCRIPTS_BY_TRACK.get(normalizeTrackId(trackId));
        if (scripts == null) {
            return null;
        }
        return scripts.get(scriptType);
    }

    public static Map<TrackScriptType, String> getScripts(String trackId) {
        Map<TrackScriptType, String> scripts = SCRIPTS_BY_TRACK.get(normalizeTrackId(trackId));
        if (scripts == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(scripts);
    }

    public static void parseFromDefinition(String trackId, DataBlock data) {
        DataBlock scriptsBlock = data.getBlock("scripts");
        if (scriptsBlock == null) {
            return;
        }

        Map<TrackScriptType, String> parsed = new EnumMap<>(TrackScriptType.class);
        for (Map.Entry<String, DataBlock.Value> entry : scriptsBlock.getValueMap().entrySet()) {
            TrackScriptType type = TrackScriptType.fromJsonKey(entry.getKey());
            if (type == null) {
                ScriptLog.trackRegistryWarn("Unknown track script key '{}' in {}", entry.getKey(), trackId);
                continue;
            }

            String rawPath = entry.getValue().asString();
            if (rawPath == null || rawPath.trim().isEmpty()) {
                continue;
            }

            String resolved = resolveScriptPath(trackId, rawPath.trim());
            parsed.put(type, resolved);
            ScriptLog.trackRegistryRegistered(trackId, type.getJsonKey(), resolved);
        }

        if (!parsed.isEmpty()) {
            SCRIPTS_BY_TRACK.put(normalizeTrackId(trackId), Collections.unmodifiableMap(parsed));
        }
    }

    private static String resolveScriptPath(String trackId, String rawPath) {
        String path = rawPath.replace('\\', '/');
        if (path.contains(":")) {
            return path.endsWith(".js") ? path : path + ".js";
        }

        String domain = new Identifier(trackId).getDomain();
        if (!path.endsWith(".js")) {
            path = path + ".js";
        }
        if (!path.contains("/")) {
            path = "scripts/" + path;
        }
        return domain + ":" + path;
    }

    private static String normalizeTrackId(String trackId) {
        return trackId.toLowerCase(Locale.ROOT);
    }
}
