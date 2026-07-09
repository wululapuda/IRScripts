package cn.wululapuda.irscripts.api;

import cn.wululapuda.irscripts.math.CurveHandleRegistry;
import cn.wululapuda.irscripts.model.MeshDataHandle;
import cn.wululapuda.irscripts.model.MeshGroupHandle;
import cn.wululapuda.irscripts.model.ModelCoordinateSystem;
import cn.wululapuda.irscripts.model.ModelDataHandle;
import cn.wululapuda.irscripts.model.ModelHandleRegistry;
import cn.wululapuda.irscripts.model.ModelVecUtil;
import cn.wululapuda.irscripts.util.ScriptLog;

import java.util.ArrayList;
import java.util.List;

public final class ModelMeshApi {
    private final ModelHandleRegistry registry;
    private final ModelMeshOriginApi origin;
    private final ModelMeshGroupApi group;

    public ModelMeshApi(
            ModelHandleRegistry registry,
            CurveHandleRegistry curveRegistry,
            ModelCoordinateSystem coordinates
    ) {
        this.registry = registry;
        this.origin = new ModelMeshOriginApi(registry);
        this.group = new ModelMeshGroupApi(registry, curveRegistry);
    }

    public List<String> list(String modelData) {
        try {
            return registry.requireModel(modelData).listMeshes();
        } catch (IllegalArgumentException error) {
            ScriptLog.apiWarn("model.mesh", "list", error.getMessage());
            return new ArrayList<>();
        }
    }

    public String get(String modelData, String meshName) {
        try {
            ModelDataHandle model = registry.requireModel(modelData);
            model.requireGroup(meshName);
            MeshDataHandle mesh = new MeshDataHandle(
                    model.id,
                    meshName,
                    new double[] {0.0D, 0.0D, 0.0D},
                    new double[] {1.0D, 1.0D, 1.0D},
                    new double[] {0.0D, 0.0D, 0.0D},
                    new double[] {0.0D, 0.0D, 0.0D}
            );
            registry.register(mesh);
            return mesh.id;
        } catch (IllegalArgumentException error) {
            ScriptLog.apiWarn("model.mesh", "get", error.getMessage());
            return "";
        }
    }

    public String scaling(String meshData, Object scaleVec) {
        return transform(meshData, scaleVec, null, false);
    }

    public String scalingfrom(String meshData, Object scaleVec, Object pivotVec) {
        return transform(meshData, scaleVec, pivotVec, false);
    }

    public String rotate(String meshData, Object rotationVec) {
        return transform(meshData, rotationVec, null, true);
    }

    public String rotatefrom(String meshData, Object rotationVec, Object pivotVec) {
        return transform(meshData, rotationVec, pivotVec, true);
    }

    public String mirrorx(String meshData) {
        return mirror(meshData, 0);
    }

    public String mirrory(String meshData) {
        return mirror(meshData, 1);
    }

    public String mirrorz(String meshData) {
        return mirror(meshData, 2);
    }

    public ModelMeshOriginApi getOrigin() {
        return origin;
    }

    public ModelMeshGroupApi getGroup() {
        return group;
    }

    private String mirror(String meshData, int axis) {
        try {
            MeshDataHandle mesh = registry.requireMesh(meshData);
            double[] newScale = new double[] {mesh.scale[0], mesh.scale[1], mesh.scale[2]};
            newScale[axis] *= -1.0D;
            MeshDataHandle updated = mesh.withScale(newScale);
            registry.register(updated);
            return updated.id;
        } catch (IllegalArgumentException error) {
            ScriptLog.apiWarn("model.mesh", axisName(axis), error.getMessage());
            return "";
        }
    }

    private static String axisName(int axis) {
        if (axis == 0) {
            return "mirrorx";
        }
        if (axis == 1) {
            return "mirrory";
        }
        return "mirrorz";
    }

    private String transform(String meshData, Object vector, Object pivotVec, boolean rotation) {
        try {
            MeshDataHandle mesh = registry.requireMesh(meshData);
            double[] values = ModelVecUtil.parseVec3(vector, rotation ? "rotation" : "scale");
            MeshDataHandle updated;
            if (rotation) {
                updated = mesh.withRotation(new double[] {
                        mesh.rotationDeg[0] + values[0],
                        mesh.rotationDeg[1] + values[1],
                        mesh.rotationDeg[2] + values[2]
                });
            } else {
                updated = mesh.withScale(new double[] {
                        mesh.scale[0] * values[0],
                        mesh.scale[1] * values[1],
                        mesh.scale[2] * values[2]
                });
            }
            if (pivotVec != null) {
                updated = updated.withPivot(ModelVecUtil.parseVec3(pivotVec, "pivot"));
            }
            registry.register(updated);
            return updated.id;
        } catch (IllegalArgumentException error) {
            ScriptLog.apiWarn("model.mesh", rotation ? "rotate" : "scaling", error.getMessage());
            return "";
        }
    }
}
