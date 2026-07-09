package cn.wululapuda.irscripts.api;

import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cn.wululapuda.irscripts.util.ScriptLog;

/**
 * Control group (CG) read/write for IR rolling-stock animations and widgets.
 * <p>
 * Names match {@code control_group} in the vehicle JSON / model (e.g. {@code leftdoor}).
 */
public final class CgGroupControlApi {
    private final EntityRollingStock stock;

    public CgGroupControlApi(EntityRollingStock stock) {
        this.stock = stock;
    }

    /**
     * @param name control group name
     * @return current value in {@code 0.0~1.0}; {@code 0.0} if the name is invalid
     */
    public double get(String name) {
        if (!isValidName(name)) {
            ScriptLog.apiWarn("cg_group", "get", "control group name is empty");
            return 0.0D;
        }
        return stock.getControlPosition(name);
    }

    /**
     * @param name  control group name
     * @param value clamped to {@code 0.0~1.0} (IR allowed range)
     */
    public void set(String name, double value) {
        if (!requireServer("set")) {
            return;
        }
        if (!isValidName(name)) {
            ScriptLog.apiWarn("cg_group", "set", "control group name is empty");
            return;
        }
        stock.setControlPosition(name, clamp01(value));
    }

    private static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty();
    }

    private static float clamp01(double value) {
        return (float) Math.max(0.0D, Math.min(1.0D, value));
    }

    private boolean requireServer(String action) {
        if (stock.getWorld().isServer) {
            return true;
        }
        ScriptLog.apiIgnored("cg_group", action, "server-side only");
        return false;
    }
}
