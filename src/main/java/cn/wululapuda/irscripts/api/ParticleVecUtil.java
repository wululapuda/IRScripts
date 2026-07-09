package cn.wululapuda.irscripts.api;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Scriptable;

/**
 * Parses {@code [x, y, z]} arrays passed from Rhino scripts into model-space vectors.
 */
public final class ParticleVecUtil {
    private ParticleVecUtil() {
    }

    public static double[] parseVec3(Object value, String argumentName) {
        if (value instanceof double[]) {
            return requireLength((double[]) value, argumentName);
        }
        if (value instanceof float[]) {
            float[] floats = (float[]) value;
            if (floats.length < 3) {
                throw new IllegalArgumentException(argumentName + " must contain at least 3 elements [x, y, z]");
            }
            return new double[] {floats[0], floats[1], floats[2]};
        }
        if (value instanceof NativeArray) {
            return parseIndexed(value, toIntLength(((NativeArray) value).getLength()), argumentName);
        }
        if (value instanceof Scriptable) {
            Scriptable scriptable = (Scriptable) value;
            Object lengthValue = scriptable.get("length", scriptable);
            if (lengthValue instanceof Number) {
                return parseIndexed(scriptable, ((Number) lengthValue).intValue(), argumentName);
            }
        }
        throw new IllegalArgumentException(argumentName + " must be a 3-element array [x, y, z]");
    }

    private static double[] parseIndexed(Object container, int length, String argumentName) {
        if (length < 3) {
            throw new IllegalArgumentException(argumentName + " must contain at least 3 elements [x, y, z]");
        }
        double[] result = new double[3];
        for (int index = 0; index < 3; index++) {
            Object element;
            if (container instanceof NativeArray) {
                element = ((NativeArray) container).get(index, (NativeArray) container);
            } else {
                element = ((Scriptable) container).get(index, (Scriptable) container);
            }
            if (element == null || element == Scriptable.NOT_FOUND) {
                throw new IllegalArgumentException(argumentName + "[" + index + "] is missing");
            }
            result[index] = Context.toNumber(element);
        }
        return result;
    }

    private static double[] requireLength(double[] values, String argumentName) {
        if (values.length < 3) {
            throw new IllegalArgumentException(argumentName + " must contain at least 3 elements [x, y, z]");
        }
        return new double[] {values[0], values[1], values[2]};
    }

    private static int toIntLength(long length) {
        if (length > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int) length;
    }
}
