package cn.wululapuda.irscripts.script;

/**
 * Callback surface from {@link ScriptContinuationScheduler} back into a script unit.
 */
public interface ScriptUnitHandle {
    void onContinuationScheduled(String functionName);

    void onContinuationFinished(String functionName);
}
