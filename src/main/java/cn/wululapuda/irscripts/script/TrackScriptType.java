package cn.wululapuda.irscripts.script;

import cam72cam.immersiverailroading.library.TrackItems;

import java.util.Locale;

public enum TrackScriptType {
    STRAIGHT("straight", TrackItems.STRAIGHT),
    SLOPE("slope", TrackItems.SLOPE),
    CURVE("curve", TrackItems.TURN),
    SWITCH("switch", TrackItems.SWITCH),
    CUSTOM_CURVE("customcurve", TrackItems.CUSTOM),
    TURNTABLE("turntable", TrackItems.TURNTABLE);

    private final String jsonKey;
    private final TrackItems trackItem;

    TrackScriptType(String jsonKey, TrackItems trackItem) {
        this.jsonKey = jsonKey;
        this.trackItem = trackItem;
    }

    public String getJsonKey() {
        return jsonKey;
    }

    public TrackItems getTrackItem() {
        return trackItem;
    }

    public static TrackScriptType fromJsonKey(String key) {
        if (key == null) {
            return null;
        }
        String normalized = key.trim().toLowerCase(Locale.ROOT);
        for (TrackScriptType type : values()) {
            if (type.jsonKey.equals(normalized)) {
                return type;
            }
        }
        return null;
    }

    public static TrackScriptType fromTrackItem(TrackItems item) {
        if (item == null) {
            return null;
        }
        for (TrackScriptType type : values()) {
            if (type.trackItem == item) {
                return type;
            }
        }
        return null;
    }
}
