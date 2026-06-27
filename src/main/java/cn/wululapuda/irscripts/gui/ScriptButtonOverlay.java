package cn.wululapuda.irscripts.gui;

import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cam72cam.mod.MinecraftClient;
import cam72cam.mod.entity.Entity;
import cam72cam.mod.entity.Player;
import cn.wululapuda.irscripts.net.ScriptButtonClickPacket;
import cn.wululapuda.irscripts.script.ScriptButtonEntry;
import cn.wululapuda.irscripts.script.StockScriptRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

import java.util.Collections;
import java.util.List;

public final class ScriptButtonOverlay {
    private static final int PANEL_X = 8;
    private static final int PANEL_Y = 8;
    private static final int BUTTON_WIDTH = 120;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_GAP = 4;
    private static final int PANEL_PADDING = 6;
    private static final int TITLE_HEIGHT = 12;

    private ScriptButtonOverlay() {
    }

    public static boolean shouldShow(EntityRollingStock stock) {
        if (stock == null) {
            return false;
        }
        String defId = stock.getDefinitionID();
        return !StockScriptRegistry.getScripts(defId).isEmpty()
                && !StockScriptRegistry.getButtonEntries(defId).isEmpty();
    }

    public static EntityRollingStock getRidingStock() {
        Player player = MinecraftClient.getPlayer();
        if (player == null) {
            return null;
        }
        Entity riding = player.getRiding();
        if (riding == null) {
            return null;
        }
        if (riding instanceof EntityRollingStock) {
            return (EntityRollingStock) riding;
        }
        return riding.as(EntityRollingStock.class);
    }

    public static List<ScriptButtonEntry> getButtons(EntityRollingStock stock) {
        if (!shouldShow(stock)) {
            return Collections.emptyList();
        }
        return StockScriptRegistry.getButtonEntries(stock.getDefinitionID());
    }

    public static void draw(GuiScreen gui, EntityRollingStock stock, int mouseX, int mouseY) {
        List<ScriptButtonEntry> buttons = getButtons(stock);
        if (buttons.isEmpty()) {
            return;
        }

        int panelHeight = PANEL_PADDING * 2 + TITLE_HEIGHT + buttons.size() * (BUTTON_HEIGHT + BUTTON_GAP) - BUTTON_GAP;
        int panelWidth = BUTTON_WIDTH + PANEL_PADDING * 2;

        GlStateManager.disableLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        Gui.drawRect(PANEL_X, PANEL_Y, PANEL_X + panelWidth, PANEL_Y + panelHeight, 0xC0101010);
        gui.drawString(fontRenderer, "IR Scripts", PANEL_X + PANEL_PADDING, PANEL_Y + PANEL_PADDING, 0xFFFFFF);

        int buttonY = PANEL_Y + PANEL_PADDING + TITLE_HEIGHT;
        for (ScriptButtonEntry button : buttons) {
            int x1 = PANEL_X + PANEL_PADDING;
            int y1 = buttonY;
            int x2 = x1 + BUTTON_WIDTH;
            int y2 = y1 + BUTTON_HEIGHT;

            boolean hovered = mouseX >= x1 && mouseX < x2 && mouseY >= y1 && mouseY < y2;
            int background = hovered ? 0xFF4A7FC1 : 0xFF303030;
            Gui.drawRect(x1, y1, x2, y2, background);
            Gui.drawRect(x1, y1, x2, y1 + 1, 0xFF808080);
            Gui.drawRect(x1, y2 - 1, x2, y2, 0xFF101010);

            String label = button.label();
            int textWidth = fontRenderer.getStringWidth(label);
            int textX = x1 + Math.max(4, (BUTTON_WIDTH - textWidth) / 2);
            int textY = y1 + (BUTTON_HEIGHT - 8) / 2;
            gui.drawString(fontRenderer, label, textX, textY, 0xFFFFFF);

            buttonY += BUTTON_HEIGHT + BUTTON_GAP;
        }
    }

    public static ScriptButtonEntry hitTest(EntityRollingStock stock, int mouseX, int mouseY) {
        List<ScriptButtonEntry> buttons = getButtons(stock);
        if (buttons.isEmpty()) {
            return null;
        }

        int buttonY = PANEL_Y + PANEL_PADDING + TITLE_HEIGHT;
        for (ScriptButtonEntry button : buttons) {
            int x1 = PANEL_X + PANEL_PADDING;
            int y1 = buttonY;
            int x2 = x1 + BUTTON_WIDTH;
            int y2 = y1 + BUTTON_HEIGHT;
            if (mouseX >= x1 && mouseX < x2 && mouseY >= y1 && mouseY < y2) {
                return button;
            }
            buttonY += BUTTON_HEIGHT + BUTTON_GAP;
        }
        return null;
    }

    public static boolean handleClick(EntityRollingStock stock, int mouseX, int mouseY, int mouseButton) {
        if (mouseButton != 0 || !shouldShow(stock)) {
            return false;
        }
        ScriptButtonEntry hit = hitTest(stock, mouseX, mouseY);
        if (hit == null) {
            return false;
        }
        new ScriptButtonClickPacket(stock.getUUID(), hit.scriptPath, hit.functionName).sendToServer();
        return true;
    }
}
