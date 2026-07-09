package cn.wululapuda.irscripts.net;

import cam72cam.mod.net.Packet;
import cam72cam.mod.serialization.TagField;
import cn.wululapuda.irscripts.IRScripts;

import java.lang.reflect.Method;
import java.util.UUID;

public class ScriptParticlePacket extends Packet {
    public static final int TYPE_SMOKE = 0;
    public static final int TYPE_STEAM = 1;

    @TagField
    private UUID stockId;

    @TagField
    private float startX;

    @TagField
    private float startY;

    @TagField
    private float startZ;

    @TagField
    private float offsetX;

    @TagField
    private float offsetY;

    @TagField
    private float offsetZ;

    @TagField
    private float speed;

    @TagField
    private float time;

    @TagField
    private float concentration;

    @TagField
    private byte type;

    @TagField
    private String texture;

    public ScriptParticlePacket() {
    }

    public ScriptParticlePacket(
            UUID stockId,
            float startX,
            float startY,
            float startZ,
            float offsetX,
            float offsetY,
            float offsetZ,
            float speed,
            float time,
            float concentration,
            byte type,
            String texture
    ) {
        this.stockId = stockId;
        this.startX = startX;
        this.startY = startY;
        this.startZ = startZ;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.speed = speed;
        this.time = time;
        this.concentration = concentration;
        this.type = type;
        this.texture = texture;
    }

    @Override
    protected void handle() {
        try {
            Class<?> manager = Class.forName("cn.wululapuda.irscripts.client.ScriptClientParticleManager");
            Method start = manager.getMethod(
                    "startEffect",
                    UUID.class,
                    float.class,
                    float.class,
                    float.class,
                    float.class,
                    float.class,
                    float.class,
                    float.class,
                    float.class,
                    float.class,
                    byte.class,
                    String.class
            );
            start.invoke(
                    null,
                    stockId,
                    startX,
                    startY,
                    startZ,
                    offsetX,
                    offsetY,
                    offsetZ,
                    speed,
                    time,
                    concentration,
                    type,
                    texture
            );
        } catch (ReflectiveOperationException error) {
            IRScripts.logger.warn("[Particle] Failed to start client effect", error);
        }
    }
}
