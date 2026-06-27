package cn.wululapuda.irscripts.mixin;

import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cn.wululapuda.irscripts.gui.ScriptButtonOverlay;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GuiInventory.class)
public class MixinGuiInventory {
    @Inject(method = "drawScreen", at = @At("RETURN"), remap = true)
    private void irscripts$drawScriptButtons(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        EntityRollingStock stock = ScriptButtonOverlay.getRidingStock();
        if (stock != null) {
            ScriptButtonOverlay.draw((GuiScreen) (Object) this, stock, mouseX, mouseY);
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true, remap = true)
    private void irscripts$onScriptButtonClick(int mouseX, int mouseY, int mouseButton, CallbackInfoReturnable<Boolean> cir) {
        EntityRollingStock stock = ScriptButtonOverlay.getRidingStock();
        if (stock != null && ScriptButtonOverlay.handleClick(stock, mouseX, mouseY, mouseButton)) {
            cir.setReturnValue(true);
        }
    }
}
