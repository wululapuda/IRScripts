package cn.wululapuda.irscripts.api;

import cn.wululapuda.irscripts.model.MeshDataHandle;
import cn.wululapuda.irscripts.model.ModelDataHandle;
import cn.wululapuda.irscripts.model.ModelHandleRegistry;
import cn.wululapuda.irscripts.model.ModelVecUtil;
import cn.wululapuda.irscripts.util.ScriptLog;

public final class ModelMeshOriginApi {
    private final ModelHandleRegistry registry;

    public ModelMeshOriginApi(ModelHandleRegistry registry) {
        this.registry = registry;
    }

    public double[] get(String meshData) {
        try {
            return registry.requireMesh(meshData).origin;
        } catch (IllegalArgumentException error) {
            ScriptLog.apiWarn("model.mesh.origin", "get", error.getMessage());
            return new double[] {0.0D, 0.0D, 0.0D};
        }
    }

    public String set(String meshData, Object xyz) {
        try {
            MeshDataHandle mesh = registry.requireMesh(meshData);
            MeshDataHandle updated = mesh.withOrigin(ModelVecUtil.parseVec3(xyz, "origin"));
            registry.register(updated);
            return updated.id;
        } catch (IllegalArgumentException error) {
            ScriptLog.apiWarn("model.mesh.origin", "set", error.getMessage());
            return "";
        }
    }

    public String setcenter(String meshData) {
        try {
            MeshDataHandle mesh = registry.requireMesh(meshData);
            ModelDataHandle model = registry.requireModel(mesh.modelId);
            MeshDataHandle updated = mesh.withOrigin(model.centerOf(mesh.groupName));
            registry.register(updated);
            return updated.id;
        } catch (IllegalArgumentException error) {
            ScriptLog.apiWarn("model.mesh.origin", "setcenter", error.getMessage());
            return "";
        }
    }
}
