package cn.wululapuda.irscripts.api;

import cam72cam.mod.resource.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Estimates Ogg Vorbis duration from resource bytes (used by {@code utilPlay} on the server).
 */
public final class OggDurationUtil {
    private static final long FALLBACK_DURATION_MS = 1000L;
    private static final Map<String, Long> DURATION_CACHE = new ConcurrentHashMap<>();

    private OggDurationUtil() {
    }

    public static long getDurationMs(Identifier identifier) {
        if (identifier == null || !identifier.canLoad()) {
            return FALLBACK_DURATION_MS;
        }

        String cacheKey = identifier.toString();
        Long cached = DURATION_CACHE.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        long durationMs;
        try (InputStream input = identifier.getResourceStream()) {
            durationMs = parseDurationMs(input);
        } catch (IOException ex) {
            durationMs = FALLBACK_DURATION_MS;
        }

        DURATION_CACHE.put(cacheKey, durationMs);
        return durationMs;
    }

    static long parseDurationMs(InputStream input) throws IOException {
        byte[] buffer = readAll(input);
        if (buffer.length < 64) {
            return FALLBACK_DURATION_MS;
        }

        int sampleRate = readSampleRate(buffer);
        if (sampleRate <= 0) {
            return FALLBACK_DURATION_MS;
        }

        long granule = readLastGranule(buffer);
        if (granule <= 0L) {
            return FALLBACK_DURATION_MS;
        }

        return Math.max(1L, granule * 1000L / sampleRate);
    }

    private static byte[] readAll(InputStream input) throws IOException {
        byte[] data = new byte[Math.max(8192, input.available())];
        int total = 0;
        int read;
        while ((read = input.read(data, total, data.length - total)) >= 0) {
            total += read;
            if (total == data.length) {
                byte[] bigger = new byte[data.length * 2];
                System.arraycopy(data, 0, bigger, 0, data.length);
                data = bigger;
            }
            if (read == 0) {
                break;
            }
        }
        byte[] exact = new byte[total];
        System.arraycopy(data, 0, exact, 0, total);
        return exact;
    }

    private static int readSampleRate(byte[] data) {
        for (int i = 0; i + 44 < data.length; i++) {
            if (!isOggPage(data, i)) {
                continue;
            }
            int segmentCount = data[i + 26] & 0xFF;
            int headerEnd = i + 27 + segmentCount;
            if (headerEnd + 30 >= data.length) {
                continue;
            }
            if (data[headerEnd] != 0x01 || data[headerEnd + 1] != 'v' || data[headerEnd + 2] != 'o'
                    || data[headerEnd + 3] != 'r' || data[headerEnd + 4] != 'b' || data[headerEnd + 5] != 'i'
                    || data[headerEnd + 6] != 's') {
                continue;
            }
            return (data[headerEnd + 12] & 0xFF)
                    | ((data[headerEnd + 13] & 0xFF) << 8)
                    | ((data[headerEnd + 14] & 0xFF) << 16)
                    | ((data[headerEnd + 15] & 0xFF) << 24);
        }
        return -1;
    }

    private static long readLastGranule(byte[] data) {
        long last = -1L;
        for (int i = 0; i + 27 < data.length; i++) {
            if (!isOggPage(data, i)) {
                continue;
            }
            long granule = (data[i + 6] & 0xFFL)
                    | ((data[i + 7] & 0xFFL) << 8)
                    | ((data[i + 8] & 0xFFL) << 16)
                    | ((data[i + 9] & 0xFFL) << 24)
                    | ((data[i + 10] & 0xFFL) << 32)
                    | ((data[i + 11] & 0xFFL) << 40)
                    | ((data[i + 12] & 0xFFL) << 48)
                    | ((data[i + 13] & 0xFFL) << 56);
            if (granule >= 0L) {
                last = granule;
            }
            int segmentCount = data[i + 26] & 0xFF;
            int pageSize = 27 + segmentCount;
            for (int s = 0; s < segmentCount; s++) {
                pageSize += data[i + 27 + s] & 0xFF;
            }
            i += Math.max(pageSize - 1, 0);
        }
        return last;
    }

    private static boolean isOggPage(byte[] data, int offset) {
        return data[offset] == 'O'
                && data[offset + 1] == 'g'
                && data[offset + 2] == 'g'
                && data[offset + 3] == 'S';
    }
}
