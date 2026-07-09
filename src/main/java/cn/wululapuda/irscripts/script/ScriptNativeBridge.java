package cn.wululapuda.irscripts.script;

import cam72cam.immersiverailroading.entity.EntityRollingStock;

/**
 * Native hooks for bundled JS standard libraries ({@code time}, etc.).
 */
public final class ScriptNativeBridge {
    public static final double TICKS_PER_SECOND = 20.0D;
    public static final double SECONDS_PER_TICK = 0.05D;

    private final EntityRollingStock stock;

    public ScriptNativeBridge(EntityRollingStock stock) {
        this.stock = stock;
    }

    /** Wall-clock seconds (fractional). */
    public double time() {
        return System.currentTimeMillis() / 1000.0D;
    }

    /** Monotonic seconds for interval measurement. */
    public double monotonic() {
        return System.nanoTime() / 1_000_000_000.0D;
    }

    /** Alias of {@link #monotonic()}. */
    public double perfCounter() {
        return monotonic();
    }

    /** JVM thread CPU seconds (approximate). */
    public double processTime() {
        return System.nanoTime() / 1_000_000_000.0D;
    }

    /** Server world age in ticks ({@code World#getTicks()}). */
    public long worldTick() {
        if (stock.getWorld() != null) {
            return stock.getWorld().getTicks();
        }
        return 0L;
    }

    /** This stock entity age in ticks. */
    public int stockTick() {
        return stock.getTickCount();
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

    /**
     * Pauses the current script for approximately {@code seconds} without blocking the server thread.
     */
    public void sleep(double seconds) {
        ScriptInvocationContext context = ScriptInvocationContext.get();
        if (context == null) {
            throw new IllegalStateException("time.sleep requires an active script invocation");
        }
        int ticks = (int) Math.max(1L, Math.round(secondsToTicks(seconds)));
        context.pauseForTicks(ticks, null, null);
    }
}
