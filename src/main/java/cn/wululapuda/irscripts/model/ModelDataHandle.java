package cn.wululapuda.irscripts.model;

import cam72cam.mod.model.obj.OBJGroup;
import cam72cam.mod.model.obj.OBJModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class ModelDataHandle {
    public final String id;
    public final String path;
    public final OBJModel model;

    public ModelDataHandle(String path, OBJModel model) {
        this.id = "model:" + UUID.randomUUID();
        this.path = path;
        this.model = model;
    }

    public List<String> listMeshes() {
        return new ArrayList<>(model.groups.keySet());
    }

    public OBJGroup requireGroup(String groupName) {
        OBJGroup group = model.groups.get(groupName);
        if (group == null) {
            throw new IllegalArgumentException("Unknown mesh/group: " + groupName);
        }
        return group;
    }

    public double[] centerOf(String groupName) {
        OBJGroup group = requireGroup(groupName);
        return new double[] {
                (group.min.x + group.max.x) * 0.5D,
                (group.min.y + group.max.y) * 0.5D,
                (group.min.z + group.max.z) * 0.5D
        };
    }
}
