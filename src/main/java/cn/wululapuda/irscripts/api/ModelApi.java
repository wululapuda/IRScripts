package cn.wululapuda.irscripts.api;

import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cam72cam.mod.entity.Entity;
import cn.wululapuda.irscripts.math.CurveHandleRegistry;
import cn.wululapuda.irscripts.model.ModelCoordinateSystem;
import cn.wululapuda.irscripts.model.ModelDataHandle;
import cn.wululapuda.irscripts.model.ModelHandleRegistry;
import cn.wululapuda.irscripts.model.ModelLoader;
import cn.wululapuda.irscripts.model.ModelPathUtil;
import cn.wululapuda.irscripts.model.ModelVecUtil;
import cn.wululapuda.irscripts.util.ScriptLog;
import cam72cam.mod.model.obj.OBJModel;
import cam72cam.mod.resource.Identifier;

/**
 * Root-level script API exposed as {@code model} (same scope as {@code stock}).
 */
public final class ModelApi {
    private final EntityRollingStock stock;
    private final ModelHandleRegistry registry;
    private final ScriptModelTracker tracker;
    private final ModelCoordinateSystem coordinates = new ModelCoordinateSystem();
    private final ModelMeshApi mesh;
    private final ModelRenderApi render;

    public ModelApi(
            EntityRollingStock stock,
            ModelHandleRegistry registry,
            CurveHandleRegistry curveRegistry,
            ScriptModelTracker tracker
    ) {
        this.stock = stock;
        this.registry = registry;
        this.tracker = tracker;
        this.mesh = new ModelMeshApi(registry, curveRegistry, coordinates);
        this.render = new ModelRenderApi(stock, registry, tracker, coordinates);
    }

    /** Sets the world-coordinate anchor for the entire rendering framework. */
    public void setCenter(Object xyz) {
        try {
            coordinates.setCenter(ModelVecUtil.parseVec3(xyz, "center"));
        } catch (IllegalArgumentException error) {
            ScriptLog.apiWarn("model", "setCenter", error.getMessage());
        }
    }

    public void setNormal(Object xyz) {
        try {
            coordinates.setNormal(ModelVecUtil.parseVec3(xyz, "normal"));
        } catch (IllegalArgumentException error) {
            ScriptLog.apiWarn("model", "setNormal", error.getMessage());
        }
    }

    /** Alias for scripts that follow the draft spelling. */
    public void setNormol(Object xyz) {
        setNormal(xyz);
    }

    public String load(String path) {
        if (!requireServer("load")) {
            return "";
        }
        if (path == null || path.trim().isEmpty()) {
            ScriptLog.apiWarn("model", "load", "path is empty");
            return "";
        }
        try {
            Identifier identifier = ModelPathUtil.resolve(stock, path);
            double scale = stock.gauge != null ? stock.gauge.scale() : 1.0D;
            OBJModel objModel = ModelLoader.load(identifier, scale);
            ModelDataHandle handle = new ModelDataHandle(identifier.getDomain() + ":" + identifier.getPath(), objModel);
            registry.register(handle);
            ScriptLog.modelLoaded(stock.getUUID(), handle.id, identifier.toString());
            return handle.id;
        } catch (Exception error) {
            ScriptLog.apiWarn("model", "load", "failed to load " + path + ": " + error.getMessage());
            return "";
        }
    }

    public ModelMeshApi getMesh() {
        return mesh;
    }

    public ModelRenderApi getRender() {
        return render;
    }

    private boolean requireServer(String action) {
        if (stock.getWorld().isServer) {
            return true;
        }
        ScriptLog.apiIgnored("model", action, "server-side only");
        return false;
    }
}
