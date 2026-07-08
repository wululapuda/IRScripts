package cn.wululapuda.irscripts.script;

import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cam72cam.mod.world.World;
import cn.wululapuda.irscripts.config.ScriptRuntimeSettings;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public final class StockScriptTickHandler {
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.world.isRemote) {
            return;
        }

        World world = World.get(event.world);
        if (world == null || !world.isServer) {
            return;
        }

        ScriptBootstrap.ensureScanned();
        ScriptContinuationScheduler.processWorldTick(event.world);
        TrainScriptManager.purgeStaleInstances(world);

        long budgetMs = ScriptRuntimeSettings.getTickBudgetMs();
        long deadlineNanos = budgetMs > 0L
                ? System.nanoTime() + budgetMs * 1_000_000L
                : Long.MAX_VALUE;

        for (EntityRollingStock stock : world.getEntities(EntityRollingStock.class)) {
            if (System.nanoTime() >= deadlineNanos) {
                break;
            }
            TrainScriptManager.onStockTick(stock);
        }
    }
}
