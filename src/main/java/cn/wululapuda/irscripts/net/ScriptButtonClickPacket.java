package cn.wululapuda.irscripts.net;

import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cam72cam.mod.entity.Entity;
import cam72cam.mod.entity.Player;
import cam72cam.mod.net.Packet;
import cam72cam.mod.serialization.TagField;
import cn.wululapuda.irscripts.script.TrainScriptManager;

import java.util.UUID;

public class ScriptButtonClickPacket extends Packet {
    @TagField
    private UUID stockId;

    @TagField
    private String scriptPath;

    @TagField
    private String functionName;

    public ScriptButtonClickPacket(UUID stockId, String scriptPath, String functionName) {
        this.stockId = stockId;
        this.scriptPath = scriptPath;
        this.functionName = functionName;
    }

    public ScriptButtonClickPacket() {
    }

    @Override
    protected void handle() {
        Player player = getPlayer();
        Entity entity = getWorld().getEntity(stockId, Entity.class);
        if (!(entity instanceof EntityRollingStock)) {
            return;
        }
        if (player.getRiding() != entity) {
            return;
        }
        TrainScriptManager.invokeButton((EntityRollingStock) entity, scriptPath, functionName);
    }
}
