package cn.wululapuda.irscripts.script;

import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cn.wululapuda.irscripts.api.MathApi;
import cn.wululapuda.irscripts.api.ModelApi;
import cn.wululapuda.irscripts.api.TrackApi;
import cn.wululapuda.irscripts.api.ScriptSoundTracker;
import cn.wululapuda.irscripts.api.ScriptModelTracker;
import cn.wululapuda.irscripts.math.CurveHandleRegistry;
import cn.wululapuda.irscripts.model.ModelHandleRegistry;
import cn.wululapuda.irscripts.util.ScriptLog;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContinuationPending;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;

import javax.script.ScriptException;
import java.util.UUID;

/**
 * Rhino runtime with continuation support for non-blocking {@code utilPlay}.
 */
public final class RhinoScriptRuntime {
    private final UUID stockId;
    private final String scriptPath;
    private final Scriptable scope;
    private final ScriptUnitHandle unit;
    private final EntityRollingStock stock;

    public RhinoScriptRuntime(
            EntityRollingStock stock,
            ScriptSoundTracker soundTracker,
            ScriptModelTracker modelTracker,
            ModelHandleRegistry modelRegistry,
            CurveHandleRegistry curveRegistry,
            ScriptUnitHandle unit,
            String source,
            String scriptPath
    ) throws ScriptException {
        this.stock = stock;
        this.stockId = stock.getUUID();
        this.scriptPath = scriptPath;
        this.unit = unit;

        Context rhinoContext = Context.enter();
        try {
            configureContext(rhinoContext);
            scope = rhinoContext.initStandardObjects(null, true);
            TrainScriptBindings bindings = new TrainScriptBindings(stock, soundTracker);
            ScriptStdlibInstaller.install(rhinoContext, scope, stock);
            ScriptableObject.putProperty(scope, "stock", Context.javaToJS(bindings, scope));
            ScriptableObject.putProperty(
                    scope,
                    "model",
                    Context.javaToJS(new ModelApi(stock, modelRegistry, curveRegistry, modelTracker), scope)
            );
            ScriptableObject.putProperty(
                    scope,
                    "math",
                    Context.javaToJS(new MathApi(curveRegistry), scope)
            );
            ScriptableObject.putProperty(
                    scope,
                    "track",
                    Context.javaToJS(new TrackApi(stock), scope)
            );
            ScriptableObject.putProperty(scope, "print", newPrintFunction(stockId, scriptPath));
            rhinoContext.evaluateString(scope, source, scriptPath, 1, null);
        } catch (RhinoException error) {
            ScriptException wrapped = new ScriptException("Failed to evaluate script " + scriptPath + ": " + error.getMessage());
            wrapped.initCause(error);
            throw wrapped;
        } finally {
            Context.exit();
        }
    }

    public void invoke(String functionName) throws ScriptException, ContinuationPending {
        Object callable = scope.get(functionName, scope);
        if (!(callable instanceof Callable)) {
            ScriptLog.scriptMissingFunction(scriptPath, functionName);
            return;
        }

        Context rhinoContext = Context.enter();
        ScriptInvocationContext context = ScriptInvocationContext.begin(unit, stock, this);
        context.setActiveFunction(functionName);
        try {
            configureContext(rhinoContext);
            rhinoContext.callFunctionWithContinuations((Callable) callable, scope, new Object[0]);
        } catch (ContinuationPending pending) {
            throw pending;
        } catch (RhinoException error) {
            ScriptException wrapped = new ScriptException(error);
            throw wrapped;
        } finally {
            ScriptInvocationContext.end();
            Context.exit();
        }
    }

    public void resume(ContinuationPending pending, ScriptUnitHandle unit, String functionName)
            throws ScriptException, ContinuationPending {
        Context rhinoContext = Context.enter();
        ScriptInvocationContext context = ScriptInvocationContext.begin(unit, stock, this);
        context.setActiveFunction(functionName);
        try {
            configureContext(rhinoContext);
            rhinoContext.resumeContinuation(pending.getContinuation(), scope, new Object[0]);
        } catch (ContinuationPending next) {
            throw next;
        } catch (RhinoException error) {
            ScriptException wrapped = new ScriptException(error);
            throw wrapped;
        } finally {
            ScriptInvocationContext.end();
            Context.exit();
        }
    }

    private BaseFunction newPrintFunction(UUID stockId, String scriptPath) {
        return new BaseFunction() {
            @Override
            public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
                if (args.length == 0) {
                    ScriptLog.scriptPrint(stockId, scriptPath, "");
                    return Undefined.instance;
                }
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < args.length; i++) {
                    if (i > 0) {
                        builder.append(' ');
                    }
                    builder.append(Context.toString(args[i]));
                }
                ScriptLog.scriptPrint(stockId, scriptPath, builder.toString());
                return Undefined.instance;
            }
        };
    }

    private static void configureContext(Context rhinoContext) {
        rhinoContext.setOptimizationLevel(-1);
        rhinoContext.setLanguageVersion(Context.VERSION_ES6);
    }
}
