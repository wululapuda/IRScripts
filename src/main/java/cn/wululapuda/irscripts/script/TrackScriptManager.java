package cn.wululapuda.irscripts.script;

import cam72cam.immersiverailroading.library.SwitchState;
import cam72cam.immersiverailroading.library.TrackItems;
import cam72cam.immersiverailroading.tile.TileRail;
import cam72cam.immersiverailroading.tile.TileRailBase;
import cam72cam.immersiverailroading.util.RailInfo;
import cn.wululapuda.irscripts.track.TrackScriptContext;
import cn.wululapuda.irscripts.util.ScriptLog;
import cam72cam.mod.math.Vec3i;

import javax.script.ScriptException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class TrackScriptManager {
    private static final Map<String, TrackScriptRuntime> RUNTIMES = new ConcurrentHashMap<>();
    private static final Map<Vec3i, String> LAST_STATE = new ConcurrentHashMap<>();
    private static final Map<Vec3i, SwitchState> LAST_SWITCH = new ConcurrentHashMap<>();

    private TrackScriptManager() {
    }

    public static void clearAll() {
        RUNTIMES.clear();
        LAST_STATE.clear();
        LAST_SWITCH.clear();
    }

    public static void onTrackStateUpdated(TileRailBase tile) {
        if (tile == null || tile.getWorld() == null || !tile.getWorld().isServer) {
            return;
        }

        TileRail parent = TrackScriptContext.resolveParentRail(tile);
        if (parent == null || parent.info == null) {
            return;
        }

        String scriptPath = TrackScriptRegistry.getScriptPath(
                parent.info.settings.track,
                parent.info.settings.type
        );
        if (scriptPath == null) {
            return;
        }

        String stateKey = buildStateKey(parent);
        Vec3i pos = parent.getPos();
        String previous = LAST_STATE.get(pos);
        if (stateKey.equals(previous)) {
            return;
        }
        LAST_STATE.put(pos, stateKey);

        TrackScriptContext context = TrackScriptContext.from(parent);
        try {
            TrackScriptRuntime runtime = runtimeFor(scriptPath, context);
            if (runtime == null || !runtime.hasInit()) {
                return;
            }
            ScriptLog.trackScriptInvokeInit(scriptPath, pos.x, pos.y, pos.z);
            runtime.invokeInit(context);
        } catch (ScriptException error) {
            ScriptLog.trackScriptFailed(scriptPath, "init", error);
        }
    }

    public static void onSwitchChanged(TileRail rail) {
        if (rail == null || rail.getWorld() == null || !rail.getWorld().isServer) {
            return;
        }
        if (rail.info == null || rail.info.settings.type != TrackItems.SWITCH) {
            return;
        }

        String scriptPath = TrackScriptRegistry.getScriptPath(
                rail.info.settings.track,
                TrackItems.SWITCH
        );
        if (scriptPath == null) {
            return;
        }

        SwitchState current = rail.info.switchState;
        Vec3i pos = rail.getPos();
        SwitchState previous = LAST_SWITCH.put(pos, current);
        if (previous != null && previous == current) {
            return;
        }

        TrackScriptContext context = TrackScriptContext.from(rail);
        try {
            TrackScriptRuntime runtime = runtimeFor(scriptPath, context);
            if (runtime == null) {
                return;
            }
            if (!runtime.hasSwitch()) {
                ScriptLog.trackScriptMissingSwitch(scriptPath);
                return;
            }
            ScriptLog.trackScriptInvokeSwitch(scriptPath, pos.x, pos.y, pos.z, current.name());
            runtime.invokeSwitch(context);
        } catch (ScriptException error) {
            ScriptLog.trackScriptFailed(scriptPath, "switch", error);
        }
    }

    public static void onTrackRemoved(TileRailBase tile) {
        if (tile == null) {
            return;
        }
        Vec3i pos = tile.getPos();
        LAST_STATE.remove(pos);
        LAST_SWITCH.remove(pos);
        TileRail parent = TrackScriptContext.resolveParentRail(tile);
        if (parent != null) {
            LAST_STATE.remove(parent.getPos());
            LAST_SWITCH.remove(parent.getPos());
        }
    }

    private static TrackScriptRuntime runtimeFor(String scriptPath, TrackScriptContext context) throws ScriptException {
        TrackScriptRuntime cached = RUNTIMES.get(scriptPath);
        if (cached != null) {
            return cached.hasInit() ? cached : null;
        }

        TrackScriptRuntime loaded = TrackScriptRuntime.load(scriptPath, context);
        RUNTIMES.put(scriptPath, loaded);
        return loaded.hasInit() ? loaded : null;
    }

    private static String buildStateKey(TileRail rail) {
        RailInfo info = rail.info;
        Vec3i pos = rail.getPos();
        return info.settings.type.name()
                + "|" + info.settings.track
                + "|" + info.placementInfo.yaw
                + "|" + info.settings.direction.name()
                + "|" + info.switchState.name()
                + "|" + info.switchForced.name()
                + "|" + pos.x + "," + pos.y + "," + pos.z;
    }
}
