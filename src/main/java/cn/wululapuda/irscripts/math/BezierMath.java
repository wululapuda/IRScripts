package cn.wululapuda.irscripts.math;

public final class BezierMath {
    private BezierMath() {
    }

    public static double defaultHandleLength(double[] start, double[] end) {
        return Vec3Math.distance(start, end) / 3.0D;
    }

    public static double[][] controlsFromEndpoints(
            double[] start,
            double[] startNormal,
            double[] end,
            double[] endNormal,
            double handleLength
    ) {
        double[] p0 = Vec3Math.copy(start);
        double[] p3 = Vec3Math.copy(end);
        double[] p1 = Vec3Math.add(p0, Vec3Math.mul(Vec3Math.normalize(startNormal), handleLength));
        double[] p2 = Vec3Math.sub(p3, Vec3Math.mul(Vec3Math.normalize(endNormal), handleLength));
        return new double[][] {p0, p1, p2, p3};
    }

    public static double[] cubic(double[] p0, double[] p1, double[] p2, double[] p3, double t) {
        double u = 1.0D - t;
        double uu = u * u;
        double tt = t * t;
        double uuu = uu * u;
        double ttt = tt * t;
        return new double[] {
                uuu * p0[0] + 3.0D * uu * t * p1[0] + 3.0D * u * tt * p2[0] + ttt * p3[0],
                uuu * p0[1] + 3.0D * uu * t * p1[1] + 3.0D * u * tt * p2[1] + ttt * p3[1],
                uuu * p0[2] + 3.0D * uu * t * p1[2] + 3.0D * u * tt * p2[2] + ttt * p3[2]
        };
    }

    public static double[] cubicTangent(double[] p0, double[] p1, double[] p2, double[] p3, double t) {
        double u = 1.0D - t;
        return new double[] {
                3.0D * u * u * (p1[0] - p0[0]) + 6.0D * u * t * (p2[0] - p1[0]) + 3.0D * t * t * (p3[0] - p2[0]),
                3.0D * u * u * (p1[1] - p0[1]) + 6.0D * u * t * (p2[1] - p1[1]) + 3.0D * t * t * (p3[1] - p2[1]),
                3.0D * u * u * (p1[2] - p0[2]) + 6.0D * u * t * (p2[2] - p1[2]) + 3.0D * t * t * (p3[2] - p2[2])
        };
    }

    public static double[] quadratic(double[] p0, double[] p1, double[] p2, double t) {
        double u = 1.0D - t;
        return new double[] {
                u * u * p0[0] + 2.0D * u * t * p1[0] + t * t * p2[0],
                u * u * p0[1] + 2.0D * u * t * p1[1] + t * t * p2[1],
                u * u * p0[2] + 2.0D * u * t * p1[2] + t * t * p2[2]
        };
    }

    public static double[] quadraticTangent(double[] p0, double[] p1, double[] p2, double t) {
        double u = 1.0D - t;
        return new double[] {
                2.0D * u * (p1[0] - p0[0]) + 2.0D * t * (p2[0] - p1[0]),
                2.0D * u * (p1[1] - p0[1]) + 2.0D * t * (p2[1] - p1[1]),
                2.0D * u * (p1[2] - p0[2]) + 2.0D * t * (p2[2] - p1[2])
        };
    }

    public static double[][] sampleCubic(double[] p0, double[] p1, double[] p2, double[] p3, int segments) {
        requirePositiveSegments(segments);
        double[][] points = new double[segments + 1][];
        for (int index = 0; index <= segments; index++) {
            double t = index / (double) segments;
            points[index] = cubic(p0, p1, p2, p3, t);
        }
        return points;
    }

    private static void requirePositiveSegments(int segments) {
        if (segments < 1) {
            throw new IllegalArgumentException("segments must be at least 1");
        }
    }
}
