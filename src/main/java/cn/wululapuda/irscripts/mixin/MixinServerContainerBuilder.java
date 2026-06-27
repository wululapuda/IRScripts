package cn.wululapuda.irscripts.mixin;

import cam72cam.mod.gui.container.IContainer;
import cam72cam.mod.gui.container.ServerContainerBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerContainerBuilder.class, remap = false)
public class MixinServerContainerBuilder implements IContainerAccess {
    @Unique
    private IContainer irscripts$container;

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void irscripts$storeContainer(net.minecraft.inventory.IInventory playerInventory, IContainer container, CallbackInfo ci) {
        this.irscripts$container = container;
    }

    @Override
    public IContainer irscripts$getContainer() {
        return irscripts$container;
    }
}
