package cn.wululapuda.irscripts.script;

import cam72cam.mod.world.World;

/**
 * Native hooks for track script stdlib ({@code time}, etc.) without a rolling stock.
 */
public final class TrackScriptNativeBridge {
    public static final double TICKS_PER_SECOND = 20.0D;
    public static final double SECONDS_PER_TICK = 0.05D;

    private final World world;

    public TrackScriptNativeBridge(World world) {
        this.world = world;
    }

    public double time() {
        return System.currentTimeMillis() / 1000.0D;
    }

    public double monotonic() {
        return System.nanoTime() / 1_000_000_000.0D;
    }

    public double perfCounter() {
        return monotonic();
    }

    public double processTime() {
        return System.nanoTime() / 1_000_000_000.0D;
    }

    public long worldTick() {
        return world != null ? world.getTicks() : 0L;
    }

    /** Track scripts have no stock entity; returns world tick count. */
    public int stockTick() {
        return world != null ? (int) world.getTicks() : 0;
    }

    public double ticksToSeconds(double ticks) {
        return ticks * SECONDS_PER_TICK;
    }

    public double secondsToTicks(double seconds) {
        if (seconds <= 0.0D) {
            return 0.0D;
        }
        return Math.ceil(seconds * TICKS_PER_SECOND);
    }
}
