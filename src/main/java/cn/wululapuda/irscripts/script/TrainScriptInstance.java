package cn.wululapuda.irscripts.script;

import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cn.wululapuda.irscripts.api.ScriptSoundTracker;
import cn.wululapuda.irscripts.util.ScriptLog;
import org.mozilla.javascript.ContinuationPending;

import javax.script.ScriptException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class TrainScriptInstance {
    private final UUID stockId;
    private final String defId;
    private final EntityRollingStock stock;
    private final ScriptSoundTracker soundTracker;
    private final ScriptUnit[] units;

    public TrainScriptInstance(EntityRollingStock stock, List<StockScriptFile> scriptFiles) throws ScriptException {
        this.stock = stock;
        this.stockId = stock.getUUID();
        this.defId = stock.getDefinitionID();
        this.soundTracker = new ScriptSoundTracker(stockId);
        this.units = new ScriptUnit[scriptFiles.size()];

        for (int i = 0; i < scriptFiles.size(); i++) {
            StockScriptFile scriptFile = scriptFiles.get(i);
            String source = loadSource(scriptFile.path);
            this.units[i] = new ScriptUnit(stock, soundTracker, scriptFile, source);
        }

        ScriptLog.runtimeCreated(stockId, defId, scriptFiles.size());
    }

    public UUID getStockId() {
        return stockId;
    }

    public void tick() {
        for (ScriptUnit unit : units) {
            unit.tick();
        }
    }

    public void dispose() {
        ScriptContinuationScheduler.clearForStock(stockId);
        cam72cam.mod.world.World world = stock != null ? stock.getWorld() : null;
        cam72cam.mod.math.Vec3d position = stock != null ? stock.getPosition() : null;
        soundTracker.stopAllForDisposal((cam72cam.mod.entity.Entity) stock, world, position);
        ScriptLog.runtimeDisposed(stockId, defId);
    }

    public void invokeButton(String scriptPath, String functionName) {
        ScriptLog.buttonInvoke(stockId, scriptPath, functionName);
        for (ScriptUnit unit : units) {
            if (unit.definition.path.equals(scriptPath)) {
                unit.invoke(functionName, ScriptMode.BUTTON);
                return;
            }
        }
        ScriptLog.buttonNotFound(stockId, scriptPath);
    }

    private static String loadSource(String path) throws ScriptException {
        try {
            return ScriptSourceLoader.getSource(path);
        } catch (Exception ex) {
            ScriptException wrapped = new ScriptException("Failed to load script " + path + ": " + ex.getMessage());
            wrapped.initCause(ex);
            throw wrapped;
        }
    }

    private final class ScriptUnit implements ScriptUnitHandle {
        private final StockScriptFile definition;
        private final RhinoScriptRuntime runtime;
        private final Set<String> onceExecuted = new HashSet<>();
        private final Set<String> disabledLoopFunctions = new HashSet<>();
        private final Map<String, Boolean> loopScriptsRunning = new HashMap<>();
        private final Set<String> continuationFunctions = new HashSet<>();

        private ScriptUnit(EntityRollingStock stock, ScriptSoundTracker soundTracker,
                           StockScriptFile definition, String source) throws ScriptException {
            this.definition = definition;
            this.runtime = new RhinoScriptRuntime(stock, soundTracker, this, source, definition.path);
        }

        private void tick() {
            for (StockScriptEntry entry : definition.getEntries()) {
                if (entry.mode == ScriptMode.LOOP_TICK) {
                    invokeLoop(entry);
                } else if (entry.mode == ScriptMode.LOOP_SCRIPTS) {
                    if (Boolean.TRUE.equals(loopScriptsRunning.get(entry.functionName))) {
                        continue;
                    }
                    if (continuationFunctions.contains(entry.functionName)) {
                        continue;
                    }
                    invokeLoop(entry);
                } else if (entry.mode == ScriptMode.ONCE && onceExecuted.add(entry.functionName)) {
                    ScriptLog.scriptOnceExecuted(stockId, definition.path, entry.functionName);
                    invoke(entry.functionName, ScriptMode.ONCE);
                }
            }
        }

        private void invokeLoop(StockScriptEntry entry) {
            if (disabledLoopFunctions.contains(entry.functionName)) {
                return;
            }

            if (entry.mode == ScriptMode.LOOP_SCRIPTS) {
                loopScriptsRunning.put(entry.functionName, true);
            }

            try {
                runtime.invoke(entry.functionName);
            } catch (ContinuationPending pending) {
                // Wait scheduled; function will resume on a later server tick.
            } catch (ScriptException ex) {
                disableLoop(entry, ex);
            } finally {
                releaseLoopScriptsRunning(entry);
            }
        }

        private void releaseLoopScriptsRunning(StockScriptEntry entry) {
            if (entry.mode != ScriptMode.LOOP_SCRIPTS) {
                return;
            }
            if (!continuationFunctions.contains(entry.functionName)
                    && !ScriptContinuationScheduler.hasPending(runtime)) {
                loopScriptsRunning.put(entry.functionName, false);
            }
        }

        private void disableLoop(StockScriptEntry entry, Throwable cause) {
            disabledLoopFunctions.add(entry.functionName);
            loopScriptsRunning.put(entry.functionName, false);
            continuationFunctions.remove(entry.functionName);
            ScriptLog.scriptError(stockId, definition.path, entry.functionName, entry.mode, cause);
            ScriptLog.scriptLoopDisabled(stockId, definition.path, entry.functionName, entry.mode);
        }

        private void invoke(String functionName, ScriptMode mode) {
            try {
                runtime.invoke(functionName);
            } catch (ContinuationPending pending) {
                // BUTTON/ONCE with utilPlay will resume asynchronously.
            } catch (ScriptException ex) {
                ScriptLog.scriptError(stockId, definition.path, functionName, mode, ex);
            }
        }

        @Override
        public void onContinuationScheduled(String functionName) {
            continuationFunctions.add(functionName);
        }

        @Override
        public void onContinuationFinished(String functionName) {
            continuationFunctions.remove(functionName);
            loopScriptsRunning.put(functionName, false);
        }
    }
}
