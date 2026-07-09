package cn.wululapuda.irscripts.net;

import cam72cam.mod.net.Packet;
import cam72cam.mod.serialization.TagField;

import java.util.UUID;

public class ScriptStopSoundPacket extends Packet {
    @TagField
    private UUID stockId;

    @TagField
    private UUID soundId;

    @TagField
    private String resolvedPath;

    public ScriptStopSoundPacket() {
    }

    public ScriptStopSoundPacket(UUID stockId, UUID soundId, String resolvedPath) {
        this.stockId = stockId;
        this.soundId = soundId;
        this.resolvedPath = resolvedPath;
    }

    @Override
    protected void handle() {
        ScriptClientSoundManager.stop(stockId, soundId, resolvedPath);
    }
}
