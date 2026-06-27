package cn.wululapuda.irscripts.script;

public final class StockScriptEntry {
    public final String functionName;
    public final ScriptMode mode;

    public StockScriptEntry(String functionName, ScriptMode mode) {
        this.functionName = functionName;
        this.mode = mode;
    }
}
