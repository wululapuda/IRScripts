package cn.wululapuda.irscripts.api;

import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cn.wululapuda.irscripts.model.MeshDataHandle;
import cn.wululapuda.irscripts.model.MeshGroupHandle;
import cn.wululapuda.irscripts.model.ModelCoordinateSystem;
import cn.wululapuda.irscripts.model.ModelDataHandle;
import cn.wululapuda.irscripts.model.ModelHandleRegistry;
import cn.wululapuda.irscripts.model.ModelRenderSpec;
import cn.wululapuda.irscripts.model.ModelVecUtil;
import cn.wululapuda.irscripts.util.ScriptLog;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class ModelRenderApi {
    private final EntityRollingStock stock;
    private final ModelHandleRegistry registry;
    private final ScriptModelTracker tracker;
    private final ModelCoordinateSystem coordinates;

    public ModelRenderApi(
            EntityRollingStock stock,
            ModelHandleRegistry registry,
            ScriptModelTracker tracker,
            ModelCoordinateSystem coordinates
    ) {
        this.stock = stock;
        this.registry = registry;
        this.tracker = tracker;
        this.coordinates = coordinates;
    }

    public void renderon(String meshOrGroup, Object position) {
        if (!requireServer("renderon")) {
            return;
        }
        try {
            double[] pos = ModelVecUtil.parseVec3(position, "position");
            ModelRenderSpec spec = buildSpec(meshOrGroup, pos);
            UUID renderId = tracker.addRender(stock, spec);
            registry.bindRender(meshOrGroup, renderId);
            ScriptLog.modelRenderAdded(stock.getUUID(), renderId, meshOrGroup);
        } catch (IllegalArgumentException error) {
            ScriptLog.apiWarn("model.render", "renderon", error.getMessage());
        }
    }

    public void render(String meshGroup) {
        if (!requireServer("render")) {
            return;
        }
        try {
            ModelRenderSpec spec = buildSpec(meshGroup, new double[] {0.0D, 0.0D, 0.0D});
            UUID renderId = tracker.addRender(stock, spec);
            registry.bindRender(meshGroup, renderId);
            ScriptLog.modelRenderAdded(stock.getUUID(), renderId, meshGroup);
        } catch (IllegalArgumentException error) {
            ScriptLog.apiWarn("model.render", "render", error.getMessage());
        }
    }

    public void rendercut(String meshOrGroup) {
        if (!requireServer("rendercut")) {
            return;
        }
        UUID renderId = registry.findRenderId(meshOrGroup);
        if (renderId == null) {
            ScriptLog.apiWarn("model.render", "rendercut", "unknown render handle: " + meshOrGroup);
            return;
        }
        tracker.removeRender(stock, renderId);
        ScriptLog.modelRenderRemoved(stock.getUUID(), renderId);
    }

    private ModelRenderSpec buildSpec(String handleId, double[] position) {
        if (registry.isMeshHandle(handleId) || handleId.startsWith("mesh:")) {
            try {
                MeshDataHandle mesh = registry.requireMesh(handleId);
                ModelDataHandle model = registry.requireModel(mesh.modelId);
                return new ModelRenderSpec(
                        model.path,
                        Collections.singletonList(mesh.groupName),
                        position,
                        mesh,
                        coordinates
                );
            } catch (IllegalArgumentException ignored) {
                // fall through to group/model lookup
            }
        }

        if (registry.isMeshGroupHandle(handleId) || handleId.startsWith("meshgroup:")) {
            MeshGroupHandle group = registry.requireMeshGroup(handleId);
            if (group.curveAdapted) {
                throw new IllegalArgumentException("curve-adapted mesh groups are not supported yet");
            }
            List<String> meshIds = group.flattenedMeshIds();
            if (meshIds.isEmpty()) {
                throw new IllegalArgumentException("mesh group is empty");
            }
            MeshDataHandle first = registry.requireMesh(meshIds.get(0));
            ModelDataHandle model = registry.requireModel(first.modelId);
            List<String> groupNames = ModelRenderSpec.collectGroupNames(registry, group);
            double[] origin = averageOrigin(registry, meshIds);
            return new ModelRenderSpec(
                    model.path,
                    groupNames,
                    position,
                    origin,
                    new double[] {1.0D, 1.0D, 1.0D},
                    new double[] {0.0D, 0.0D, 0.0D},
                    new double[] {0.0D, 0.0D, 0.0D},
                    coordinates
            );
        }

        throw new IllegalArgumentException("Unknown mesh or mesh group handle: " + handleId);
    }

    private static double[] averageOrigin(ModelHandleRegistry registry, List<String> meshIds) {
        double x = 0.0D;
        double y = 0.0D;
        double z = 0.0D;
        for (String meshId : meshIds) {
            MeshDataHandle mesh = registry.requireMesh(meshId);
            ModelDataHandle model = registry.requireModel(mesh.modelId);
            double[] center = model.centerOf(mesh.groupName);
            x += center[0];
            y += center[1];
            z += center[2];
        }
        int count = Math.max(1, meshIds.size());
        return new double[] {x / count, y / count, z / count};
    }

    private boolean requireServer(String action) {
        if (stock.getWorld().isServer) {
            return true;
        }
        ScriptLog.apiIgnored("model.render", action, "server-side only");
        return false;
    }
}
