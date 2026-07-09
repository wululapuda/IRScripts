package cn.wululapuda.irscripts.api;

import cn.wululapuda.irscripts.math.CurveHandleRegistry;
import cn.wululapuda.irscripts.math.MathArrayUtil;
import cn.wululapuda.irscripts.math.Vec3Math;
import cn.wululapuda.irscripts.util.ScriptLog;

import java.util.List;

/**
 * Root-level script API exposed as {@code math} (same scope as {@code stock} and {@code model}).
 * Provides numpy-inspired vector and array helpers plus curve sampling utilities.
 */
public final class MathApi {
    private final MathCurveApi curve;

    public MathApi(CurveHandleRegistry curveRegistry) {
        this.curve = new MathCurveApi(curveRegistry);
    }

    public double[] add(Object a, Object b) {
        return operate("add", () -> Vec3Math.add(parseVec3(a, "a"), parseVec3(b, "b")));
    }

    public double[] sub(Object a, Object b) {
        return operate("sub", () -> Vec3Math.sub(parseVec3(a, "a"), parseVec3(b, "b")));
    }

    public double[] mul(Object vector, double scalar) {
        return operate("mul", () -> Vec3Math.mul(parseVec3(vector, "vector"), scalar));
    }

    public double[] div(Object vector, double scalar) {
        return operate("div", () -> Vec3Math.div(parseVec3(vector, "vector"), scalar));
    }

    public double[] neg(Object vector) {
        return operate("neg", () -> Vec3Math.neg(parseVec3(vector, "vector")));
    }

    public double dot(Object a, Object b) {
        return operateScalar("dot", () -> Vec3Math.dot(parseVec3(a, "a"), parseVec3(b, "b")));
    }

    public double[] cross(Object a, Object b) {
        return operate("cross", () -> Vec3Math.cross(parseVec3(a, "a"), parseVec3(b, "b")));
    }

    public double length(Object vector) {
        return operateScalar("length", () -> Vec3Math.length(parseVec3(vector, "vector")));
    }

    public double[] normalize(Object vector) {
        return operate("normalize", () -> Vec3Math.normalize(parseVec3(vector, "vector")));
    }

    public double distance(Object a, Object b) {
        return operateScalar("distance", () -> Vec3Math.distance(parseVec3(a, "a"), parseVec3(b, "b")));
    }

    public double[] lerp(Object a, Object b, double t) {
        return operate("lerp", () -> Vec3Math.lerp(parseVec3(a, "a"), parseVec3(b, "b"), t));
    }

    public double[] project(Object vector, Object onto) {
        return operate("project", () -> Vec3Math.project(parseVec3(vector, "vector"), parseVec3(onto, "onto")));
    }

    public double angle(Object a, Object b) {
        return operateScalar("angle", () -> Vec3Math.angle(parseVec3(a, "a"), parseVec3(b, "b")));
    }

    public double clamp(double value, double min, double max) {
        if (min > max) {
            double tmp = min;
            min = max;
            max = tmp;
        }
        return Math.min(max, Math.max(min, value));
    }

    public double sign(double value) {
        if (value > 0.0D) {
            return 1.0D;
        }
        if (value < 0.0D) {
            return -1.0D;
        }
        return 0.0D;
    }

    public double degToRad(double degrees) {
        return degrees * Math.PI / 180.0D;
    }

    public double radToDeg(double radians) {
        return radians * 180.0D / Math.PI;
    }

    public double[] linspace(double start, double end, int count) {
        try {
            if (count < 2) {
                throw new IllegalArgumentException("count must be at least 2");
            }
            double[] values = new double[count];
            double step = (end - start) / (count - 1);
            for (int index = 0; index < count; index++) {
                values[index] = start + step * index;
            }
            return values;
        } catch (IllegalArgumentException error) {
            ScriptLog.apiWarn("math", "linspace", error.getMessage());
            return new double[] {start, end};
        }
    }

    public double[] arange(double start, double end, double step) {
        try {
            if (Math.abs(step) < 1.0E-12D) {
                throw new IllegalArgumentException("step cannot be zero");
            }
            int count = (int) Math.max(1, Math.ceil((end - start) / step));
            double[] values = new double[count];
            for (int index = 0; index < count; index++) {
                values[index] = start + step * index;
            }
            return values;
        } catch (IllegalArgumentException error) {
            ScriptLog.apiWarn("math", "arange", error.getMessage());
            return new double[] {start};
        }
    }

    public double sum(Object array) {
        return reduce("sum", array, 0.0D, (total, value) -> total + value);
    }

    public double min(Object array) {
        return reduce("min", array, Double.POSITIVE_INFINITY, Math::min);
    }

    public double max(Object array) {
        return reduce("max", array, Double.NEGATIVE_INFINITY, Math::max);
    }

    public double mean(Object array) {
        try {
            List<Double> values = MathArrayUtil.parseNumberList(array, "array");
            if (values.isEmpty()) {
                throw new IllegalArgumentException("array cannot be empty");
            }
            double total = 0.0D;
            for (double value : values) {
                total += value;
            }
            return total / values.size();
        } catch (IllegalArgumentException error) {
            ScriptLog.apiWarn("math", "mean", error.getMessage());
            return 0.0D;
        }
    }

    public MathCurveApi getCurve() {
        return curve;
    }

    private double[] operate(String action, VectorOperation operation) {
        try {
            return operation.run();
        } catch (IllegalArgumentException error) {
            ScriptLog.apiWarn("math", action, error.getMessage());
            return new double[] {0.0D, 0.0D, 0.0D};
        }
    }

    private double operateScalar(String action, ScalarOperation operation) {
        try {
            return operation.run();
        } catch (IllegalArgumentException error) {
            ScriptLog.apiWarn("math", action, error.getMessage());
            return 0.0D;
        }
    }

    private double reduce(String action, Object array, double seed, Reducer reducer) {
        try {
            List<Double> values = MathArrayUtil.parseNumberList(array, "array");
            if (values.isEmpty()) {
                throw new IllegalArgumentException("array cannot be empty");
            }
            double result = seed;
            for (double value : values) {
                result = reducer.apply(result, value);
            }
            return result;
        } catch (IllegalArgumentException error) {
            ScriptLog.apiWarn("math", action, error.getMessage());
            return 0.0D;
        }
    }

    private static double[] parseVec3(Object value, String argumentName) {
        return MathArrayUtil.parseVec3(value, argumentName);
    }

    @FunctionalInterface
    private interface VectorOperation {
        double[] run();
    }

    @FunctionalInterface
    private interface ScalarOperation {
        double run();
    }

    @FunctionalInterface
    private interface Reducer {
        double apply(double left, double right);
    }
}
