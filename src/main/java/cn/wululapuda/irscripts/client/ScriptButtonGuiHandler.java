package cn.wululapuda.irscripts.client;

import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cn.wululapuda.irscripts.gui.ContainerStockAccess;
import cn.wululapuda.irscripts.gui.ScriptButtonOverlay;
import cn.wululapuda.irscripts.script.ScriptBootstrap;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Draws BUTTON-mode script buttons without relying on client mixins (MixinBooter optional).
 */
public final class ScriptButtonGuiHandler {
    private static int lastMouseX;
    private static int lastMouseY;

    @SubscribeEvent
    public void onDrawScreenPost(GuiScreenEvent.DrawScreenEvent.Post event) {
        ScriptBootstrap.ensureScanned();
        lastMouseX = event.getMouseX();
        lastMouseY = event.getMouseY();

        GuiScreen gui = event.getGui();
        EntityRollingStock stock = ContainerStockAccess.resolveStock(gui);
        if (stock == null) {
            return;
        }

        ScriptButtonOverlay.draw(gui, stock, lastMouseX, lastMouseY);
    }

    @SubscribeEvent
    public void onMouseInputPre(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (!isMousePressed() || getMouseButton() != 0) {
            return;
        }

        ScriptBootstrap.ensureScanned();

        GuiScreen gui = event.getGui();
        EntityRollingStock stock = ContainerStockAccess.resolveStock(gui);
        if (stock == null) {
            return;
        }

        if (ScriptButtonOverlay.handleClick(stock, lastMouseX, lastMouseY, 0)) {
            event.setCanceled(true);
        }
    }

    private static boolean isMousePressed() {
        try {
            Class<?> mouse = Class.forName("org.lwjgl.input.Mouse");
            return (Boolean) mouse.getMethod("getEventButtonState").invoke(null);
        } catch (ReflectiveOperationException ex) {
            return false;
        }
    }

    private static int getMouseButton() {
        try {
            Class<?> mouse = Class.forName("org.lwjgl.input.Mouse");
            return (Integer) mouse.getMethod("getEventButton").invoke(null);
        } catch (ReflectiveOperationException ex) {
            return -1;
        }
    }
}
