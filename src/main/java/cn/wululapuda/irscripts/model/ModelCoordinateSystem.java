package cn.wululapuda.irscripts.model;

import cam72cam.mod.math.Vec3d;
import cam72cam.mod.render.opengl.RenderState;

/**
 * Script-level model coordinate system.
 * <p>
 * {@link #setCenter(double[])} stores a <b>world coordinate</b> anchor (Minecraft world X/Y/Z).
 * {@link #setNormal(double[])} defines the local up axis at that anchor.
 * {@code renderon} offsets and mesh transforms are expressed in this local frame.
 */
public final class ModelCoordinateSystem {
    private double[] center = {0.0D, 0.0D, 0.0D};
    private double[] normal = {0.0D, 1.0D, 0.0D};

    public void setCenter(double[] center) {
        this.center = copy3(center);
    }

    public void setNormal(double[] normal) {
        double[] copy = copy3(normal);
        double length = Math.sqrt(copy[0] * copy[0] + copy[1] * copy[1] + copy[2] * copy[2]);
        if (length < 1.0E-6D) {
            throw new IllegalArgumentException("normal vector cannot be zero");
        }
        this.normal = new double[] {copy[0] / length, copy[1] / length, copy[2] / length};
    }

    public double[] getCenter() {
        return copy3(center);
    }

    public double[] getNormal() {
        return copy3(normal);
    }

    /**
     * Moves the render origin to the world anchor, expressed relative to the camera.
     */
    public void applyWorldOrigin(RenderState state, Vec3d cameraPos) {
        state.translate(center[0] - cameraPos.x, center[1] - cameraPos.y, center[2] - cameraPos.z);
    }

    /** Rotates the local axes so local Y aligns with {@link #normal}. */
    public void applyLocalAxes(RenderState state) {
        applyNormalRotation(state);
    }

    private void applyNormalRotation(RenderState state) {
        double[] up = {0.0D, 1.0D, 0.0D};
        double[] target = normal;
        if (distance(up, target) < 1.0E-4D || distance(up, negate(target)) < 1.0E-4D) {
            return;
        }

        double[] axis = cross(up, target);
        double axisLength = length(axis);
        if (axisLength < 1.0E-6D) {
            return;
        }
        axis = scale(axis, 1.0D / axisLength);
        double dot = Math.max(-1.0D, Math.min(1.0D, dot(up, target)));
        double degrees = Math.toDegrees(Math.acos(dot));
        state.rotate(degrees, axis[0], axis[1], axis[2]);
    }

    private static double dot(double[] a, double[] b) {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
    }

    private static double[] cross(double[] a, double[] b) {
        return new double[] {
                a[1] * b[2] - a[2] * b[1],
                a[2] * b[0] - a[0] * b[2],
                a[0] * b[1] - a[1] * b[0]
        };
    }

    private static double length(double[] vector) {
        return Math.sqrt(dot(vector, vector));
    }

    private static double distance(double[] a, double[] b) {
        return Math.sqrt(
                (a[0] - b[0]) * (a[0] - b[0])
                        + (a[1] - b[1]) * (a[1] - b[1])
                        + (a[2] - b[2]) * (a[2] - b[2])
        );
    }

    private static double[] negate(double[] vector) {
        return new double[] {-vector[0], -vector[1], -vector[2]};
    }

    private static double[] scale(double[] vector, double factor) {
        return new double[] {vector[0] * factor, vector[1] * factor, vector[2] * factor};
    }

    private static double[] copy3(double[] values) {
        return new double[] {values[0], values[1], values[2]};
    }
}
