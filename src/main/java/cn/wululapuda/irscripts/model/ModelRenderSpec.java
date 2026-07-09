package cn.wululapuda.irscripts.model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class ModelRenderSpec {
    public final String modelPath;
    public final List<String> groupNames;
    public final double[] position;
    public final double[] origin;
    public final double[] scale;
    public final double[] rotationDeg;
    public final double[] pivot;
    public final double[] center;
    public final double[] normal;

    public ModelRenderSpec(
            String modelPath,
            List<String> groupNames,
            double[] position,
            MeshDataHandle mesh,
            ModelCoordinateSystem coordinates
    ) {
        this.modelPath = modelPath;
        this.groupNames = new ArrayList<>(groupNames);
        this.position = copy3(position);
        this.origin = copy3(mesh.origin);
        this.scale = copy3(mesh.scale);
        this.rotationDeg = copy3(mesh.rotationDeg);
        this.pivot = copy3(mesh.pivot);
        this.center = copy3(coordinates.getCenter());
        this.normal = copy3(coordinates.getNormal());
    }

    public ModelRenderSpec(
            String modelPath,
            List<String> groupNames,
            double[] position,
            double[] origin,
            double[] scale,
            double[] rotationDeg,
            double[] pivot,
            ModelCoordinateSystem coordinates
    ) {
        this.modelPath = modelPath;
        this.groupNames = new ArrayList<>(groupNames);
        this.position = copy3(position);
        this.origin = copy3(origin);
        this.scale = copy3(scale);
        this.rotationDeg = copy3(rotationDeg);
        this.pivot = copy3(pivot);
        this.center = copy3(coordinates.getCenter());
        this.normal = copy3(coordinates.getNormal());
    }

    public static List<String> collectGroupNames(ModelHandleRegistry registry, MeshGroupHandle group) {
        Set<String> names = new LinkedHashSet<>();
        appendGroupNames(registry, names, group.startMeshIds);
        appendGroupNames(registry, names, group.middleMeshIds);
        appendGroupNames(registry, names, group.endMeshIds);
        return new ArrayList<>(names);
    }

    private static void appendGroupNames(ModelHandleRegistry registry, Set<String> names, List<String> meshIds) {
        for (String meshId : meshIds) {
            names.add(registry.requireMesh(meshId).groupName);
        }
    }

    private static double[] copy3(double[] values) {
        return new double[] {values[0], values[1], values[2]};
    }
}
