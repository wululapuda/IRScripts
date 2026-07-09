package cn.wululapuda.irscripts.api;

import cn.wululapuda.irscripts.math.BezierIntersection;
import cn.wululapuda.irscripts.math.BezierMath;
import cn.wululapuda.irscripts.math.CurveDefinition;
import cn.wululapuda.irscripts.math.CurveHandleRegistry;
import cn.wululapuda.irscripts.math.CurveIntersectionResult;
import cn.wululapuda.irscripts.math.MathArrayUtil;
import cn.wululapuda.irscripts.math.Vec3Math;
import cn.wululapuda.irscripts.util.ScriptLog;

import java.util.List;

/**
 * Curve helpers for script-level geometry. Used by {@code model.mesh.group.adaptcurve}.
 */
public final class MathCurveApi {
    private final CurveHandleRegistry registry;

    public MathCurveApi(CurveHandleRegistry registry) {
        this.registry = registry;
    }

    /** Samples a cubic curve defined by two endpoints and their normals. */
    public double[] bezierEndpoints(Object start, Object startNormal, Object end, Object endNormal, double t) {
        return bezierEndpoints(start, startNormal, end, endNormal, t, -1.0D);
    }

    public double[] bezierEndpoints(
            Object start,
            Object startNormal,
            Object end,
            Object endNormal,
            double t,
            double handleLength
    ) {
        try {
            double[][] controls = resolveEndpointControls(start, startNormal, end, endNormal, handleLength);
            return BezierMath.cubic(controls[0], controls[1], controls[2], controls[3], t);
        } catch (IllegalArgumentException error) {
            ScriptLog.apiWarn("math.curve", "bezierEndpoints", error.getMessage());
            return new double[] {0.0D, 0.0D, 0.0D};
        }
    }

    public double[] bezierEndpointsTangent(Object start, Object startNormal, Object end, Object endNormal, double t) {
        return bezierEndpointsTangent(start, startNormal, end, endNormal, t, -1.0D);
    }

    public double[] bezierEndpointsTangent(
            Object start,
            Object startNormal,
            Object end,
            Object endNormal,
            double t,
            double handleLength
    ) {
        try {
            double[][] controls = resolveEndpointControls(start, startNormal, end, endNormal, handleLength);
            return BezierMath.cubicTangent(controls[0], controls[1], controls[2], controls[3], t);
        } catch (IllegalArgumentException error) {
            ScriptLog.apiWarn("math.curve", "bezierEndpointsTangent", error.getMessage());
            return new double[] {0.0D, 0.0D, 0.0D};
        }
    }

    public double[][] bezierEndpointsControls(
            Object start,
            Object startNormal,
            Object end,
            Object endNormal
    ) {
        return bezierEndpointsControls(start, startNormal, end, endNormal, -1.0D);
    }

    public double[][] bezierEndpointsControls(
            Object start,
            Object startNormal,
            Object end,
            Object endNormal,
            double handleLength
    ) {
        try {
            return resolveEndpointControls(start, startNormal, end, endNormal, handleLength);
        } catch (IllegalArgumentException error) {
            ScriptLog.apiWarn("math.curve", "bezierEndpointsControls", error.getMessage());
            return new double[][] {
                    {0.0D, 0.0D, 0.0D},
                    {0.0D, 0.0D, 0.0D},
                    {0.0D, 0.0D, 0.0D},
                    {0.0D, 0.0D, 0.0D}
            };
        }
    }

    public double[] cubic(Object p0, Object p1, Object p2, Object p3, double t) {
        try {
            return BezierMath.cubic(
                    MathArrayUtil.parseVec3(p0, "p0"),
                    MathArrayUtil.parseVec3(p1, "p1"),
                    MathArrayUtil.parseVec3(p2, "p2"),
                    MathArrayUtil.parseVec3(p3, "p3"),
                    t
            );
        } catch (IllegalArgumentException error) {
            ScriptLog.apiWarn("math.curve", "cubic", error.getMessage());
            return new double[] {0.0D, 0.0D, 0.0D};
        }
    }

    public double[] cubicTangent(Object p0, Object p1, Object p2, Object p3, double t) {
        try {
            return BezierMath.cubicTangent(
                    MathArrayUtil.parseVec3(p0, "p0"),
                    MathArrayUtil.parseVec3(p1, "p1"),
                    MathArrayUtil.parseVec3(p2, "p2"),
                    MathArrayUtil.parseVec3(p3, "p3"),
                    t
            );
        } catch (IllegalArgumentException error) {
            ScriptLog.apiWarn("math.curve", "cubicTangent", error.getMessage());
            return new double[] {0.0D, 0.0D, 0.0D};
        }
    }

    public double[] quadratic(Object p0, Object p1, Object p2, double t) {
        try {
            return BezierMath.quadratic(
                    MathArrayUtil.parseVec3(p0, "p0"),
                    MathArrayUtil.parseVec3(p1, "p1"),
                    MathArrayUtil.parseVec3(p2, "p2"),
                    t
            );
        } catch (IllegalArgumentException error) {
            ScriptLog.apiWarn("math.curve", "quadratic", error.getMessage());
            return new double[] {0.0D, 0.0D, 0.0D};
        }
    }

    public String createEndpoints(Object start, Object startNormal, Object end, Object endNormal) {
        return createEndpoints(start, startNormal, end, endNormal, -1.0D);
    }

    public String createEndpoints(
            Object start,
            Object startNormal,
            Object end,
            Object endNormal,
            double handleLength
    ) {
        try {
            double[][] controls = resolveEndpointControls(start, startNormal, end, endNormal, handleLength);
            return registerControls(controls);
        } catch (IllegalArgumentException error) {
            ScriptLog.apiWarn("math.curve", "createEndpoints", error.getMessage());
            return "";
        }
    }

    public String createCubic(Object p0, Object p1, Object p2, Object p3) {
        try {
            return registerControls(new double[][] {
                    MathArrayUtil.parseVec3(p0, "p0"),
                    MathArrayUtil.parseVec3(p1, "p1"),
                    MathArrayUtil.parseVec3(p2, "p2"),
                    MathArrayUtil.parseVec3(p3, "p3")
            });
        } catch (IllegalArgumentException error) {
            ScriptLog.apiWarn("math.curve", "createCubic", error.getMessage());
            return "";
        }
    }

    public double[] at(String curveId, double t) {
        try {
            return registry.require(curveId).at(t);
        } catch (IllegalArgumentException error) {
            ScriptLog.apiWarn("math.curve", "at", error.getMessage());
            return new double[] {0.0D, 0.0D, 0.0D};
        }
    }

    public double[] tangent(String curveId, double t) {
        try {
            return registry.require(curveId).tangent(t);
        } catch (IllegalArgumentException error) {
            ScriptLog.apiWarn("math.curve", "tangent", error.getMessage());
            return new double[] {0.0D, 0.0D, 0.0D};
        }
    }

    public double[][] sample(String curveId, int segments) {
        try {
            return registry.require(curveId).sample(segments);
        } catch (IllegalArgumentException error) {
            ScriptLog.apiWarn("math.curve", "sample", error.getMessage());
            return new double[][] {{0.0D, 0.0D, 0.0D}};
        }
    }

    public double[][] controls(String curveId) {
        try {
            return registry.require(curveId).controls();
        } catch (IllegalArgumentException error) {
            ScriptLog.apiWarn("math.curve", "controls", error.getMessage());
            return new double[][] {
                    {0.0D, 0.0D, 0.0D},
                    {0.0D, 0.0D, 0.0D},
                    {0.0D, 0.0D, 0.0D},
                    {0.0D, 0.0D, 0.0D}
            };
        }
    }

    /**
     * Finds all intersection points between two cubic curve handles.
     * Returns an array of results with world position and parameters on each curve.
     */
    public CurveIntersectionResult[] intersections(String curveId1, String curveId2) {
        return intersections(curveId1, curveId2, 0.05D);
    }

    public CurveIntersectionResult[] intersections(String curveId1, String curveId2, double tolerance) {
        try {
            CurveDefinition first = registry.require(curveId1);
            CurveDefinition second = registry.require(curveId2);
            List<CurveIntersectionResult> results = BezierIntersection.findCubicCubic(
                    first.p0, first.p1, first.p2, first.p3,
                    second.p0, second.p1, second.p2, second.p3,
                    tolerance
            );
            return results.toArray(new CurveIntersectionResult[0]);
        } catch (IllegalArgumentException error) {
            ScriptLog.apiWarn("math.curve", "intersections", error.getMessage());
            return new CurveIntersectionResult[0];
        }
    }

    private String registerControls(double[][] controls) {
        CurveDefinition curve = CurveDefinition.fromControls(registry.nextId(), controls);
        registry.register(curve);
        return curve.id;
    }

    private double[][] resolveEndpointControls(
            Object start,
            Object startNormal,
            Object end,
            Object endNormal,
            double handleLength
    ) {
        double[] p0 = MathArrayUtil.parseVec3(start, "start");
        double[] n0 = MathArrayUtil.parseVec3(startNormal, "startNormal");
        double[] p3 = MathArrayUtil.parseVec3(end, "end");
        double[] n3 = MathArrayUtil.parseVec3(endNormal, "endNormal");
        double length = handleLength > 0.0D ? handleLength : BezierMath.defaultHandleLength(p0, p3);
        return BezierMath.controlsFromEndpoints(p0, n0, p3, n3, length);
    }
}
