package cn.wululapuda.irscripts.math;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CurveHandleRegistry {
    private final Map<String, CurveDefinition> curves = new ConcurrentHashMap<>();

    public void register(CurveDefinition curve) {
        curves.put(curve.id, curve);
    }

    public CurveDefinition require(String handleId) {
        CurveDefinition curve = curves.get(handleId);
        if (curve == null) {
            throw new IllegalArgumentException("Unknown curve handle: " + handleId);
        }
        return curve;
    }

    public boolean isCurveHandle(String handleId) {
        return curves.containsKey(handleId);
    }

    public String nextId() {
        return "curve:" + UUID.randomUUID();
    }

    public void clear() {
        curves.clear();
    }
}
