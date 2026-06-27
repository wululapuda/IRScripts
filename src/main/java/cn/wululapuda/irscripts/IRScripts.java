package cn.wululapuda.irscripts;

import cam72cam.mod.net.Packet;
import cam72cam.mod.net.PacketDirection;
import cn.wululapuda.irscripts.net.ScriptButtonClickPacket;
import cn.wululapuda.irscripts.net.ScriptSoundPacket;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = IRScripts.MODID,
        name = IRScripts.NAME,
        version = IRScripts.VERSION,
        dependencies = "required-after:immersiverailroading;after:mixinbooter"
)
public class IRScripts {
    public static final String MODID = "irscripts";
    public static final String NAME = "IR Scripts";
    public static final String VERSION = "1.0.0";
    public static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        Packet.register(ScriptButtonClickPacket::new, PacketDirection.ClientToServer);
        Packet.register(ScriptSoundPacket::new, PacketDirection.ServerToClient);
        logger.info("IRScripts loaded. Nashorn JavaScript engine ready for rolling stock scripts.");
    }
}
