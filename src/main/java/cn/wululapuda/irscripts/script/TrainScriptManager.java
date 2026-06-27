package cn.wululapuda.irscripts.script;

import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cn.wululapuda.irscripts.IRScripts;

import javax.script.ScriptException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class TrainScriptManager {
    private static final Map<UUID, TrainScriptInstance> INSTANCES = new ConcurrentHashMap<>();
    private static final Set<UUID> NO_SCRIPT_STOCKS = ConcurrentHashMap.newKeySet();

    private TrainScriptManager() {
    }

    public static void clearAll() {
        INSTANCES.values().forEach(TrainScriptInstance::dispose);
        INSTANCES.clear();
        NO_SCRIPT_STOCKS.clear();
    }

    public static void onStockTick(EntityRollingStock stock) {
        TrainScriptInstance instance = ensureInstance(stock);
        if (instance != null) {
            instance.tick();
        }
    }

    public static void invokeButton(EntityRollingStock stock, String scriptPath, String functionName) {
        if (!stock.getWorld().isServer) {
            return;
        }
        TrainScriptInstance instance = ensureInstance(stock);
        if (instance != null) {
            instance.invokeButton(scriptPath, functionName);
        }
    }

    public static void detach(EntityRollingStock stock) {
        UUID stockId = stock.getUUID();
        TrainScriptInstance removed = INSTANCES.remove(stockId);
        if (removed != null) {
            removed.dispose();
        }
        NO_SCRIPT_STOCKS.remove(stockId);
    }

    private static TrainScriptInstance ensureInstance(EntityRollingStock stock) {
        UUID stockId = stock.getUUID();
        TrainScriptInstance instance = INSTANCES.get(stockId);
        if (instance != null) {
            return instance;
        }
        if (NO_SCRIPT_STOCKS.contains(stockId)) {
            return null;
        }

        instance = createInstance(stock);
        if (instance == null) {
            NO_SCRIPT_STOCKS.add(stockId);
            return null;
        }
        INSTANCES.put(stockId, instance);
        return instance;
    }

    private static TrainScriptInstance createInstance(EntityRollingStock stock) {
        List<StockScriptFile> scripts = StockScriptRegistry.getScripts(stock.getDefinitionID());
        if (scripts.isEmpty()) {
            return null;
        }

        try {
            return new TrainScriptInstance(stock, scripts);
        } catch (ScriptException ex) {
            IRScripts.logger.error("Failed to initialize scripts for stock {} ({}): {}", stock.getUUID(), stock.getDefinitionID(), ex.getMessage());
            return null;
        }
    }
}
