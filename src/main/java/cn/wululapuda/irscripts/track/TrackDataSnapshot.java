package cn.wululapuda.irscripts.track;

public final class TrackDataSnapshot {
    private final String type;
    private final double gauge;
    private final double gaugeScale;
    private final boolean blueprint;
    private final Double curvosity;
    private final String smoothing;
    private final String direction;
    private final TrackEndpointSnapshot start;
    private final TrackEndpointSnapshot end;
    private final TrackEndpointSnapshot branch1;
    private final TrackEndpointSnapshot branch2;
    private final double[][] controls;

    public TrackDataSnapshot(
            String type,
            double gauge,
            double gaugeScale,
            boolean blueprint,
            Double curvosity,
            String smoothing,
            String direction,
            TrackEndpointSnapshot start,
            TrackEndpointSnapshot end,
            TrackEndpointSnapshot branch1,
            TrackEndpointSnapshot branch2,
            double[][] controls
    ) {
        this.type = type;
        this.gauge = gauge;
        this.gaugeScale = gaugeScale;
        this.blueprint = blueprint;
        this.curvosity = curvosity;
        this.smoothing = smoothing;
        this.direction = direction;
        this.start = start;
        this.end = end;
        this.branch1 = branch1;
        this.branch2 = branch2;
        this.controls = controls;
    }

    public String getType() {
        return type;
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

    public Double getCurvosity() {
        return curvosity;
    }

    public String getSmoothing() {
        return smoothing;
    }

    public String getDirection() {
        return direction;
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
}
