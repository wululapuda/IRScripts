package cn.wululapuda.irscripts.net;

import cam72cam.immersiverailroading.ConfigSound;
import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cam72cam.mod.math.Vec3d;
import cam72cam.mod.net.Packet;
import cam72cam.mod.resource.Identifier;
import cam72cam.mod.serialization.TagField;
import cam72cam.mod.sound.Audio;
import cam72cam.mod.sound.ISound;
import cam72cam.mod.sound.SoundCategory;
import cam72cam.mod.world.World;

import java.util.UUID;

public class ScriptSoundPacket extends Packet {
    @TagField
    private String file;

    @TagField
    private UUID stockId;

    @TagField
    private Vec3d pos;

    @TagField
    private Vec3d motion;

    @TagField
    private float volume;

    @TagField
    private float pitch;

    @TagField
    private int distance;

    @TagField
    private float scale;

    @TagField
    private boolean repeat;

    public ScriptSoundPacket() {
    }

    public ScriptSoundPacket(String file, UUID stockId, Vec3d pos, Vec3d motion, float volume, float pitch, int distance, float scale, boolean repeat) {
        this.file = file;
        this.stockId = stockId;
        this.pos = pos;
        this.motion = motion;
        this.volume = volume;
        this.pitch = pitch;
        this.distance = distance;
        this.scale = scale;
        this.repeat = repeat;
    }

    @Override
    protected void handle() {
        World world = getWorld();
        EntityRollingStock stock = stockId != null ? world.getEntity(stockId, EntityRollingStock.class) : null;

        Vec3d playPos = stock != null ? stock.getPosition() : pos;
        Vec3d playMotion = stock != null ? stock.getVelocity() : motion;

        ISound sound;
        if (stock != null) {
            sound = stock.createSound(new Identifier(file), repeat, distance, () -> 1.0F);
        } else {
            sound = Audio.newSound(
                    new Identifier(file),
                    SoundCategory.MASTER,
                    repeat,
                    (float) (distance * ConfigSound.soundDistanceScale),
                    scale);
        }

        sound.setVelocity(playMotion);
        sound.setVolume(volume);
        sound.setPitch(pitch);
        sound.play(playPos);
    }
}
