package cn.wululapuda.irscripts.mixin;

import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cam72cam.mod.MinecraftClient;
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

    @Unique
    private EntityRollingStock irscripts$stock;

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void irscripts$captureStock(ServerContainerBuilder serverContainer, Supplier<Boolean> valid, CallbackInfo ci) {
        IContainerAccess access = (IContainerAccess) (Object) serverContainer;
        EntityRollingStock stock = ContainerStockAccess.getStock(access.irscripts$getContainer());
        if (stock != null && ScriptButtonOverlay.shouldShow(stock)) {
            this.irscripts$stockId = stock.getUUID();
            this.irscripts$stock = stock;
        }
    }

    @Inject(method = "drawScreen", at = @At("RETURN"), remap = true)
    private void irscripts$drawScriptButtons(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        EntityRollingStock stock = resolveStock();
        if (stock != null) {
            ScriptButtonOverlay.draw((GuiScreen) (Object) this, stock, mouseX, mouseY);
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true, remap = true)
    private void irscripts$onScriptButtonClick(int mouseX, int mouseY, int mouseButton, CallbackInfoReturnable<Boolean> cir) {
        EntityRollingStock stock = resolveStock();
        if (stock != null && ScriptButtonOverlay.handleClick(stock, mouseX, mouseY, mouseButton)) {
            cir.setReturnValue(true);
        }
    }

    @Unique
    private EntityRollingStock resolveStock() {
        if (irscripts$stock != null && ScriptButtonOverlay.shouldShow(irscripts$stock)) {
            return irscripts$stock;
        }
        if (irscripts$stockId == null || MinecraftClient.getPlayer() == null) {
            return null;
        }
        EntityRollingStock stock = MinecraftClient.getPlayer().getWorld().getEntity(irscripts$stockId, EntityRollingStock.class);
        if (stock != null && ScriptButtonOverlay.shouldShow(stock)) {
            irscripts$stock = stock;
            return stock;
        }
        return null;
    }
}
