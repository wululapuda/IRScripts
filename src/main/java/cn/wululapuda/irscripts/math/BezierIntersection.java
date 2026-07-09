package cn.wululapuda.irscripts.math;

import java.util.ArrayList;
import java.util.List;

/**
 * Finds intersection points between two cubic Bezier curves in world space.
 */
public final class BezierIntersection {
    private static final int DEFAULT_MAX_DEPTH = 14;

    private BezierIntersection() {
    }

    public static List<CurveIntersectionResult> findCubicCubic(
            double[] p0,
            double[] p1,
            double[] p2,
            double[] p3,
            double[] q0,
            double[] q1,
            double[] q2,
            double[] q3,
            double tolerance
    ) {
        return findCubicCubic(p0, p1, p2, p3, q0, q1, q2, q3, tolerance, DEFAULT_MAX_DEPTH);
    }

    public static List<CurveIntersectionResult> findCubicCubic(
            double[] p0,
            double[] p1,
            double[] p2,
            double[] p3,
            double[] q0,
            double[] q1,
            double[] q2,
            double[] q3,
            double tolerance,
            int maxDepth
    ) {
        if (tolerance <= 0.0D) {
            throw new IllegalArgumentException("tolerance must be positive");
        }
        if (maxDepth < 1) {
            throw new IllegalArgumentException("maxDepth must be at least 1");
        }
        List<CurveIntersectionResult> results = new ArrayList<>();
        intersectRecursive(
                p0, p1, p2, p3, 0.0D, 1.0D,
                q0, q1, q2, q3, 0.0D, 1.0D,
                tolerance,
                maxDepth,
                0,
                results
        );
        return results;
    }

    private static void intersectRecursive(
            double[] p0,
            double[] p1,
            double[] p2,
            double[] p3,
            double t1Start,
            double t1End,
            double[] q0,
            double[] q1,
            double[] q2,
            double[] q3,
            double t2Start,
            double t2End,
            double tolerance,
            int maxDepth,
            int depth,
            List<CurveIntersectionResult> results
    ) {
        if (!boundingBoxesOverlap(p0, p1, p2, p3, q0, q1, q2, q3, tolerance)) {
            return;
        }

        if (depth >= maxDepth || (isApproximatelyFlat(p0, p1, p2, p3, tolerance)
                && isApproximatelyFlat(q0, q1, q2, q3, tolerance))) {
            addSegmentIntersection(p0, p3, t1Start, t1End, q0, q3, t2Start, t2End, tolerance, results);
            return;
        }

        double[][] leftA = new double[4][];
        double[][] rightA = new double[4][];
        splitCubicAtHalf(p0, p1, p2, p3, leftA, rightA);
        double[][] leftB = new double[4][];
        double[][] rightB = new double[4][];
        splitCubicAtHalf(q0, q1, q2, q3, leftB, rightB);

        double t1Mid = (t1Start + t1End) * 0.5D;
        double t2Mid = (t2Start + t2End) * 0.5D;

        intersectRecursive(leftA[0], leftA[1], leftA[2], leftA[3], t1Start, t1Mid,
                leftB[0], leftB[1], leftB[2], leftB[3], t2Start, t2Mid,
                tolerance, maxDepth, depth + 1, results);
        intersectRecursive(leftA[0], leftA[1], leftA[2], leftA[3], t1Start, t1Mid,
                rightB[0], rightB[1], rightB[2], rightB[3], t2Mid, t2End,
                tolerance, maxDepth, depth + 1, results);
        intersectRecursive(rightA[0], rightA[1], rightA[2], rightA[3], t1Mid, t1End,
                leftB[0], leftB[1], leftB[2], leftB[3], t2Start, t2Mid,
                tolerance, maxDepth, depth + 1, results);
        intersectRecursive(rightA[0], rightA[1], rightA[2], rightA[3], t1Mid, t1End,
                rightB[0], rightB[1], rightB[2], rightB[3], t2Mid, t2End,
                tolerance, maxDepth, depth + 1, results);
    }

    private static void addSegmentIntersection(
            double[] a0,
            double[] a1,
            double t1Start,
            double t1End,
            double[] b0,
            double[] b1,
            double t2Start,
            double t2End,
            double tolerance,
            List<CurveIntersectionResult> results
    ) {
        SegmentClosest closest = closestPointsOnSegments(a0, a1, b0, b1);
        if (closest.distance > tolerance) {
            return;
        }

        double t1 = lerp(t1Start, t1End, closest.s);
        double t2 = lerp(t2Start, t2End, closest.t);
        double[] position = Vec3Math.mul(Vec3Math.add(closest.pointA, closest.pointB), 0.5D);
        addUniqueResult(position, t1, t2, tolerance, results);
    }

    private static void addUniqueResult(
            double[] position,
            double t1,
            double t2,
            double tolerance,
            List<CurveIntersectionResult> results
    ) {
        for (CurveIntersectionResult existing : results) {
            if (Vec3Math.distance(existing.getPosition(), position) <= tolerance * 2.0D) {
                return;
            }
        }
        results.add(new CurveIntersectionResult(position, clamp01(t1), clamp01(t2)));
    }

    private static boolean boundingBoxesOverlap(
            double[] p0,
            double[] p1,
            double[] p2,
            double[] p3,
            double[] q0,
            double[] q1,
            double[] q2,
            double[] q3,
            double tolerance
    ) {
        double[] aMin = componentMin(p0, p1, p2, p3);
        double[] aMax = componentMax(p0, p1, p2, p3);
        double[] bMin = componentMin(q0, q1, q2, q3);
        double[] bMax = componentMax(q0, q1, q2, q3);
        return aMin[0] - tolerance <= bMax[0] && aMax[0] + tolerance >= bMin[0]
                && aMin[1] - tolerance <= bMax[1] && aMax[1] + tolerance >= bMin[1]
                && aMin[2] - tolerance <= bMax[2] && aMax[2] + tolerance >= bMin[2];
    }

    private static boolean isApproximatelyFlat(double[] p0, double[] p1, double[] p2, double[] p3, double tolerance) {
        double chord = Vec3Math.distance(p0, p3);
        double flatTolerance = Math.max(tolerance, chord * 0.05D);
        return distancePointToSegment(p1, p0, p3) <= flatTolerance
                && distancePointToSegment(p2, p0, p3) <= flatTolerance;
    }

    private static double distancePointToSegment(double[] point, double[] start, double[] end) {
        SegmentClosest closest = closestPointsOnSegments(start, end, point, point);
        return Vec3Math.distance(point, closest.pointA);
    }

    private static void splitCubicAtHalf(
            double[] p0,
            double[] p1,
            double[] p2,
            double[] p3,
            double[][] left,
            double[][] right
    ) {
        double[] p01 = Vec3Math.lerp(p0, p1, 0.5D);
        double[] p12 = Vec3Math.lerp(p1, p2, 0.5D);
        double[] p23 = Vec3Math.lerp(p2, p3, 0.5D);
        double[] p012 = Vec3Math.lerp(p01, p12, 0.5D);
        double[] p123 = Vec3Math.lerp(p12, p23, 0.5D);
        double[] p0123 = Vec3Math.lerp(p012, p123, 0.5D);

        left[0] = Vec3Math.copy(p0);
        left[1] = p01;
        left[2] = p012;
        left[3] = p0123;
        right[0] = p0123;
        right[1] = p123;
        right[2] = p23;
        right[3] = Vec3Math.copy(p3);
    }

    private static SegmentClosest closestPointsOnSegments(double[] a0, double[] a1, double[] b0, double[] b1) {
        double[] d1 = Vec3Math.sub(a1, a0);
        double[] d2 = Vec3Math.sub(b1, b0);
        double[] r = Vec3Math.sub(a0, b0);

        double a = Vec3Math.dot(d1, d1);
        double e = Vec3Math.dot(d2, d2);
        double f = Vec3Math.dot(d2, r);

        double s;
        double t;

        if (a <= 1.0E-12D && e <= 1.0E-12D) {
            s = 0.0D;
            t = 0.0D;
        } else if (a <= 1.0E-12D) {
            s = 0.0D;
            t = clamp01(f / e);
        } else {
            double c = Vec3Math.dot(d1, r);
            if (e <= 1.0E-12D) {
                t = 0.0D;
                s = clamp01(-c / a);
            } else {
                double b = Vec3Math.dot(d1, d2);
                double denom = a * e - b * b;
                s = denom != 0.0D ? clamp01((b * f - c * e) / denom) : 0.0D;
                double tNom = b * s + f;
                if (tNom < 0.0D) {
                    t = 0.0D;
                    s = clamp01(-c / a);
                } else if (tNom > e) {
                    t = 1.0D;
                    s = clamp01((b - c) / a);
                } else {
                    t = clamp01(tNom / e);
                }
            }
        }

        double[] pointA = Vec3Math.lerp(a0, a1, s);
        double[] pointB = Vec3Math.lerp(b0, b1, t);
        return new SegmentClosest(s, t, pointA, pointB, Vec3Math.distance(pointA, pointB));
    }

    private static double[] componentMin(double[] a, double[] b, double[] c, double[] d) {
        return new double[] {
                Math.min(Math.min(a[0], b[0]), Math.min(c[0], d[0])),
                Math.min(Math.min(a[1], b[1]), Math.min(c[1], d[1])),
                Math.min(Math.min(a[2], b[2]), Math.min(c[2], d[2]))
        };
    }

    private static double[] componentMax(double[] a, double[] b, double[] c, double[] d) {
        return new double[] {
                Math.max(Math.max(a[0], b[0]), Math.max(c[0], d[0])),
                Math.max(Math.max(a[1], b[1]), Math.max(c[1], d[1])),
                Math.max(Math.max(a[2], b[2]), Math.max(c[2], d[2]))
        };
    }

    private static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }

    private static double clamp01(double value) {
        if (value < 0.0D) {
            return 0.0D;
        }
        if (value > 1.0D) {
            return 1.0D;
        }
        return value;
    }

    private static final class SegmentClosest {
        private final double s;
        private final double t;
        private final double[] pointA;
        private final double[] pointB;
        private final double distance;

        private SegmentClosest(double s, double t, double[] pointA, double[] pointB, double distance) {
            this.s = s;
            this.t = t;
            this.pointA = pointA;
            this.pointB = pointB;
            this.distance = distance;
        }
    }
}
