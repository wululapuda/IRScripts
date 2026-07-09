package cn.wululapuda.irscripts.api;

import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cam72cam.mod.entity.Entity;
import cn.wululapuda.irscripts.model.ModelRenderSpec;
import cn.wululapuda.irscripts.net.ScriptModelRenderPacket;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ScriptModelTracker {
    private final UUID stockId;
    private final Map<UUID, ModelRenderSpec> active = new ConcurrentHashMap<>();

    public ScriptModelTracker(UUID stockId) {
        this.stockId = stockId;
    }

    public UUID addRender(EntityRollingStock stock, ModelRenderSpec spec) {
        UUID renderId = UUID.randomUUID();
        active.put(renderId, spec);
        send(stock, ScriptModelRenderPacket.add(stockId, renderId, spec));
        return renderId;
    }

    public void removeRender(EntityRollingStock stock, UUID renderId) {
        if (active.remove(renderId) != null) {
            send(stock, ScriptModelRenderPacket.remove(stockId, renderId));
        }
    }

    public void clearAll(EntityRollingStock stock) {
        Iterator<UUID> iterator = active.keySet().iterator();
        while (iterator.hasNext()) {
            UUID renderId = iterator.next();
            iterator.remove();
            send(stock, ScriptModelRenderPacket.remove(stockId, renderId));
        }
    }

    private void send(EntityRollingStock stock, ScriptModelRenderPacket packet) {
        if (stock == null) {
            packet.sendToAll();
            return;
        }
        packet.sendToObserving((Entity) stock);
    }
}
