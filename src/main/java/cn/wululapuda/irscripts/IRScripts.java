package cn.wululapuda.irscripts;

import cam72cam.mod.net.Packet;
import cam72cam.mod.net.PacketDirection;
import cam72cam.mod.config.ConfigFile;
import cn.wululapuda.irscripts.config.IRScriptsModConfig;
import cn.wululapuda.irscripts.config.ScriptRuntimeSettings;
import cn.wululapuda.irscripts.net.ScriptButtonClickPacket;
import cn.wululapuda.irscripts.net.ScriptConfigSyncPacket;
import cn.wululapuda.irscripts.net.ScriptModelRenderPacket;
import cn.wululapuda.irscripts.net.ScriptParticlePacket;
import cn.wululapuda.irscripts.net.ScriptSoundPacket;
import cn.wululapuda.irscripts.net.ScriptStopSoundPacket;
import cn.wululapuda.irscripts.util.ScriptLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;

@Mod(
        modid = IRScripts.MODID,
        name = IRScripts.NAME,
        version = IRScripts.VERSION,
        dependencies = "required-after:immersiverailroading;after:mixinbooter"
)
public class IRScripts {
    public static final String MODID = "irscripts";
    public static final String NAME = "IR Scripts";
    public static final String VERSION = "2.0.0";
    public static org.apache.logging.log4j.Logger logger;

    @SidedProxy(
            clientSide = "cn.wululapuda.irscripts.client.ClientProxy",
            serverSide = "cn.wululapuda.irscripts.CommonProxy"
    )
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        ConfigFile.sync(IRScriptsModConfig.class);
        ScriptRuntimeSettings.applyFromConfig();
        Packet.register(ScriptButtonClickPacket::new, PacketDirection.ClientToServer);
        Packet.register(ScriptConfigSyncPacket::new, PacketDirection.ClientToServer);
        Packet.register(ScriptSoundPacket::new, PacketDirection.ServerToClient);
        Packet.register(ScriptStopSoundPacket::new, PacketDirection.ServerToClient);
        Packet.register(ScriptParticlePacket::new, PacketDirection.ServerToClient);
        Packet.register(ScriptModelRenderPacket::new, PacketDirection.ServerToClient);
        ScriptEventHandler.init();
        proxy.preInit(event);
        ScriptLog.startup();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ScriptEventHandler.onInitialization(event);
        proxy.init(event);
    }

    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        ScriptEventHandler.onLoadComplete(event);
    }

    @Mod.EventHandler
    public void serverAboutToStart(FMLServerAboutToStartEvent event) {
        ScriptEventHandler.onServerAboutToStart(event);
    }
}
