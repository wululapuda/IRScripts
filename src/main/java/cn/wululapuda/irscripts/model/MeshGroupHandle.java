package cn.wululapuda.irscripts.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class MeshGroupHandle {
    public final String id;
    public final List<String> startMeshIds;
    public final List<String> middleMeshIds;
    public final List<String> endMeshIds;
    public final boolean curveAdapted;
    public final String curveId;

    public MeshGroupHandle(List<String> startMeshIds, List<String> middleMeshIds, List<String> endMeshIds, boolean curveAdapted) {
        this(startMeshIds, middleMeshIds, endMeshIds, curveAdapted, null);
    }

    public MeshGroupHandle(
            List<String> startMeshIds,
            List<String> middleMeshIds,
            List<String> endMeshIds,
            boolean curveAdapted,
            String curveId
    ) {
        this.id = "meshgroup:" + UUID.randomUUID();
        this.startMeshIds = Collections.unmodifiableList(new ArrayList<>(startMeshIds));
        this.middleMeshIds = Collections.unmodifiableList(new ArrayList<>(middleMeshIds));
        this.endMeshIds = Collections.unmodifiableList(new ArrayList<>(endMeshIds));
        this.curveAdapted = curveAdapted;
        this.curveId = curveId;
    }

    public MeshGroupHandle withCurve(String curveId) {
        return new MeshGroupHandle(startMeshIds, middleMeshIds, endMeshIds, true, curveId);
    }

    public List<String> flattenedMeshIds() {
        List<String> ordered = new ArrayList<>();
        ordered.addAll(startMeshIds);
        ordered.addAll(middleMeshIds);
        ordered.addAll(endMeshIds);
        return ordered;
    }
}
