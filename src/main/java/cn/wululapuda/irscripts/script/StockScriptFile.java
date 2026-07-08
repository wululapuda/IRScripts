package cn.wululapuda.irscripts.script;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class StockScriptFile {
    public final String path;
    private final List<StockScriptEntry> entries;

    public StockScriptFile(String path, List<StockScriptEntry> entries) {
        this.path = path;
        this.entries = Collections.unmodifiableList(new ArrayList<>(entries));
    }

    public List<StockScriptEntry> getEntries() {
        return entries;
    }
}
