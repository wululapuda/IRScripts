package cn.wululapuda.irscripts.script;

import cn.wululapuda.irscripts.util.ScriptLog;
import org.mozilla.javascript.engine.RhinoScriptEngineFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * Creates a JavaScript engine for rolling-stock scripts.
 * Minecraft's bundled JRE keeps Nashorn in {@code lib/ext}, which Forge's classloader cannot see,
 * so we ship Mozilla Rhino inside the mod jar.
 */
public final class ScriptEngineProvider {
    private ScriptEngineProvider() {
    }

    public static ScriptEngine create() {
        try {
            ScriptEngine engine = new RhinoScriptEngineFactory().getScriptEngine();
            ScriptLog.engineCreated("rhino");
            return engine;
        } catch (Throwable rhinoFailure) {
            ScriptLog.engineDirectUnavailable("Rhino: " + rhinoFailure.getMessage());
        }

        try {
            Class<?> factoryClass = Class.forName("jdk.nashorn.api.scripting.NashornScriptEngineFactory");
            Object factory = factoryClass.getDeclaredConstructor().newInstance();
            ScriptEngine engine = (ScriptEngine) factoryClass.getMethod("getScriptEngine").invoke(factory);
            ScriptLog.engineCreated("nashorn");
            return engine;
        } catch (Throwable nashornFailure) {
            ScriptLog.engineDirectUnavailable("Nashorn: " + nashornFailure.getMessage());
        }

        ScriptEngineManager manager = new ScriptEngineManager();
        for (String name : new String[]{"rhino", "javascript", "js", "nashorn"}) {
            ScriptEngine engine = manager.getEngineByName(name);
            if (engine != null) {
                ScriptLog.engineCreatedVia(name);
                return engine;
            }
        }

        return null;
    }
}
