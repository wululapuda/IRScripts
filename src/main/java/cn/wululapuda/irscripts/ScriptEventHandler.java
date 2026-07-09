package cn.wululapuda.irscripts;

import cam72cam.immersiverailroading.registry.DefinitionManager;
import cn.wululapuda.irscripts.script.ScriptBootstrap;
import cn.wululapuda.irscripts.script.StockScriptTickHandler;
import cn.wululapuda.irscripts.util.ScriptLog;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;

public final class ScriptEventHandler {
    private ScriptEventHandler() {
    }

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new StockScriptTickHandler());
    }

    public static void onInitialization(FMLInitializationEvent event) {
        tryScan("FMLInitializationEvent");
    }

    public static void onLoadComplete(FMLLoadCompleteEvent event) {
        tryScan("FMLLoadCompleteEvent");
    }

    public static void onClientReady() {
        tryScan("ClientReady");
    }

    public static void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        tryScan("FMLServerAboutToStartEvent");
    }

    private static void tryScan(String phase) {
        if (DefinitionManager.getDefinitionNames() == null || DefinitionManager.getDefinitionNames().isEmpty()) {
            ScriptLog.bootstrapWaiting(phase + ": definitions not ready");
            return;
        }
        ScriptLog.bootstrapScanStart(phase);
        ScriptBootstrap.scanAllDefinitions();
    }
}
