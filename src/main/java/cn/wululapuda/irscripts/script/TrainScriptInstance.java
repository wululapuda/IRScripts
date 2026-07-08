package cn.wululapuda.irscripts.script;

import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cn.wululapuda.irscripts.util.ScriptLog;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class TrainScriptInstance {
    private final UUID stockId;
    private final String defId;
    private final ScriptUnit[] units;

    public TrainScriptInstance(EntityRollingStock stock, List<StockScriptFile> scriptFiles) throws ScriptException {
        this.stockId = stock.getUUID();
        this.defId = stock.getDefinitionID();
        this.units = new ScriptUnit[scriptFiles.size()];

        for (int i = 0; i < scriptFiles.size(); i++) {
            StockScriptFile scriptFile = scriptFiles.get(i);
            String source = loadSource(scriptFile.path);
            this.units[i] = new ScriptUnit(stock, scriptFile, source);
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

    private final class ScriptUnit {
        private final StockScriptFile definition;
        private final Invocable invocable;
        private final Set<String> onceExecuted = new HashSet<>();

        private ScriptUnit(EntityRollingStock stock, StockScriptFile definition, String source) throws ScriptException {
            this.definition = definition;
            ScriptEngine engine = ScriptEngineProvider.create();
            if (engine == null) {
                throw new ScriptException("Nashorn JavaScript engine is not available");
            }

            TrainScriptBindings bindings = new TrainScriptBindings(stock);
            engine.put("stock", bindings);
            engine.put("print", (java.util.function.Consumer<Object>) message ->
                    ScriptLog.scriptPrint(stockId, definition.path, message));

            try {
                engine.eval(source);
            } catch (ScriptException ex) {
                ScriptException wrapped = new ScriptException("Failed to evaluate script " + definition.path + ": " + ex.getMessage());
                wrapped.initCause(ex);
                throw wrapped;
            }

            if (!(engine instanceof Invocable)) {
                throw new ScriptException("JavaScript engine is not invocable for " + definition.path);
            }
            this.invocable = (Invocable) engine;
        }

        private void tick() {
            for (StockScriptEntry entry : definition.getEntries()) {
                if (entry.mode == ScriptMode.LOOP) {
                    invoke(entry.functionName, ScriptMode.LOOP);
                } else if (entry.mode == ScriptMode.ONCE && onceExecuted.add(entry.functionName)) {
                    ScriptLog.scriptOnceExecuted(stockId, definition.path, entry.functionName);
                    invoke(entry.functionName, ScriptMode.ONCE);
                }
            }
        }

        private void invoke(String functionName, ScriptMode mode) {
            try {
                invocable.invokeFunction(functionName);
            } catch (ScriptException ex) {
                ScriptLog.scriptError(stockId, definition.path, functionName, mode, ex);
            } catch (NoSuchMethodException ex) {
                ScriptLog.scriptMissingFunction(definition.path, functionName);
            }
        }
    }
}
