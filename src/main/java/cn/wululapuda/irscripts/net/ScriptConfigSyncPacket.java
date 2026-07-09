package cn.wululapuda.irscripts.net;

import cam72cam.mod.config.ConfigFile;
import cam72cam.mod.net.Packet;
import cam72cam.mod.serialization.TagField;
import cn.wululapuda.irscripts.config.IRScriptsModConfig;
import cn.wululapuda.irscripts.config.ScriptRuntimeSettings;
import cn.wululapuda.irscripts.util.ScriptLog;

public class ScriptConfigSyncPacket extends Packet {
    @TagField
    private boolean scriptPrint;

    @TagField
    private boolean debug;

    public ScriptConfigSyncPacket() {
    }

    public ScriptConfigSyncPacket(boolean scriptPrint, boolean debug) {
        this.scriptPrint = scriptPrint;
        this.debug = debug;
    }

    @Override
    protected void handle() {
        IRScriptsModConfig.scriptPrint = scriptPrint;
        IRScriptsModConfig.debug = debug;
        ConfigFile.write(IRScriptsModConfig.class);
        ScriptRuntimeSettings.applyFromConfig();
        ScriptLog.configUpdated(scriptPrint, debug);
    }
}
