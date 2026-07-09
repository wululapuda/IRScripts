package cn.wululapuda.irscripts.api;

/**
 * Resolved parameters for a single script sound playback request.
 */
public final class SoundPlayRequest {
    private final String path;
    private final float volume;
    private final float pitch;
    private final boolean repeat;
    private final int maxDistance;

    public SoundPlayRequest(String path, float volume, float pitch, boolean repeat, int maxDistance) {
        this.path = path;
        this.volume = volume;
        this.pitch = pitch;
        this.repeat = repeat;
        this.maxDistance = maxDistance;
    }

    public String getPath() {
        return path;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public int getMaxDistance() {
        return maxDistance;
    }
}
