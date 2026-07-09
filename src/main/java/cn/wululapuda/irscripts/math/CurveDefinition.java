package cn.wululapuda.irscripts.math;

public final class CurveDefinition {
    public final String id;
    public final double[] p0;
    public final double[] p1;
    public final double[] p2;
    public final double[] p3;

    public CurveDefinition(String id, double[] p0, double[] p1, double[] p2, double[] p3) {
        this.id = id;
        this.p0 = Vec3Math.copy(p0);
        this.p1 = Vec3Math.copy(p1);
        this.p2 = Vec3Math.copy(p2);
        this.p3 = Vec3Math.copy(p3);
    }

    public static CurveDefinition fromControls(String id, double[][] controls) {
        return new CurveDefinition(id, controls[0], controls[1], controls[2], controls[3]);
    }

    public double[] at(double t) {
        return BezierMath.cubic(p0, p1, p2, p3, clampUnit(t));
    }

    public double[] tangent(double t) {
        return BezierMath.cubicTangent(p0, p1, p2, p3, clampUnit(t));
    }

    public double[][] controls() {
        return new double[][] {
                Vec3Math.copy(p0),
                Vec3Math.copy(p1),
                Vec3Math.copy(p2),
                Vec3Math.copy(p3)
        };
    }

    public double[][] sample(int segments) {
        return BezierMath.sampleCubic(p0, p1, p2, p3, segments);
    }

    private static double clampUnit(double t) {
        if (t < 0.0D) {
            return 0.0D;
        }
        if (t > 1.0D) {
            return 1.0D;
        }
        return t;
    }
}
