package cn.wululapuda.irscripts.api;

import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cam72cam.mod.entity.Entity;
import cam72cam.mod.resource.Identifier;
import cn.wululapuda.irscripts.net.ScriptSoundPacket;
import cn.wululapuda.irscripts.script.ScriptInvocationContext;
import cn.wululapuda.irscripts.util.ScriptLog;

import java.util.UUID;

/**
 * Server-side sound playback for rolling-stock scripts.
 * <p>
 * {@code play(...)} is non-blocking. {@code utilPlay(...)} pauses the current script until the
 * estimated clip duration elapses without blocking the server thread. {@code stopPlay(...)} stops active script sounds on observing clients.
 */
public final class StockSoundApi {
    static final int DEFAULT_MIN_DISTANCE = 16;
    static final int ABSOLUTE_MIN_DISTANCE = 1;
    static final int ABSOLUTE_MAX_DISTANCE = 512;

    private final EntityRollingStock stock;
    private final ScriptSoundTracker tracker;

    public StockSoundApi(EntityRollingStock stock, ScriptSoundTracker tracker) {
        this.stock = stock;
        this.tracker = tracker;
    }

    public void play(String path, double volume) {
        play(path, volume, 1.0D, false);
    }

    public void play(String path, double volume, double pitch) {
        play(path, volume, pitch, false);
    }

    public void play(String path, double volume, double pitch, boolean repeat) {
        play(path, volume, pitch, repeat, 0.0D);
    }

    public void play(String path, double volume, double pitch, boolean repeat, double maxDistance) {
        if (!requireServer("play")) {
            return;
        }

        SoundPlayRequest request = buildRequest(path, volume, pitch, repeat, maxDistance);
        if (request == null) {
            return;
        }

        dispatch(request);
    }

    public void utilPlay(String path, double volume) {
        utilPlay(path, volume, 1.0D, false);
    }

    public void utilPlay(String path, double volume, double pitch) {
        utilPlay(path, volume, pitch, false);
    }

    public void utilPlay(String path, double volume, double pitch, boolean repeat) {
        utilPlay(path, volume, pitch, repeat, 0.0D);
    }

    /**
     * Plays a sound and blocks the current script until the clip is estimated to have finished.
     * {@code repeat} waits for one loop iteration, then stops the sound automatically.
     */
    public void utilPlay(String path, double volume, double pitch, boolean repeat, double maxDistance) {
        if (!requireServer("utilPlay")) {
            return;
        }

        SoundPlayRequest request = buildRequest(path, volume, pitch, repeat, maxDistance);
        if (request == null) {
            return;
        }

        UUID soundId = dispatch(request);
        waitForPlayback(request, soundId);
    }

    /** Stops all script sounds currently tracked for this stock instance. */
    public void stopPlay() {
        if (!requireServer("stopPlay")) {
            return;
        }
        tracker.stopAll((Entity) stock);
        ScriptLog.soundStopped(stock.getUUID(), "all", null);
    }

    /** Stops script sounds matching the given resource path. */
    public void stopPlay(String path) {
        if (!requireServer("stopPlay")) {
            return;
        }

        Identifier identifier;
        try {
            identifier = SoundPathUtil.resolve(stock, path);
        } catch (IllegalArgumentException ex) {
            ScriptLog.soundFailed("resolve path for stopPlay " + path, ex);
            return;
        }

        tracker.stopByPath(identifier.toString(), (Entity) stock);
        ScriptLog.soundStopped(stock.getUUID(), "path", identifier.toString());
    }

    SoundPlayRequest buildRequest(String path, double volume, double pitch, boolean repeat, double maxDistance) {
        Identifier identifier;
        try {
            identifier = SoundPathUtil.resolve(stock, path);
        } catch (IllegalArgumentException ex) {
            ScriptLog.soundFailed("resolve path " + path, ex);
            return null;
        }

        return new SoundPlayRequest(
                identifier.toString(),
                clampVolume(volume),
                clampPitch(pitch),
                repeat,
                resolveMaxDistance(maxDistance)
        );
    }

    UUID dispatch(SoundPlayRequest request) {
        UUID soundId = tracker.register(request);
        new ScriptSoundPacket(
                request.getPath(),
                stock.getUUID(),
                soundId,
                stock.getPosition(),
                stock.getVelocity(),
                request.getVolume(),
                request.getPitch(),
                request.getMaxDistance(),
                stock.soundScale(),
                request.isRepeat()
        ).sendToObserving((Entity) stock);

        ScriptLog.soundPlayed(
                stock.getUUID(),
                request.getPath(),
                request.getVolume(),
                request.getPitch(),
                request.isRepeat(),
                request.getMaxDistance()
        );
        return soundId;
    }

    private void waitForPlayback(SoundPlayRequest request, UUID soundId) {
        long durationMs = OggDurationUtil.getDurationMs(new Identifier(request.getPath()));
        durationMs = Math.max(1L, (long) (durationMs / request.getPitch()));
        int waitTicks = Math.max(1, (int) Math.ceil(durationMs / 50.0D));

        ScriptLog.utilPlayWait(stock.getUUID(), request.getPath(), durationMs);

        ScriptInvocationContext context = ScriptInvocationContext.get();
        if (context == null) {
            ScriptLog.apiWarn("sound", "utilPlay", "no script context; playing without wait");
            return;
        }

        context.pauseForTicks(waitTicks, () -> {
            if (request.isRepeat()) {
                tracker.stopSound(soundId, (Entity) stock);
            } else {
                tracker.unregister(soundId);
            }
        }, () -> tracker.stopSound(soundId, (Entity) stock));
    }

    int resolveMaxDistance(double maxDistance) {
        if (maxDistance <= 0.0D || Double.isNaN(maxDistance) || Double.isInfinite(maxDistance)) {
            return (int) Math.max(DEFAULT_MIN_DISTANCE, DEFAULT_MIN_DISTANCE * stock.gauge.scale());
        }
        return (int) Math.max(ABSOLUTE_MIN_DISTANCE, Math.min(ABSOLUTE_MAX_DISTANCE, Math.round(maxDistance)));
    }

    static float clampVolume(double volume) {
        return (float) Math.max(0.0D, Math.min(1.0D, volume));
    }

    static float clampPitch(double pitch) {
        return (float) Math.max(0.1D, Math.min(2.0D, pitch));
    }

    private boolean requireServer(String action) {
        if (stock.getWorld().isServer) {
            return true;
        }
        ScriptLog.apiIgnored("sound", action, "server-side only");
        return false;
    }
}
