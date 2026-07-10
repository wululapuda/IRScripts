package cn.wululapuda.irscripts.mixin;

import cam72cam.immersiverailroading.library.SwitchState;
import cam72cam.immersiverailroading.tile.TileRail;
import cn.wululapuda.irscripts.script.TrackScriptManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileRail.class, remap = false)
public class MixinTileRail {
    @Inject(method = "setSwitchState", at = @At("RETURN"), remap = false)
    private void irscripts$afterSwitchState(SwitchState state, CallbackInfo ci) {
        TrackScriptManager.onSwitchChanged((TileRail) (Object) this);
    }
}
