package cn.wululapuda.irscripts.script;

import cn.wululapuda.irscripts.api.MathApi;
import cn.wululapuda.irscripts.api.TrackApi;
import cn.wululapuda.irscripts.math.CurveHandleRegistry;
import cn.wululapuda.irscripts.track.TrackScriptContext;
import cn.wululapuda.irscripts.util.ScriptLog;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;

import javax.script.ScriptException;
import java.io.IOException;

public final class TrackScriptRuntime {
    private final String scriptPath;
    private final Scriptable scope;
    private final boolean hasInit;
    private final boolean hasSwitch;

    private TrackScriptRuntime(String scriptPath, Scriptable scope, boolean hasInit, boolean hasSwitch) {
        this.scriptPath = scriptPath;
        this.scope = scope;
        this.hasInit = hasInit;
        this.hasSwitch = hasSwitch;
    }

    public static TrackScriptRuntime load(String scriptPath, TrackScriptContext context) throws ScriptException {
        String source;
        try {
            source = ScriptSourceLoader.getSource(scriptPath);
        } catch (IOException error) {
            ScriptException wrapped = new ScriptException("Failed to load track script " + scriptPath + ": " + error.getMessage());
            wrapped.initCause(error);
            throw wrapped;
        }

        Context rhinoContext = Context.enter();
        try {
            rhinoContext.setOptimizationLevel(-1);
            rhinoContext.setLanguageVersion(Context.VERSION_ES6);
            Scriptable scope = rhinoContext.initStandardObjects(null, true);
            CurveHandleRegistry curves = new CurveHandleRegistry();
            installStdlib(rhinoContext, scope, context.getWorld());
            ScriptableObject.putProperty(
                    scope,
                    "track",
                    Context.javaToJS(new TrackApi(context), scope)
            );
            ScriptableObject.putProperty(
                    scope,
                    "math",
                    Context.javaToJS(new MathApi(curves), scope)
            );
            ScriptableObject.putProperty(scope, "print", newPrintFunction(scriptPath, context));
            rhinoContext.evaluateString(scope, source, scriptPath, 1, null);

            boolean hasInit = scope.get("init", scope) instanceof Callable;
            boolean hasSwitch = scope.get("switch", scope) instanceof Callable;
            if (!hasInit) {
                ScriptLog.trackScriptMissingInit(scriptPath);
            }
            return new TrackScriptRuntime(scriptPath, scope, hasInit, hasSwitch);
        } catch (RhinoException error) {
            ScriptException wrapped = new ScriptException("Failed to evaluate track script " + scriptPath + ": " + error.getMessage());
            wrapped.initCause(error);
            throw wrapped;
        } finally {
            Context.exit();
        }
    }

    public boolean hasInit() {
        return hasInit;
    }

    public boolean hasSwitch() {
        return hasSwitch;
    }

    public void invokeInit(TrackScriptContext context) throws ScriptException {
        if (!hasInit) {
            return;
        }
        bindTrack(context);
        invoke("init");
    }

    public void invokeSwitch(TrackScriptContext context) throws ScriptException {
        if (!hasSwitch) {
            ScriptLog.trackScriptMissingSwitch(scriptPath);
            return;
        }
        bindTrack(context);
        invoke("switch");
    }

    private void bindTrack(TrackScriptContext context) {
        ScriptableObject.putProperty(scope, "track", Context.javaToJS(new TrackApi(context), scope));
    }

    private void invoke(String functionName) throws ScriptException {
        Object callable = scope.get(functionName, scope);
        if (!(callable instanceof Callable)) {
            ScriptLog.trackScriptMissingFunction(scriptPath, functionName);
            return;
        }

        Context rhinoContext = Context.enter();
        try {
            rhinoContext.setOptimizationLevel(-1);
            rhinoContext.setLanguageVersion(Context.VERSION_ES6);
            ((Callable) callable).call(rhinoContext, scope, scope, new Object[0]);
        } catch (RhinoException error) {
            ScriptException wrapped = new ScriptException(
                    "Track script " + scriptPath + "#" + functionName + ": " + error.getMessage()
            );
            wrapped.initCause(error);
            ScriptLog.trackScriptError(scriptPath, functionName, error);
            throw wrapped;
        } finally {
            Context.exit();
        }
    }

    private static void installStdlib(Context rhinoContext, Scriptable scope, cam72cam.mod.world.World world)
            throws ScriptException {
        ScriptableObject.putProperty(
                scope,
                "__native",
                Context.javaToJS(new TrackScriptNativeBridge(world), scope)
        );
        for (String path : new String[] {"irscripts:lib/time.js", "irscripts:lib/util.js", "irscripts:lib/random.js"}) {
            try {
                String source = ScriptSourceLoader.getSource(path);
                rhinoContext.evaluateString(scope, source, path, 1, null);
            } catch (IOException | RhinoException error) {
                ScriptLog.trackScriptStdlibWarn(path, error.getMessage());
            }
        }
    }

    private static BaseFunction newPrintFunction(String scriptPath, TrackScriptContext context) {
        return new BaseFunction() {
            @Override
            public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
                StringBuilder builder = new StringBuilder();
                for (int index = 0; index < args.length; index++) {
                    if (index > 0) {
                        builder.append(' ');
                    }
                    builder.append(Context.toString(args[index]));
                }
                int[] pos = context.getPlacementBlock();
                ScriptLog.trackScriptPrint(scriptPath, pos[0], pos[1], pos[2], builder.toString());
                return Undefined.instance;
            }
        };
    }
}
