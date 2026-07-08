package cn.wululapuda.irscripts.script;

import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cam72cam.mod.world.World;
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

        for (EntityRollingStock stock : world.getEntities(EntityRollingStock.class)) {
            TrainScriptManager.onStockTick(stock);
        }
    }
}
