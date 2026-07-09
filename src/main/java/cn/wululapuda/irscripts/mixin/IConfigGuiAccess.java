package cn.wululapuda.irscripts.mixin;

import cam72cam.mod.config.ConfigGui;
import cam72cam.mod.gui.screen.IScreenBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.function.Function;

@Mixin(value = ConfigGui.class, remap = false)
public interface IConfigGuiAccess {
    @Accessor("widgets")
    List<Function<IScreenBuilder, Object>> irscripts$getWidgets();

    @Accessor("parent")
    ConfigGui irscripts$getParent();

    @Accessor("pc")
    Object irscripts$getPc();
}
