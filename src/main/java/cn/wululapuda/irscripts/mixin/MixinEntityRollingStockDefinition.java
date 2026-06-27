package cn.wululapuda.irscripts.mixin;

import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cam72cam.immersiverailroading.registry.EntityRollingStockDefinition;
import cam72cam.immersiverailroading.util.DataBlock;
import cn.wululapuda.irscripts.script.StockScriptRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityRollingStockDefinition.class, remap = false)
public class MixinEntityRollingStockDefinition {
    @Inject(method = "loadData", at = @At("TAIL"), remap = false)
    private void irscripts$loadScripts(DataBlock data, CallbackInfo ci) {
        EntityRollingStockDefinition self = (EntityRollingStockDefinition) (Object) this;
        StockScriptRegistry.parseFromDefinition(self.defID, data);
    }
}
