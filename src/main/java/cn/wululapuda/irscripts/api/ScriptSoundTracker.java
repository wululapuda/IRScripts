package cn.wululapuda.irscripts.api;

import cam72cam.mod.entity.Entity;
import cam72cam.mod.math.Vec3d;
import cam72cam.mod.world.World;
import cn.wululapuda.irscripts.net.ScriptStopSoundPacket;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Tracks active script sounds for one rolling-stock instance (server side). */
public final class ScriptSoundTracker {
    private final UUID stockId;
    private final Map<UUID, String> activeSounds = new ConcurrentHashMap<>();

    public ScriptSoundTracker(UUID stockId) {
        this.stockId = stockId;
    }

    public UUID register(SoundPlayRequest request) {
        UUID soundId = UUID.randomUUID();
        activeSounds.put(soundId, request.getPath());
        return soundId;
    }

    public void unregister(UUID soundId) {
        if (soundId != null) {
            activeSounds.remove(soundId);
        }
    }

    public void stopAll(Entity observer) {
        if (activeSounds.isEmpty()) {
            return;
        }
        sendStopPacket(new ScriptStopSoundPacket(stockId, null, null), observer);
        activeSounds.clear();
    }

    public void stopByPath(String resolvedPath, Entity observer) {
        if (resolvedPath == null || resolvedPath.isEmpty()) {
            return;
        }

        Iterator<Map.Entry<UUID, String>> iterator = activeSounds.entrySet().iterator();
        boolean sent = false;
        while (iterator.hasNext()) {
            Map.Entry<UUID, String> entry = iterator.next();
            if (resolvedPath.equals(entry.getValue())) {
                sendStopPacket(new ScriptStopSoundPacket(stockId, entry.getKey(), null), observer);
                iterator.remove();
                sent = true;
            }
        }

        if (!sent) {
            sendStopPacket(new ScriptStopSoundPacket(stockId, null, resolvedPath), observer);
        }
    }

    public void stopSound(UUID soundId, Entity observer) {
        if (soundId == null || !activeSounds.containsKey(soundId)) {
            return;
        }
        sendStopPacket(new ScriptStopSoundPacket(stockId, soundId, null), observer);
        activeSounds.remove(soundId);
    }

    public void clear(Entity observer) {
        stopAllForDisposal(observer, null, null);
    }

    /**
     * Stops all tracked sounds when a stock instance is disposed. Falls back to broadcast if the
     * entity is already invalid for {@code sendToObserving}.
     */
    public void stopAllForDisposal(Entity observer, World world, Vec3d position) {
        if (activeSounds.isEmpty()) {
            return;
        }

        sendStopPacket(new ScriptStopSoundPacket(stockId, null, null), observer, world, position);
        activeSounds.clear();
    }

    private void sendStopPacket(ScriptStopSoundPacket packet, Entity observer) {
        World world = observer != null ? observer.getWorld() : null;
        Vec3d position = observer != null ? observer.getPosition() : null;
        sendStopPacket(packet, observer, world, position);
    }

    private void sendStopPacket(ScriptStopSoundPacket packet, Entity observer, World world, Vec3d position) {
        if (observer != null && world != null && world.getEntity(stockId, Entity.class) != null) {
            packet.sendToObserving(observer);
            return;
        }
        if (world != null && position != null) {
            packet.sendToAllAround(world, position, 512.0D);
        } else {
            packet.sendToAll();
        }
    }
}
