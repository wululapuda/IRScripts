package cn.wululapuda.irscripts.mixin;

import cam72cam.immersiverailroading.tile.TileRail;
import cam72cam.immersiverailroading.util.RailInfo;
import cn.wululapuda.irscripts.script.TrackScriptManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = RailInfo.class, remap = false)
public class MixinRailInfo {
    @Inject(
            method = "build(Lcam72cam/mod/entity/Player;Lcam72cam/mod/math/Vec3i;Z)Ljava/util/List;",
            at = @At("RETURN"),
            remap = false
    )
    private void irscripts$afterBuild(
            cam72cam.mod.entity.Player player,
            cam72cam.mod.math.Vec3i pos,
            boolean consume,
            CallbackInfoReturnable<List<cam72cam.mod.item.ItemStack>> cir
    ) {
        if (cir.getReturnValue() == null || player == null || player.getWorld() == null || !player.getWorld().isServer) {
            return;
        }
        TileRail rail = player.getWorld().getBlockEntity(pos, TileRail.class);
        if (rail != null) {
            TrackScriptManager.onTrackStateUpdated(rail);
        }
    }
}
