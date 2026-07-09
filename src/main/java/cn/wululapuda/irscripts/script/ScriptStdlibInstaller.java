package cn.wululapuda.irscripts.script;

import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cn.wululapuda.irscripts.util.ScriptLog;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import javax.script.ScriptException;
import java.io.IOException;

/**
 * Installs bundled JavaScript standard libraries into a Rhino scope before user scripts run.
 */
public final class ScriptStdlibInstaller {
    private static final String[] LIBRARY_PATHS = {
            "irscripts:lib/time.js",
            "irscripts:lib/util.js",
            "irscripts:lib/random.js",
    };

    private ScriptStdlibInstaller() {
    }

    public static void install(Context rhinoContext, Scriptable scope, EntityRollingStock stock) throws ScriptException {
        ScriptableObject.putProperty(
                scope,
                "__native",
                Context.javaToJS(new ScriptNativeBridge(stock), scope)
        );

        for (String path : LIBRARY_PATHS) {
            evaluateLibrary(rhinoContext, scope, path);
        }
    }

    private static void evaluateLibrary(Context rhinoContext, Scriptable scope, String path) throws ScriptException {
        try {
            String source = ScriptSourceLoader.getSource(path);
            rhinoContext.evaluateString(scope, source, path, 1, null);
        } catch (IOException ex) {
            ScriptException wrapped = new ScriptException("Failed to load stdlib " + path + ": " + ex.getMessage());
            wrapped.initCause(ex);
            throw wrapped;
        } catch (RhinoException error) {
            ScriptLog.registryWarn("Stdlib {} failed: {}", path, error.getMessage());
            ScriptException wrapped = new ScriptException("Failed to evaluate stdlib " + path + ": " + error.getMessage());
            wrapped.initCause(error);
            throw wrapped;
        }
    }
}
