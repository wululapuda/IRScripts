package cn.wululapuda.irscripts.mixin;

import cam72cam.immersiverailroading.registry.DefinitionManager;
import cn.wululapuda.irscripts.script.StockScriptRegistry;
import cn.wululapuda.irscripts.script.TrackScriptBootstrap;
import cn.wululapuda.irscripts.script.TrackScriptManager;
import cn.wululapuda.irscripts.script.TrackScriptRegistry;
import cn.wululapuda.irscripts.script.TrainScriptManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DefinitionManager.class, remap = false)
public class MixinDefinitionManager {
    @Inject(method = "initDefinitions", at = @At("HEAD"), remap = false)
    private static void irscripts$onDefinitionsReload(CallbackInfo ci) {
        StockScriptRegistry.clear();
        TrackScriptRegistry.clear();
        TrackScriptManager.clearAll();
        TrainScriptManager.clearAll();
    }

    @Inject(method = "initDefinitions", at = @At("RETURN"), remap = false)
    private static void irscripts$onDefinitionsLoaded(CallbackInfo ci) {
        TrackScriptBootstrap.scanAllTracks();
    }
}
