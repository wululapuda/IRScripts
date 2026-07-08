package cn.wululapuda.irscripts.mixin;

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
        ScriptButtonOverlay.drawIfRiding((GuiScreen) (Object) this, mouseX, mouseY);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true, remap = true)
    private void irscripts$onScriptButtonClick(int mouseX, int mouseY, int mouseButton, CallbackInfoReturnable<Boolean> cir) {
        if (ScriptButtonOverlay.handleClickIfRiding(mouseX, mouseY, mouseButton)) {
            cir.setReturnValue(true);
        }
    }
}
