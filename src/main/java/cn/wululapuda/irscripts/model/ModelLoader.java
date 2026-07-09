package cn.wululapuda.irscripts.model;

import cam72cam.mod.model.obj.OBJModel;
import cam72cam.mod.resource.Identifier;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ModelLoader {
    private static final Map<String, OBJModel> CACHE = new ConcurrentHashMap<>();

    private ModelLoader() {
    }

    public static OBJModel load(Identifier modelLoc, double scale) throws Exception {
        String cacheKey = modelLoc.toString() + "@" + scale;
        OBJModel cached = CACHE.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        OBJModel model = new OBJModel(modelLoc, 0.0F, scale);
        CACHE.put(cacheKey, model);
        return model;
    }
}
