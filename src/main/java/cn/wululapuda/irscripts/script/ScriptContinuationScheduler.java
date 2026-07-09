package cn.wululapuda.irscripts.script;

import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cn.wululapuda.irscripts.config.ScriptRuntimeSettings;
import cn.wululapuda.irscripts.util.ScriptLog;
import org.mozilla.javascript.ContinuationPending;

import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Resumes Rhino continuations on later server ticks instead of blocking with {@link Thread#sleep}.
 */
public final class ScriptContinuationScheduler {
    private static final List<PendingResume> PENDING = new ArrayList<>();

    private ScriptContinuationScheduler() {
    }

    public static void schedule(
            EntityRollingStock stock,
            RhinoScriptRuntime runtime,
            ScriptUnitHandle unit,
            String functionName,
            ContinuationPending pending,
            int waitTicks,
            Runnable onWaitComplete,
            Runnable onCancel
    ) {
        synchronized (PENDING) {
            PENDING.add(new PendingResume(
                    stock,
                    runtime,
                    unit,
                    functionName,
                    pending,
                    waitTicks,
                    onWaitComplete,
                    onCancel
            ));
        }
        if (unit != null) {
            unit.onContinuationScheduled(functionName);
        }
        if (ScriptRuntimeSettings.isDebug()) {
            ScriptLog.continuationScheduled(stock.getUUID(), functionName, waitTicks);
        }
    }

    public static boolean hasPending(RhinoScriptRuntime runtime) {
        synchronized (PENDING) {
            for (PendingResume entry : PENDING) {
                if (entry.runtime == runtime) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void processWorldTick(net.minecraft.world.World mcWorld) {
        if (mcWorld == null || mcWorld.isRemote) {
            return;
        }

        cam72cam.mod.world.World irWorld = cam72cam.mod.world.World.get(mcWorld);
        List<PendingResume> ready = new ArrayList<>();
        synchronized (PENDING) {
            Iterator<PendingResume> iterator = PENDING.iterator();
            while (iterator.hasNext()) {
                PendingResume entry = iterator.next();
                if (entry.stock.getWorld() != irWorld) {
                    continue;
                }

                if (!isStockStillPresent(entry.stock)) {
                    cancelEntry(entry);
                    iterator.remove();
                    continue;
                }

                if (entry.ticksRemaining > 0) {
                    entry.ticksRemaining--;
                    continue;
                }

                iterator.remove();
                ready.add(entry);
            }
        }

        for (PendingResume entry : ready) {
            if (entry.onWaitComplete != null) {
                try {
                    entry.onWaitComplete.run();
                } catch (RuntimeException error) {
                    ScriptLog.continuationFailed(entry.stock.getUUID(), entry.functionName, error);
                    finishContinuation(entry);
                    continue;
                }
            }

            if (!isStockStillPresent(entry.stock)) {
                cancelEntry(entry);
                continue;
            }

            try {
                entry.runtime.resume(entry.pending, entry.unit, entry.functionName);
                if (!hasPending(entry.runtime) && entry.unit != null) {
                    entry.unit.onContinuationFinished(entry.functionName);
                }
            } catch (ContinuationPending ignored) {
                // utilPlay/time.sleep paused again; a new PendingResume was registered
            } catch (ScriptException error) {
                ScriptLog.continuationFailed(entry.stock.getUUID(), entry.functionName, error);
                finishContinuation(entry);
            }
        }
    }

    public static void clearForStock(UUID stockId) {
        synchronized (PENDING) {
            Iterator<PendingResume> iterator = PENDING.iterator();
            while (iterator.hasNext()) {
                PendingResume entry = iterator.next();
                if (!entry.stock.getUUID().equals(stockId)) {
                    continue;
                }
                cancelEntry(entry);
                iterator.remove();
            }
        }
    }

    private static void cancelEntry(PendingResume entry) {
        if (entry.onCancel != null) {
            try {
                entry.onCancel.run();
            } catch (RuntimeException error) {
                ScriptLog.continuationFailed(entry.stock.getUUID(), entry.functionName, error);
            }
        }
        finishContinuation(entry);
    }

    private static void finishContinuation(PendingResume entry) {
        if (entry.unit != null) {
            entry.unit.onContinuationFinished(entry.functionName);
        }
    }

    private static boolean isStockStillPresent(EntityRollingStock stock) {
        if (stock == null || stock.getWorld() == null) {
            return false;
        }
        return stock.getWorld().getEntity(stock.getUUID(), EntityRollingStock.class) != null;
    }

    private static final class PendingResume {
        private final EntityRollingStock stock;
        private final RhinoScriptRuntime runtime;
        private final ScriptUnitHandle unit;
        private final String functionName;
        private final ContinuationPending pending;
        private int ticksRemaining;
        private final Runnable onWaitComplete;
        private final Runnable onCancel;

        private PendingResume(
                EntityRollingStock stock,
                RhinoScriptRuntime runtime,
                ScriptUnitHandle unit,
                String functionName,
                ContinuationPending pending,
                int waitTicks,
                Runnable onWaitComplete,
                Runnable onCancel
        ) {
            this.stock = stock;
            this.runtime = runtime;
            this.unit = unit;
            this.functionName = functionName;
            this.pending = pending;
            this.ticksRemaining = waitTicks;
            this.onWaitComplete = onWaitComplete;
            this.onCancel = onCancel;
        }
    }
}
