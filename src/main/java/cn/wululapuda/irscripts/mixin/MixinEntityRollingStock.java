package cn.wululapuda.irscripts.mixin;

import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cn.wululapuda.irscripts.script.TrainScriptManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityRollingStock.class, remap = false)
public class MixinEntityRollingStock {
    @Inject(method = "onTick", at = @At("TAIL"), remap = false)
    private void irscripts$onTick(CallbackInfo ci) {
        EntityRollingStock self = (EntityRollingStock) (Object) this;
        if (self.getWorld().isServer) {
            TrainScriptManager.onStockTick(self);
        }
    }

    @Inject(method = "onRemoved", at = @At("HEAD"), remap = false)
    private void irscripts$onRemoved(CallbackInfo ci) {
        TrainScriptManager.detach((EntityRollingStock) (Object) this);
    }
}
