package cn.wululapuda.irscripts.track;

public final class TrackDataSnapshot {
    private final String type;
    private final String trackId;
    private final double gauge;
    private final double gaugeScale;
    private final boolean blueprint;
    private final boolean usesNative;
    private final String scriptPath;
    private final Double curvosity;
    private final String smoothing;
    private final String direction;
    private final String switchDirection;
    private final String switchForced;
    private final int[] placement;
    private final TrackEndpointSnapshot start;
    private final TrackEndpointSnapshot end;
    private final TrackEndpointSnapshot branch1;
    private final TrackEndpointSnapshot branch2;
    private final double[][] controls;

    public TrackDataSnapshot(
            String type,
            String trackId,
            double gauge,
            double gaugeScale,
            boolean blueprint,
            boolean usesNative,
            String scriptPath,
            Double curvosity,
            String smoothing,
            String direction,
            String switchDirection,
            String switchForced,
            int[] placement,
            TrackEndpointSnapshot start,
            TrackEndpointSnapshot end,
            TrackEndpointSnapshot branch1,
            TrackEndpointSnapshot branch2,
            double[][] controls
    ) {
        this.type = type;
        this.trackId = trackId;
        this.gauge = gauge;
        this.gaugeScale = gaugeScale;
        this.blueprint = blueprint;
        this.usesNative = usesNative;
        this.scriptPath = scriptPath;
        this.curvosity = curvosity;
        this.smoothing = smoothing;
        this.direction = direction;
        this.switchDirection = switchDirection;
        this.switchForced = switchForced;
        this.placement = copy3(placement);
        this.start = start;
        this.end = end;
        this.branch1 = branch1;
        this.branch2 = branch2;
        this.controls = controls;
    }

    public String getType() {
        return type;
    }

    public String getTrackId() {
        return trackId;
    }

    public double getGauge() {
        return gauge;
    }

    public double getGaugeScale() {
        return gaugeScale;
    }

    public boolean isBlueprint() {
        return blueprint;
    }

    public boolean isUsesNative() {
        return usesNative;
    }

    public String getScriptPath() {
        return scriptPath;
    }

    public Double getCurvosity() {
        return curvosity;
    }

    public String getSmoothing() {
        return smoothing;
    }

    /** Curve handedness: NONE, RIGHT, or LEFT. */
    public String getDirection() {
        return direction;
    }

    /** Active switch routing: NONE, STRAIGHT (direction 1), or TURN (direction 2). */
    public String getSwitchDirection() {
        return switchDirection;
    }

    /** Forced switch lock: NONE, STRAIGHT, or TURN. */
    public String getSwitchForced() {
        return switchForced;
    }

    /** Parent tile block position where the track was placed. */
    public int[] getPlacement() {
        return copy3(placement);
    }

    public TrackEndpointSnapshot getStart() {
        return start;
    }

    public TrackEndpointSnapshot getEnd() {
        return end;
    }

    /** Switch straight branch endpoint (direction 1). */
    public TrackEndpointSnapshot getBranch1() {
        return branch1;
    }

    /** Switch turn branch endpoint (direction 2). */
    public TrackEndpointSnapshot getBranch2() {
        return branch2;
    }

    public double[][] getControls() {
        return controls;
    }

    private static int[] copy3(int[] values) {
        return new int[] {values[0], values[1], values[2]};
    }
}
