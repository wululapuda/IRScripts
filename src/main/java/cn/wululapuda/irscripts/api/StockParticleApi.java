package cn.wululapuda.irscripts.api;

import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cam72cam.mod.entity.Entity;
import cn.wululapuda.irscripts.config.ScriptRuntimeSettings;
import cn.wululapuda.irscripts.net.ScriptParticlePacket;
import cn.wululapuda.irscripts.util.ScriptLog;

public final class StockParticleApi {
    private final EntityRollingStock stock;

    public StockParticleApi(EntityRollingStock stock) {
        this.stock = stock;
    }

    /**
     * Diesel-style smoke (dark, concentration-controlled).
     *
     * @param start         model-space origin {@code [x, y, z]}
     * @param offset        model-space offset {@code [x, y, z]} added to {@code start}
     * @param speed         particle motion speed
     * @param time          effect duration in seconds
     * @param concentration smoke density {@code 0.0~1.0}, like IR diesel exhaust
     */
    public void smoke(Object start, Object offset, double speed, double time, double concentration) {
        smoke(start, offset, speed, time, concentration, null);
    }

    public void smoke(
            Object start,
            Object offset,
            double speed,
            double time,
            double concentration,
            String texture
    ) {
        emit(ScriptParticlePacket.TYPE_SMOKE, start, offset, speed, time, concentration, texture);
    }

    /**
     * Light steam puff (IR steam-locomotive style).
     *
     * @param start  model-space origin {@code [x, y, z]}
     * @param offset model-space offset {@code [x, y, z]} added to {@code start}
     * @param speed  particle motion speed
     * @param time   effect duration in seconds
     */
    public void steam(Object start, Object offset, double speed, double time) {
        steam(start, offset, speed, time, null);
    }

    public void steam(Object start, Object offset, double speed, double time, String texture) {
        emit(ScriptParticlePacket.TYPE_STEAM, start, offset, speed, time, 0.0D, texture);
    }

    private void emit(
            int type,
            Object start,
            Object offset,
            double speed,
            double time,
            double concentration,
            String texture
    ) {
        String action = type == ScriptParticlePacket.TYPE_SMOKE ? "smoke" : "steam";
        if (!requireServer(action)) {
            return;
        }
        if (time <= 0.0D) {
            ScriptLog.apiWarn("particle", action, "time must be > 0");
            return;
        }
        if (speed < 0.0D) {
            ScriptLog.apiWarn("particle", action, "speed must be >= 0");
            return;
        }

        double[] startVec;
        double[] offsetVec;
        try {
            startVec = ParticleVecUtil.parseVec3(start, "start");
            offsetVec = ParticleVecUtil.parseVec3(offset, "offset");
        } catch (IllegalArgumentException error) {
            ScriptLog.apiWarn("particle", action, error.getMessage());
            return;
        }

        float clampedConcentration = (float) Math.max(0.0D, Math.min(1.0D, concentration));
        String texturePath = emptyToNull(texture);

        new ScriptParticlePacket(
                stock.getUUID(),
                (float) startVec[0],
                (float) startVec[1],
                (float) startVec[2],
                (float) offsetVec[0],
                (float) offsetVec[1],
                (float) offsetVec[2],
                (float) speed,
                (float) time,
                clampedConcentration,
                (byte) type,
                texturePath
        ).sendToObserving((Entity) stock);

        if (ScriptRuntimeSettings.isDebug()) {
            ScriptLog.particleEmitted(
                    stock.getUUID(),
                    type,
                    startVec[0], startVec[1], startVec[2],
                    offsetVec[0], offsetVec[1], offsetVec[2],
                    speed,
                    time,
                    clampedConcentration,
                    texturePath
            );
        }
    }

    private static String emptyToNull(String texture) {
        if (texture == null || texture.trim().isEmpty()) {
            return null;
        }
        return texture.trim();
    }

    private boolean requireServer(String action) {
        if (stock.getWorld().isServer) {
            return true;
        }
        ScriptLog.apiIgnored("particle", action, "server-side only");
        return false;
    }
}
