package cn.wululapuda.irscripts.mixin;

import cam72cam.mod.gui.container.ClientContainerBuilder;
import cam72cam.mod.gui.container.ServerContainerBuilder;
import cn.wululapuda.irscripts.gui.ContainerStockAccess;
import cn.wululapuda.irscripts.gui.ScriptButtonOverlay;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;
import java.util.function.Supplier;

@Mixin(value = ClientContainerBuilder.class, remap = false)
public class MixinClientContainerBuilder {
    @Unique
    private UUID irscripts$stockId;

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void irscripts$captureStock(ServerContainerBuilder serverContainer, Supplier<Boolean> valid, CallbackInfo ci) {
        IContainerAccess access = (IContainerAccess) (Object) serverContainer;
        UUID stockId = ContainerStockAccess.getStockId(access.irscripts$getContainer());
        if (stockId != null && ScriptButtonOverlay.shouldShowStockId(stockId)) {
            this.irscripts$stockId = stockId;
        }
    }

    @Inject(method = "drawScreen", at = @At("RETURN"), remap = true)
    private void irscripts$drawScriptButtons(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (irscripts$stockId != null) {
            ScriptButtonOverlay.drawForStockId((GuiScreen) (Object) this, irscripts$stockId, mouseX, mouseY);
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true, remap = true)
    private void irscripts$onScriptButtonClick(int mouseX, int mouseY, int mouseButton, CallbackInfoReturnable<Boolean> cir) {
        if (irscripts$stockId != null && ScriptButtonOverlay.handleClickForStockId(irscripts$stockId, mouseX, mouseY, mouseButton)) {
            cir.setReturnValue(true);
        }
    }
}
