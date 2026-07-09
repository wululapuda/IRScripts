package cn.wululapuda.irscripts.model;

import java.util.UUID;

public final class MeshDataHandle {
    public final String id;
    public final String modelId;
    public final String groupName;
    public final double[] origin;
    public final double[] scale;
    public final double[] rotationDeg;
    public final double[] pivot;

    public MeshDataHandle(
            String modelId,
            String groupName,
            double[] origin,
            double[] scale,
            double[] rotationDeg,
            double[] pivot
    ) {
        this.id = "mesh:" + UUID.randomUUID();
        this.modelId = modelId;
        this.groupName = groupName;
        this.origin = copy3(origin);
        this.scale = copy3(scale);
        this.rotationDeg = copy3(rotationDeg);
        this.pivot = copy3(pivot);
    }

    public MeshDataHandle withOrigin(double[] newOrigin) {
        return new MeshDataHandle(modelId, groupName, newOrigin, scale, rotationDeg, pivot);
    }

    public MeshDataHandle withScale(double[] newScale) {
        return new MeshDataHandle(modelId, groupName, origin, newScale, rotationDeg, pivot);
    }

    public MeshDataHandle withRotation(double[] newRotation) {
        return new MeshDataHandle(modelId, groupName, origin, scale, newRotation, pivot);
    }

    public MeshDataHandle withPivot(double[] newPivot) {
        return new MeshDataHandle(modelId, groupName, origin, scale, rotationDeg, newPivot);
    }

    private static double[] copy3(double[] values) {
        return new double[] {values[0], values[1], values[2]};
    }
}
