package cn.wululapuda.irscripts.mixin;

import cam72cam.mod.config.ConfigGui;
import cam72cam.mod.gui.screen.IScreenBuilder;
import cn.wululapuda.irscripts.client.IrConfigGuiSupport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ConfigGui.class, remap = false)
public class MixinConfigGui {
    @Inject(method = "init", at = @At("TAIL"), remap = false)
    private void irscripts$addIrScriptsCategory(IScreenBuilder screen, CallbackInfo ci) {
        ConfigGui configGui = (ConfigGui) (Object) this;
        if (IrConfigGuiSupport.shouldAddIrScriptsEntry(configGui)) {
            IrConfigGuiSupport.addIrScriptsEntry(configGui, screen);
        }
    }

    @Inject(method = "onClose", at = @At("TAIL"), remap = false)
    private void irscripts$syncIrScriptsConfig(CallbackInfo ci) {
        IrConfigGuiSupport.syncIrScriptsConfigIfClosing((ConfigGui) (Object) this);
    }
}
