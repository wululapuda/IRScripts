package cn.wululapuda.irscripts.client;

import cam72cam.mod.config.ConfigGui;
import cam72cam.mod.gui.screen.IScreen;
import cam72cam.mod.gui.screen.ScreenBuilder;
import cam72cam.mod.world.World;
import cn.wululapuda.irscripts.net.ScriptClientSoundManager;
import cn.wululapuda.irscripts.script.ScriptBootstrap;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.lang.reflect.Field;

public final class ClientEventHandler {
    private ClientEventHandler() {
    }

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        MinecraftForge.EVENT_BUS.register(new ScriptButtonGuiHandler());
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getMinecraft();
        if (minecraft.world == null) {
            return;
        }

        World world = World.get(minecraft.world);
        if (world != null) {
            ScriptBootstrap.ensureScanned();
            ScriptClientSoundManager.purgeMissingStocks(world);
            ScriptClientModelManager.purgeMissingStocks(world);
        }
    }

    @SubscribeEvent
    public void onInitGuiPost(GuiScreenEvent.InitGuiEvent.Post event) {
        if (!(event.getGui() instanceof ScreenBuilder)) {
            return;
        }
        ScreenBuilder screenBuilder = (ScreenBuilder) event.getGui();
        ConfigGui configGui = extractConfigGui(screenBuilder);
        if (configGui == null) {
            return;
        }
        if (IrConfigGuiSupport.shouldAddIrScriptsEntry(configGui)) {
            IrConfigGuiSupport.addIrScriptsEntry(configGui, screenBuilder);
        }
    }

    private static ConfigGui extractConfigGui(ScreenBuilder screenBuilder) {
        IScreen screen = extractScreen(screenBuilder);
        if (screen instanceof ConfigGui) {
            return (ConfigGui) screen;
        }
        return null;
    }

    private static IScreen extractScreen(ScreenBuilder screenBuilder) {
        try {
            Field screenField = ScreenBuilder.class.getDeclaredField("screen");
            screenField.setAccessible(true);
            return (IScreen) screenField.get(screenBuilder);
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }
}
