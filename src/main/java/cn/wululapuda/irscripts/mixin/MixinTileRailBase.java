package cn.wululapuda.irscripts.mixin;

import cam72cam.immersiverailroading.tile.TileRail;
import cam72cam.immersiverailroading.tile.TileRailBase;
import cn.wululapuda.irscripts.script.TrackScriptManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileRailBase.class, remap = false)
public class MixinTileRailBase {
    @Inject(method = "onNeighborChange", at = @At("RETURN"), remap = false)
    private void irscripts$afterNeighborChange(cam72cam.mod.math.Vec3i neighbor, CallbackInfo ci) {
        TrackScriptManager.onTrackStateUpdated((TileRailBase) (Object) this);
    }

    @Inject(method = "load", at = @At("RETURN"), remap = false)
    private void irscripts$afterLoad(cam72cam.mod.serialization.TagCompound data, CallbackInfo ci) {
        TileRailBase self = (TileRailBase) (Object) this;
        if (self.getWorld() != null && self.getWorld().isServer) {
            TrackScriptManager.onTrackStateUpdated(self);
        }
    }

    @Inject(method = "onBreak", at = @At("HEAD"), remap = false)
    private void irscripts$beforeBreak(CallbackInfo ci) {
        TrackScriptManager.onTrackRemoved((TileRailBase) (Object) this);
    }
}
