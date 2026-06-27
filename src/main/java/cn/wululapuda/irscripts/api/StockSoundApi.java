package cn.wululapuda.irscripts.api;

import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cam72cam.mod.entity.Entity;
import cam72cam.mod.resource.Identifier;
import cn.wululapuda.irscripts.IRScripts;
import cn.wululapuda.irscripts.net.ScriptSoundPacket;

public final class StockSoundApi {
    private static final int DEFAULT_DISTANCE = 16;

    private final EntityRollingStock stock;

    public StockSoundApi(EntityRollingStock stock) {
        this.stock = stock;
    }

    public void play(String path, double volume) {
        play(path, volume, 1.0D, false);
    }

    public void play(String path, double volume, double pitch) {
        play(path, volume, pitch, false);
    }

    public void play(String path, double volume, double pitch, boolean repeat) {
        if (!requireServer("play")) {
            return;
        }

        Identifier identifier;
        try {
            identifier = SoundPathUtil.resolve(stock, path);
        } catch (IllegalArgumentException ex) {
            IRScripts.logger.warn("[IRScripts] playSound failed: {}", ex.getMessage());
            return;
        }

        float clampedVolume = clampVolume(volume);
        float clampedPitch = (float) Math.max(0.1D, Math.min(2.0D, pitch));
        int distance = (int) Math.max(DEFAULT_DISTANCE, DEFAULT_DISTANCE * stock.gauge.scale());

        new ScriptSoundPacket(
                identifier.toString(),
                stock.getUUID(),
                stock.getPosition(),
                stock.getVelocity(),
                clampedVolume,
                clampedPitch,
                distance,
                stock.soundScale(),
                repeat
        ).sendToObserving((Entity) stock);

        IRScripts.logger.debug("[IRScripts] playSound {} volume={} on stock {}", identifier, clampedVolume, stock.getUUID());
    }

    private static float clampVolume(double volume) {
        return (float) Math.max(0.0D, Math.min(1.0D, volume));
    }

    private boolean requireServer(String action) {
        if (stock.getWorld().isServer) {
            return true;
        }
        IRScripts.logger.warn("[IRScripts] sound.{} ignored: sound API is server-side only", action);
        return false;
    }
}
