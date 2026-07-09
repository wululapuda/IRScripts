package cn.wululapuda.irscripts.net;

import cam72cam.mod.net.Packet;
import cam72cam.mod.serialization.TagField;
import cn.wululapuda.irscripts.IRScripts;
import cn.wululapuda.irscripts.model.ModelRenderSpec;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

public class ScriptModelRenderPacket extends Packet {
    @TagField
    private UUID stockId;

    @TagField
    private UUID renderId;

    @TagField
    private boolean remove;

    @TagField
    private String modelPath;

    @TagField
    private String groups;

    @TagField
    private float posX;

    @TagField
    private float posY;

    @TagField
    private float posZ;

    @TagField
    private float originX;

    @TagField
    private float originY;

    @TagField
    private float originZ;

    @TagField
    private float scaleX;

    @TagField
    private float scaleY;

    @TagField
    private float scaleZ;

    @TagField
    private float rotX;

    @TagField
    private float rotY;

    @TagField
    private float rotZ;

    @TagField
    private float pivotX;

    @TagField
    private float pivotY;

    @TagField
    private float pivotZ;

    @TagField
    private float centerX;

    @TagField
    private float centerY;

    @TagField
    private float centerZ;

    @TagField
    private float normalX;

    @TagField
    private float normalY;

    @TagField
    private float normalZ;

    public ScriptModelRenderPacket() {
    }

    private ScriptModelRenderPacket(UUID stockId, UUID renderId, boolean remove, ModelRenderSpec spec) {
        this.stockId = stockId;
        this.renderId = renderId;
        this.remove = remove;
        if (!remove && spec != null) {
            this.modelPath = spec.modelPath;
            this.groups = joinGroups(spec.groupNames);
            this.posX = (float) spec.position[0];
            this.posY = (float) spec.position[1];
            this.posZ = (float) spec.position[2];
            this.originX = (float) spec.origin[0];
            this.originY = (float) spec.origin[1];
            this.originZ = (float) spec.origin[2];
            this.scaleX = (float) spec.scale[0];
            this.scaleY = (float) spec.scale[1];
            this.scaleZ = (float) spec.scale[2];
            this.rotX = (float) spec.rotationDeg[0];
            this.rotY = (float) spec.rotationDeg[1];
            this.rotZ = (float) spec.rotationDeg[2];
            this.pivotX = (float) spec.pivot[0];
            this.pivotY = (float) spec.pivot[1];
            this.pivotZ = (float) spec.pivot[2];
            this.centerX = (float) spec.center[0];
            this.centerY = (float) spec.center[1];
            this.centerZ = (float) spec.center[2];
            this.normalX = (float) spec.normal[0];
            this.normalY = (float) spec.normal[1];
            this.normalZ = (float) spec.normal[2];
        }
    }

    public static ScriptModelRenderPacket add(UUID stockId, UUID renderId, ModelRenderSpec spec) {
        return new ScriptModelRenderPacket(stockId, renderId, false, spec);
    }

    public static ScriptModelRenderPacket remove(UUID stockId, UUID renderId) {
        return new ScriptModelRenderPacket(stockId, renderId, true, null);
    }

    @Override
    protected void handle() {
        try {
            Class<?> manager = Class.forName("cn.wululapuda.irscripts.client.ScriptClientModelManager");
            Method method = manager.getMethod("applyPacket", ScriptModelRenderPacket.class);
            method.invoke(null, this);
        } catch (ReflectiveOperationException error) {
            IRScripts.logger.warn("[Model] Failed to apply client render packet", error);
        }
    }

    public UUID getStockId() {
        return stockId;
    }

    public UUID getRenderId() {
        return renderId;
    }

    public boolean isRemove() {
        return remove;
    }

    public String getModelPath() {
        return modelPath;
    }

    public String getGroups() {
        return groups;
    }

    public float getPosX() {
        return posX;
    }

    public float getPosY() {
        return posY;
    }

    public float getPosZ() {
        return posZ;
    }

    public float getOriginX() {
        return originX;
    }

    public float getOriginY() {
        return originY;
    }

    public float getOriginZ() {
        return originZ;
    }

    public float getScaleX() {
        return scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    public float getScaleZ() {
        return scaleZ;
    }

    public float getRotX() {
        return rotX;
    }

    public float getRotY() {
        return rotY;
    }

    public float getRotZ() {
        return rotZ;
    }

    public float getPivotX() {
        return pivotX;
    }

    public float getPivotY() {
        return pivotY;
    }

    public float getPivotZ() {
        return pivotZ;
    }

    public float getCenterX() {
        return centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public float getCenterZ() {
        return centerZ;
    }

    public float getNormalX() {
        return normalX;
    }

    public float getNormalY() {
        return normalY;
    }

    public float getNormalZ() {
        return normalZ;
    }

    private static String joinGroups(List<String> groupNames) {
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < groupNames.size(); index++) {
            if (index > 0) {
                builder.append('\u001f');
            }
            builder.append(groupNames.get(index));
        }
        return builder.toString();
    }
}
