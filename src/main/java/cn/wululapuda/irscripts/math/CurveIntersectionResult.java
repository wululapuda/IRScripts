package cn.wululapuda.irscripts.math;

import java.util.ArrayList;
import java.util.List;

public final class CurveIntersectionResult {
    private final double[] position;
    private final double t1;
    private final double t2;

    public CurveIntersectionResult(double[] position, double t1, double t2) {
        this.position = Vec3Math.copy(position);
        this.t1 = t1;
        this.t2 = t2;
    }

    public double[] getPosition() {
        return Vec3Math.copy(position);
    }

    public double getT1() {
        return t1;
    }

    public double getT2() {
        return t2;
    }
}
