package cn.wululapuda.irscripts.net;

import cam72cam.mod.sound.ISound;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Client-side registry of script {@link ISound} instances for stopPlay support. */
public final class ScriptClientSoundManager {
    private static final Map<UUID, Map<UUID, ClientSound>> SOUNDS_BY_STOCK = new ConcurrentHashMap<>();

    private ScriptClientSoundManager() {
    }

    public static void register(UUID stockId, UUID soundId, String resolvedPath, ISound sound) {
        if (stockId == null || soundId == null || sound == null) {
            return;
        }
        SOUNDS_BY_STOCK.computeIfAbsent(stockId, ignored -> new ConcurrentHashMap<>())
                .put(soundId, new ClientSound(resolvedPath, sound));
    }

    public static void stop(UUID stockId, UUID soundId, String resolvedPath) {
        if (stockId == null) {
            return;
        }

        Map<UUID, ClientSound> sounds = SOUNDS_BY_STOCK.get(stockId);
        if (sounds == null || sounds.isEmpty()) {
            return;
        }

        if (soundId != null) {
            ClientSound removed = sounds.remove(soundId);
            stopSound(removed);
            cleanupStock(stockId, sounds);
            return;
        }

        Iterator<Map.Entry<UUID, ClientSound>> iterator = sounds.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, ClientSound> entry = iterator.next();
            if (resolvedPath == null || resolvedPath.isEmpty() || resolvedPath.equals(entry.getValue().resolvedPath)) {
                stopSound(entry.getValue());
                iterator.remove();
            }
        }
        cleanupStock(stockId, sounds);
    }

    /** Stops script sounds when the rolling stock entity is no longer present on the client. */
    public static void purgeMissingStocks(cam72cam.mod.world.World world) {
        if (world == null || SOUNDS_BY_STOCK.isEmpty()) {
            return;
        }

        for (UUID stockId : new java.util.ArrayList<>(SOUNDS_BY_STOCK.keySet())) {
            if (world.getEntity(stockId, cam72cam.immersiverailroading.entity.EntityRollingStock.class) == null) {
                stop(stockId, null, null);
            }
        }
    }

    private static void stopSound(ClientSound clientSound) {
        if (clientSound != null && clientSound.sound != null) {
            clientSound.sound.stop();
        }
    }

    private static void cleanupStock(UUID stockId, Map<UUID, ClientSound> sounds) {
        if (sounds.isEmpty()) {
            SOUNDS_BY_STOCK.remove(stockId);
        }
    }

    private static final class ClientSound {
        private final String resolvedPath;
        private final ISound sound;

        private ClientSound(String resolvedPath, ISound sound) {
            this.resolvedPath = resolvedPath;
            this.sound = sound;
        }
    }
}
