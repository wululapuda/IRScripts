package cn.wululapuda.irscripts.track;

public final class TrackEndpointSnapshot {
    private final double[] position;
    private final double[] normal;

    public TrackEndpointSnapshot(double[] position, double[] normal) {
        this.position = copy3(position);
        this.normal = copy3(normal);
    }

    public double[] getPosition() {
        return copy3(position);
    }

    public double[] getNormal() {
        return copy3(normal);
    }

    private static double[] copy3(double[] values) {
        return new double[] {values[0], values[1], values[2]};
    }
}
