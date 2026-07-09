package cn.wululapuda.irscripts.math;

import cn.wululapuda.irscripts.api.ParticleVecUtil;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Scriptable;

import java.util.ArrayList;
import java.util.List;

public final class MathArrayUtil {
    private MathArrayUtil() {
    }

    public static double[] parseVec3(Object value, String argumentName) {
        return ParticleVecUtil.parseVec3(value, argumentName);
    }

    public static List<Double> parseNumberList(Object value, String argumentName) {
        if (value == null) {
            throw new IllegalArgumentException(argumentName + " cannot be null");
        }
        List<Double> result = new ArrayList<>();
        if (value instanceof NativeArray) {
            NativeArray array = (NativeArray) value;
            int length = toIntLength(array.getLength());
            for (int index = 0; index < length; index++) {
                result.add(requireNumber(array.get(index, array), argumentName + "[" + index + "]"));
            }
            return result;
        }
        if (value instanceof Scriptable) {
            Scriptable scriptable = (Scriptable) value;
            Object lengthValue = scriptable.get("length", scriptable);
            if (lengthValue instanceof Number) {
                int length = ((Number) lengthValue).intValue();
                for (int index = 0; index < length; index++) {
                    Object element = scriptable.get(index, scriptable);
                    result.add(requireNumber(element, argumentName + "[" + index + "]"));
                }
                return result;
            }
        }
        if (value instanceof double[]) {
            for (double entry : (double[]) value) {
                result.add(entry);
            }
            return result;
        }
        if (value instanceof float[]) {
            for (float entry : (float[]) value) {
                result.add((double) entry);
            }
            return result;
        }
        if (value instanceof List) {
            for (Object entry : (List<?>) value) {
                result.add(requireNumber(entry, argumentName));
            }
            return result;
        }
        throw new IllegalArgumentException(argumentName + " must be a numeric array");
    }

    public static double[] toArray(List<Double> values) {
        double[] result = new double[values.size()];
        for (int index = 0; index < values.size(); index++) {
            result[index] = values.get(index);
        }
        return result;
    }

    private static double requireNumber(Object value, String argumentName) {
        if (value == null || value == Scriptable.NOT_FOUND) {
            throw new IllegalArgumentException(argumentName + " is missing");
        }
        return Context.toNumber(value);
    }

    private static int toIntLength(long length) {
        if (length > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int) length;
    }
}
