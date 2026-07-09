package cn.wululapuda.irscripts.model;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ModelHandleRegistry {
    private final Map<String, ModelDataHandle> models = new ConcurrentHashMap<>();
    private final Map<String, MeshDataHandle> meshes = new ConcurrentHashMap<>();
    private final Map<String, MeshGroupHandle> meshGroups = new ConcurrentHashMap<>();
    private final Map<String, UUID> handleToRenderId = new ConcurrentHashMap<>();

    public void register(ModelDataHandle model) {
        models.put(model.id, model);
    }

    public void register(MeshDataHandle mesh) {
        meshes.put(mesh.id, mesh);
    }

    public void register(MeshGroupHandle group) {
        meshGroups.put(group.id, group);
    }

    public ModelDataHandle requireModel(String handleId) {
        ModelDataHandle model = models.get(handleId);
        if (model == null) {
            throw new IllegalArgumentException("Unknown model data handle: " + handleId);
        }
        return model;
    }

    public MeshDataHandle requireMesh(String handleId) {
        MeshDataHandle mesh = meshes.get(handleId);
        if (mesh == null) {
            throw new IllegalArgumentException("Unknown mesh data handle: " + handleId);
        }
        return mesh;
    }

    public MeshGroupHandle requireMeshGroup(String handleId) {
        MeshGroupHandle group = meshGroups.get(handleId);
        if (group == null) {
            throw new IllegalArgumentException("Unknown mesh group handle: " + handleId);
        }
        return group;
    }

    public boolean isKnownHandle(String handleId) {
        return models.containsKey(handleId)
                || meshes.containsKey(handleId)
                || meshGroups.containsKey(handleId);
    }

    public boolean isMeshHandle(String handleId) {
        return meshes.containsKey(handleId);
    }

    public boolean isMeshGroupHandle(String handleId) {
        return meshGroups.containsKey(handleId);
    }

    public void bindRender(String sourceHandle, UUID renderId) {
        handleToRenderId.put(sourceHandle, renderId);
    }

    public UUID findRenderId(String handleOrRenderId) {
        if (handleOrRenderId == null) {
            return null;
        }
        UUID bound = handleToRenderId.get(handleOrRenderId);
        if (bound != null) {
            return bound;
        }
        try {
            return UUID.fromString(handleOrRenderId);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    public void clear() {
        models.clear();
        meshes.clear();
        meshGroups.clear();
        handleToRenderId.clear();
    }
}
