package cn.wululapuda.irscripts.model;

import cn.wululapuda.irscripts.api.ParticleVecUtil;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Scriptable;

import java.util.ArrayList;
import java.util.List;

public final class ModelVecUtil {
    private ModelVecUtil() {
    }

    public static double[] parseVec3(Object value, String argumentName) {
        return ParticleVecUtil.parseVec3(value, argumentName);
    }

    public static List<String> parseStringList(Object value, String argumentName) {
        if (value == null) {
            throw new IllegalArgumentException(argumentName + " cannot be null");
        }
        List<String> result = new ArrayList<>();
        if (value instanceof NativeArray) {
            NativeArray array = (NativeArray) value;
            int length = toIntLength(array.getLength());
            for (int index = 0; index < length; index++) {
                result.add(requireString(array.get(index, array), argumentName + "[" + index + "]"));
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
                    result.add(requireString(element, argumentName + "[" + index + "]"));
                }
                return result;
            }
        }
        if (value instanceof String[]) {
            for (String entry : (String[]) value) {
                result.add(entry);
            }
            return result;
        }
        if (value instanceof List) {
            for (Object entry : (List<?>) value) {
                result.add(requireString(entry, argumentName));
            }
            return result;
        }
        throw new IllegalArgumentException(argumentName + " must be an array of mesh handles");
    }

    public static List<String> parseMeshHandleList(Object value, String argumentName) {
        return parseStringList(value, argumentName);
    }

    private static String requireString(Object value, String argumentName) {
        if (value == null || value == Scriptable.NOT_FOUND) {
            throw new IllegalArgumentException(argumentName + " is missing");
        }
        String text = Context.toString(value);
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException(argumentName + " cannot be empty");
        }
        return text.trim();
    }

    private static int toIntLength(long length) {
        if (length > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int) length;
    }
}
