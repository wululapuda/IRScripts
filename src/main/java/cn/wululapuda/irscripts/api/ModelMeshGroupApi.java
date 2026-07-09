package cn.wululapuda.irscripts.api;

import cn.wululapuda.irscripts.math.CurveHandleRegistry;
import cn.wululapuda.irscripts.model.MeshGroupHandle;
import cn.wululapuda.irscripts.model.ModelHandleRegistry;
import cn.wululapuda.irscripts.model.ModelVecUtil;
import cn.wululapuda.irscripts.util.ScriptLog;

import java.util.List;

public final class ModelMeshGroupApi {
    private final ModelHandleRegistry registry;
    private final CurveHandleRegistry curveRegistry;

    public ModelMeshGroupApi(ModelHandleRegistry registry, CurveHandleRegistry curveRegistry) {
        this.registry = registry;
        this.curveRegistry = curveRegistry;
    }

    public String create(Object startMeshes, Object middleMeshes, Object endMeshes) {
        try {
            List<String> start = ModelVecUtil.parseMeshHandleList(startMeshes, "startMeshes");
            List<String> middle = ModelVecUtil.parseMeshHandleList(middleMeshes, "middleMeshes");
            List<String> end = ModelVecUtil.parseMeshHandleList(endMeshes, "endMeshes");
            for (String meshId : start) {
                registry.requireMesh(meshId);
            }
            for (String meshId : middle) {
                registry.requireMesh(meshId);
            }
            for (String meshId : end) {
                registry.requireMesh(meshId);
            }
            MeshGroupHandle group = new MeshGroupHandle(start, middle, end, false);
            registry.register(group);
            return group.id;
        } catch (IllegalArgumentException error) {
            ScriptLog.apiWarn("model.mesh.group", "create", error.getMessage());
            return "";
        }
    }

    public void adaptcurve(String meshGroup, String curveId) {
        try {
            MeshGroupHandle group = registry.requireMeshGroup(meshGroup);
            curveRegistry.require(curveId);
            registry.register(group.withCurve(curveId));
            ScriptLog.modelCurveAdapted(meshGroup, curveId);
        } catch (IllegalArgumentException error) {
            ScriptLog.apiWarn("model.mesh.group", "adaptcurve", error.getMessage());
        }
    }
}
