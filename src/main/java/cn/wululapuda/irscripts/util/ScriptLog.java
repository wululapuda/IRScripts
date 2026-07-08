package cn.wululapuda.irscripts.util;

import cn.wululapuda.irscripts.IRScripts;
import cn.wululapuda.irscripts.script.ScriptMode;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Centralized logging for IR Scripts with categories, script output, and rate-limited LOOP errors.
 *
 * JVM flags:
 *   -Dirscripts.debug=true          enable debug logs
 *   -Dirscripts.scriptPrint=false  silence script print() output
 *   -Dirscripts.loopErrorCooldownMs=5000  min interval between repeated LOOP error logs
 */
public final class ScriptLog {
    private static final long LOOP_ERROR_COOLDOWN_MS = Long.getLong("irscripts.loopErrorCooldownMs", 5000L);
    private static final boolean DEBUG = Boolean.getBoolean("irscripts.debug");
    private static final boolean SCRIPT_PRINT = !"false".equalsIgnoreCase(System.getProperty("irscripts.scriptPrint", "true"));

    private static final Map<String, Long> LOOP_ERROR_LAST_LOG = new ConcurrentHashMap<>();

    private ScriptLog() {
    }

    private static Logger log() {
        return IRScripts.logger;
    }

    public static void startup() {
        log().info("IR Scripts {} starting (Nashorn JS, server-side execution)", IRScripts.VERSION);
        if (DEBUG) {
            log().info("[Config] debug=true, scriptPrint={}, loopErrorCooldownMs={}",
                    SCRIPT_PRINT, LOOP_ERROR_COOLDOWN_MS);
        }
    }

    public static void registryCleared() {
        LOOP_ERROR_LAST_LOG.clear();
        log().info("[Registry] Cleared script registry and source cache");
    }

    public static void registryRegistered(String defId, String scriptPath, String functionName, ScriptMode mode) {
        log().info("[Registry] {} -> {}  {}={}", defId, scriptPath, functionName, mode);
    }

    public static void registryWarn(String message, Object... args) {
        log().warn("[Registry] " + message, args);
    }

    public static void sourceLoaded(String path, int lineCount) {
        if (DEBUG) {
            log().debug("[Source] Loaded {} ({} lines)", path, lineCount);
        }
    }

    public static void sourceFailed(String path, Throwable cause) {
        log().error("[Source] Failed to load {}", path, cause);
    }

    public static void runtimeCreated(UUID stockId, String defId, int scriptFileCount) {
        log().info("[Runtime] Created for stock {} def={} ({} script file(s))",
                shortUuid(stockId), defId, scriptFileCount);
        if (DEBUG) {
            log().debug("[Runtime] Full stock UUID {}", stockId);
        }
    }

    public static void runtimeDisposed(UUID stockId, String defId) {
        if (DEBUG) {
            log().debug("[Runtime] Disposed stock {} def={}", shortUuid(stockId), defId);
        }
    }

    public static void runtimeInitFailed(UUID stockId, String defId, Throwable cause) {
        log().error("[Runtime] Failed to init scripts for stock {} def={}", shortUuid(stockId), defId, cause);
    }

    public static void runtimeNoScripts(UUID stockId, String defId) {
        if (DEBUG) {
            log().debug("[Runtime] No scripts for stock {} def={}", shortUuid(stockId), defId);
        }
    }

    public static void buttonInvoke(UUID stockId, String scriptPath, String functionName) {
        if (DEBUG) {
            log().debug("[Button] stock={} {}#{}", shortUuid(stockId), scriptPath, functionName);
        }
    }

    public static void buttonNotFound(UUID stockId, String scriptPath) {
        log().warn("[Button] Script path {} not found for stock {}", scriptPath, shortUuid(stockId));
    }

    public static void scriptPrint(UUID stockId, String scriptPath, Object message) {
        if (!SCRIPT_PRINT) {
            return;
        }
        log().info("[Script|{}|{}] {}", shortUuid(stockId), scriptPath, message);
    }

    public static void scriptOnceExecuted(UUID stockId, String scriptPath, String functionName) {
        if (DEBUG) {
            log().debug("[Script|{}|{}] ONCE {} executed", shortUuid(stockId), scriptPath, functionName);
        }
    }

    public static void scriptError(UUID stockId, String scriptPath, String functionName, ScriptMode mode, Throwable cause) {
        if (mode == ScriptMode.LOOP && shouldSuppressLoopError(stockId, scriptPath, functionName)) {
            return;
        }
        log().error("[Script|{}|{}] {}#{} ({}) failed",
                shortUuid(stockId), scriptPath, scriptPath, functionName, mode, cause);
    }

    public static void scriptMissingFunction(String scriptPath, String functionName) {
        log().error("[Script|{}] Missing function {}", scriptPath, functionName);
    }

    public static void apiIgnored(String api, String action, String reason, Object... detail) {
        if (DEBUG) {
            if (detail.length == 0) {
                log().debug("[API|{}] {} ignored: {}", api, action, reason);
            } else {
                log().debug("[API|{}] {} ignored: {} ({})", api, action, reason, detail[0]);
            }
        }
    }

    public static void apiWarn(String api, String action, String message, Object... args) {
        log().warn("[API|{}] {}: " + message, prepend(api, action, args));
    }

    public static void soundPlayed(UUID stockId, String identifier, float volume, float pitch, boolean repeat) {
        if (DEBUG) {
            log().debug("[Sound|{}] play {} vol={} pitch={} repeat={}",
                    shortUuid(stockId), identifier, volume, pitch, repeat);
        }
    }

    public static void soundFailed(String context, Throwable cause) {
        log().warn("[Sound] {} failed: {}", context, cause.getMessage());
        if (DEBUG) {
            log().debug("[Sound] detail", cause);
        }
    }

    public static void bootstrapScanStart(String phase) {
        log().info("[Bootstrap] Scanning stock definitions ({})", phase);
    }

    public static void bootstrapWaiting(String reason) {
        log().info("[Bootstrap] Waiting: {}", reason);
    }

    public static void bootstrapComplete(int defsChecked, int defsWithScripts, int registrySize) {
        log().info("[Bootstrap] Done: checked {} definition(s), {} with scripts, {} registry entries",
                defsChecked, defsWithScripts, registrySize);
    }

    public static void bootstrapMissing(String defId) {
        if (DEBUG) {
            log().debug("[Bootstrap] Could not load JSON for {}", defId);
        }
    }

    public static void bootstrapFailed(String defId, Throwable cause) {
        log().warn("[Bootstrap] Failed to scan {}", defId, cause);
    }

    public static void engineCreatedVia(String name) {
        if (DEBUG) {
            log().debug("[Engine] Created via ScriptEngineManager name '{}'", name);
        }
    }

    public static void engineCreated(String engineId) {
        log().info("[Engine] Using {} JavaScript engine", engineId);
    }

    public static void engineDirectUnavailable(String reason) {
        log().warn("[Engine] Direct Nashorn init failed: {}", reason);
    }

    private static boolean shouldSuppressLoopError(UUID stockId, String scriptPath, String functionName) {
        String key = stockId + "|" + scriptPath + "|" + functionName;
        long now = System.currentTimeMillis();
        Long last = LOOP_ERROR_LAST_LOG.put(key, now);
        return last != null && now - last < LOOP_ERROR_COOLDOWN_MS;
    }

    private static String shortUuid(UUID uuid) {
        if (uuid == null) {
            return "null";
        }
        String s = uuid.toString();
        return s.substring(0, 8);
    }

    private static Object[] prepend(String api, String action, Object[] args) {
        Object[] merged = new Object[args.length + 2];
        merged[0] = api;
        merged[1] = action;
        System.arraycopy(args, 0, merged, 2, args.length);
        return merged;
    }
}
