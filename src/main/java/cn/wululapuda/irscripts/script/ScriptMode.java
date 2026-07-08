package cn.wululapuda.irscripts.script;

import java.util.Locale;

public enum ScriptMode {
    /** Runs the function every game tick (legacy JSON value {@code LOOP} maps here). */
    LOOP_TICK,
    /** Runs the function again only after the previous invocation has fully returned. */
    LOOP_SCRIPTS,
    ONCE,
    BUTTON;

    public boolean isLoop() {
        return this == LOOP_TICK || this == LOOP_SCRIPTS;
    }

    public static ScriptMode fromString(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim().toUpperCase(Locale.ROOT).replace('-', '_');
        if ("LOOP".equals(normalized)) {
            return LOOP_TICK;
        }
        try {
            return valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
