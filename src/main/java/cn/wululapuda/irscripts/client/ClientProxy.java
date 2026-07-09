package cn.wululapuda.irscripts.client;

import cn.wululapuda.irscripts.CommonProxy;
import cn.wululapuda.irscripts.ScriptEventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        ClientEventHandler.init();
        ScriptClientParticleManager.init();
        ScriptClientModelManager.init();
        ScriptEventHandler.onClientReady();
    }
}
