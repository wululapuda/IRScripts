package cn.wululapuda.irscripts.script;

import java.util.Locale;

public enum ScriptMode {
    LOOP,
    ONCE,
    BUTTON;

    public static ScriptMode fromString(String value) {
        if (value == null) {
            return null;
        }
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
