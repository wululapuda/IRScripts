package cn.wululapuda.irscripts.script;

public final class ScriptButtonEntry {
    public final String scriptPath;
    public final String functionName;

    public ScriptButtonEntry(String scriptPath, String functionName) {
        this.scriptPath = scriptPath;
        this.functionName = functionName;
    }

    public String label() {
        return functionName;
    }
}
