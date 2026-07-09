package cn.wululapuda.irscripts.script;

import cam72cam.immersiverailroading.entity.EntityRollingStock;
import org.mozilla.javascript.ContinuationPending;
import org.mozilla.javascript.Context;

/**
 * Per-thread context while a script function is running on the server thread.
 * Used by {@code utilPlay} to pause without blocking the world tick.
 */
public final class ScriptInvocationContext {
    private static final ThreadLocal<ScriptInvocationContext> CURRENT = new ThreadLocal<>();

    private final ScriptUnitHandle unit;
    private final EntityRollingStock stock;
    private final RhinoScriptRuntime runtime;
    private String activeFunction;

    private ScriptInvocationContext(ScriptUnitHandle unit, EntityRollingStock stock, RhinoScriptRuntime runtime) {
        this.unit = unit;
        this.stock = stock;
        this.runtime = runtime;
    }

    public static ScriptInvocationContext begin(ScriptUnitHandle unit, EntityRollingStock stock, RhinoScriptRuntime runtime) {
        ScriptInvocationContext context = new ScriptInvocationContext(unit, stock, runtime);
        CURRENT.set(context);
        return context;
    }

    public static void end() {
        CURRENT.remove();
    }

    public static ScriptInvocationContext get() {
        return CURRENT.get();
    }

    public void setActiveFunction(String functionName) {
        this.activeFunction = functionName;
    }

    public String getActiveFunction() {
        return activeFunction;
    }

    public ScriptUnitHandle getUnit() {
        return unit;
    }

    public EntityRollingStock getStock() {
        return stock;
    }

    public RhinoScriptRuntime getRuntime() {
        return runtime;
    }

    /**
     * Pauses the current script until {@code ticks} server ticks elapse, then runs {@code onWaitComplete}.
     * {@code onCancel} runs when the wait is aborted (e.g. stock removed) before the script resumes.
     * Does not return — throws {@link ContinuationPending} for Rhino to unwind the stack.
     */
    public void pauseForTicks(int ticks, Runnable onWaitComplete, Runnable onCancel) {
        if (ticks < 1) {
            ticks = 1;
        }
        Context rhinoContext = Context.getCurrentContext();
        if (rhinoContext == null) {
            throw new IllegalStateException("utilPlay pause requires an active Rhino context");
        }
        ContinuationPending pending = rhinoContext.captureContinuation();
        ScriptContinuationScheduler.schedule(
                stock,
                runtime,
                unit,
                activeFunction,
                pending,
                ticks,
                onWaitComplete,
                onCancel
        );
        throw pending;
    }
}
