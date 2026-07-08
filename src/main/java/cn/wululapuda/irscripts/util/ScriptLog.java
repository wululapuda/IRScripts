package cn.wululapuda.irscripts.util;

import cn.wululapuda.irscripts.IRScripts;
import cn.wululapuda.irscripts.config.ScriptRuntimeSettings;
import cn.wululapuda.irscripts.script.ScriptMode;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

/**
 * Centralized logging for IR Scripts with categories and script output.
 *
 * JVM flags:
 *   -Dirscripts.debug=true          enable debug logs
 *   -Dirscripts.scriptPrint=false  silence script print() output
 */
public final class ScriptLog {
    private ScriptLog() {
    }

    private static Logger log() {
        return IRScripts.logger;
    }

    public static void startup() {
        log().info("IR Scripts {} starting (Rhino JS, server-side execution)", IRScripts.VERSION);
        if (ScriptRuntimeSettings.isDebug()) {
            log().info("[Config] debug=true, scriptPrint={}", ScriptRuntimeSettings.isScriptPrint());
        }
    }

    public static void configUpdated(boolean scriptPrint, boolean debug) {
        log().info("[Config] Updated: scriptPrint={}, debug={}", scriptPrint, debug);
    }

    public static void registryCleared() {
        log().info("[Registry] Cleared script registry and source cache");
    }

    public static void registryRegistered(String defId, String scriptPath, String functionName, ScriptMode mode) {
        log().info("[Registry] {} -> {}  {}={}", defId, scriptPath, functionName, mode);
    }

    public static void registryWarn(String message, Object... args) {
        log().warn("[Registry] " + message, args);
    }

    public static void sourceLoaded(String path, int lineCount) {
        if (ScriptRuntimeSettings.isDebug()) {
            log().debug("[Source] Loaded {} ({} lines)", path, lineCount);
        }
    }

    public static void sourceFailed(String path, Throwable cause) {
        log().error("[Source] Failed to load {}", path, cause);
    }

    public static void runtimeCreated(UUID stockId, String defId, int scriptFileCount) {
        log().info("[Runtime] Created for stock {} def={} ({} script file(s))",
                shortUuid(stockId), defId, scriptFileCount);
        if (ScriptRuntimeSettings.isDebug()) {
            log().debug("[Runtime] Full stock UUID {}", stockId);
        }
    }

    public static void runtimeDisposed(UUID stockId, String defId) {
        if (ScriptRuntimeSettings.isDebug()) {
            log().debug("[Runtime] Disposed stock {} def={}", shortUuid(stockId), defId);
        }
    }

    public static void runtimeInitFailed(UUID stockId, String defId, Throwable cause) {
        log().error("[Runtime] Failed to init scripts for stock {} def={}", shortUuid(stockId), defId, cause);
    }

    public static void runtimeNoScripts(UUID stockId, String defId) {
        if (ScriptRuntimeSettings.isDebug()) {
            log().debug("[Runtime] No scripts for stock {} def={}", shortUuid(stockId), defId);
        }
    }

    public static void buttonInvoke(UUID stockId, String scriptPath, String functionName) {
        if (ScriptRuntimeSettings.isDebug()) {
            log().debug("[Button] stock={} {}#{}", shortUuid(stockId), scriptPath, functionName);
        }
    }

    public static void buttonNotFound(UUID stockId, String scriptPath) {
        log().warn("[Button] Script path {} not found for stock {}", scriptPath, shortUuid(stockId));
    }

    public static void scriptPrint(UUID stockId, String scriptPath, Object message) {
        if (!ScriptRuntimeSettings.isScriptPrint()) {
            return;
        }
        log().info("[Script|{}|{}] {}", shortUuid(stockId), scriptPath, message);
    }

    public static void scriptOnceExecuted(UUID stockId, String scriptPath, String functionName) {
        if (ScriptRuntimeSettings.isDebug()) {
            log().debug("[Script|{}|{}] ONCE {} executed", shortUuid(stockId), scriptPath, functionName);
        }
    }

    public static void scriptError(UUID stockId, String scriptPath, String functionName, ScriptMode mode, Throwable cause) {
        log().error("[Script|{}|{}] {}#{} ({}) failed",
                shortUuid(stockId), scriptPath, scriptPath, functionName, mode, cause);
    }

    public static void scriptLoopDisabled(UUID stockId, String scriptPath, String functionName, ScriptMode mode) {
        log().warn("[Script|{}|{}] {}#{} ({}) disabled after error; will not run again for this stock instance",
                shortUuid(stockId), scriptPath, scriptPath, functionName, mode);
    }

    public static void scriptMissingFunction(String scriptPath, String functionName) {
        log().error("[Script|{}] Missing function {}", scriptPath, functionName);
    }

    public static void apiIgnored(String api, String action, String reason, Object... detail) {
        if (ScriptRuntimeSettings.isDebug()) {
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

    public static void soundPlayed(UUID stockId, String identifier, float volume, float pitch, boolean repeat, int maxDistance) {
        if (ScriptRuntimeSettings.isDebug()) {
            log().debug("[Sound|{}] play {} vol={} pitch={} repeat={} distance={}",
                    shortUuid(stockId), identifier, volume, pitch, repeat, maxDistance);
        }
    }

    public static void soundFailed(String context, Throwable cause) {
        log().warn("[Sound] {} failed: {}", context, cause.getMessage());
        if (ScriptRuntimeSettings.isDebug()) {
            log().debug("[Sound] detail", cause);
        }
    }

    public static void soundStopped(UUID stockId, String scope, String detail) {
        if (ScriptRuntimeSettings.isDebug()) {
            log().debug("[Sound|{}] stopPlay {} {}", shortUuid(stockId), scope, detail == null ? "" : detail);
        }
    }

    public static void utilPlayWait(UUID stockId, String path, long durationMs) {
        if (ScriptRuntimeSettings.isDebug()) {
            log().debug("[Sound|{}] utilPlay pausing {} ms (~{} ticks) for {}",
                    shortUuid(stockId), durationMs, Math.max(1, (int) Math.ceil(durationMs / 50.0D)), path);
        }
    }

    public static void continuationScheduled(UUID stockId, String functionName, int waitTicks) {
        log().debug("[Continuation|{}] {} resumes after {} tick(s)",
                shortUuid(stockId), functionName, waitTicks);
    }

    public static void continuationFailed(UUID stockId, String functionName, Throwable cause) {
        log().error("[Continuation|{}] {} failed during resume", shortUuid(stockId), functionName, cause);
    }

    public static void particleEmitted(
            UUID stockId,
            int type,
            double startX,
            double startY,
            double startZ,
            double offsetX,
            double offsetY,
            double offsetZ,
            double speed,
            double time,
            float concentration,
            String texture
    ) {
        if (ScriptRuntimeSettings.isDebug()) {
            log().debug("[Particle|{}] {} start=[{},{},{}] offset=[{},{},{}] speed={} time={}s concentration={} texture={}",
                    shortUuid(stockId),
                    type == 0 ? "smoke" : "steam",
                    startX, startY, startZ,
                    offsetX, offsetY, offsetZ,
                    speed, time, concentration,
                    texture == null ? "default" : texture);
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
        if (ScriptRuntimeSettings.isDebug()) {
            log().debug("[Bootstrap] Could not load JSON for {}", defId);
        }
    }

    public static void bootstrapFailed(String defId, Throwable cause) {
        log().warn("[Bootstrap] Failed to scan {}", defId, cause);
    }

    public static void engineCreatedVia(String name) {
        if (ScriptRuntimeSettings.isDebug()) {
            log().debug("[Engine] Created via ScriptEngineManager name '{}'", name);
        }
    }

    public static void engineCreated(String engineId) {
        log().info("[Engine] Using {} JavaScript engine", engineId);
    }

    public static void engineDirectUnavailable(String reason) {
        log().warn("[Engine] Direct Nashorn init failed: {}", reason);
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
