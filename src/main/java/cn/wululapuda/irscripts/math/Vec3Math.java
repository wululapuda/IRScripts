package cn.wululapuda.irscripts.math;

public final class Vec3Math {
    private Vec3Math() {
    }

    public static double[] add(double[] a, double[] b) {
        return new double[] {a[0] + b[0], a[1] + b[1], a[2] + b[2]};
    }

    public static double[] sub(double[] a, double[] b) {
        return new double[] {a[0] - b[0], a[1] - b[1], a[2] - b[2]};
    }

    public static double[] mul(double[] vector, double scalar) {
        return new double[] {vector[0] * scalar, vector[1] * scalar, vector[2] * scalar};
    }

    public static double[] div(double[] vector, double scalar) {
        if (Math.abs(scalar) < 1.0E-12D) {
            throw new IllegalArgumentException("division by zero");
        }
        return mul(vector, 1.0D / scalar);
    }

    public static double[] neg(double[] vector) {
        return new double[] {-vector[0], -vector[1], -vector[2]};
    }

    public static double dot(double[] a, double[] b) {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
    }

    public static double[] cross(double[] a, double[] b) {
        return new double[] {
                a[1] * b[2] - a[2] * b[1],
                a[2] * b[0] - a[0] * b[2],
                a[0] * b[1] - a[1] * b[0]
        };
    }

    public static double length(double[] vector) {
        return Math.sqrt(dot(vector, vector));
    }

    public static double[] normalize(double[] vector) {
        double length = length(vector);
        if (length < 1.0E-12D) {
            throw new IllegalArgumentException("cannot normalize zero vector");
        }
        return mul(vector, 1.0D / length);
    }

    public static double distance(double[] a, double[] b) {
        return length(sub(a, b));
    }

    public static double[] lerp(double[] a, double[] b, double t) {
        return new double[] {
                a[0] + (b[0] - a[0]) * t,
                a[1] + (b[1] - a[1]) * t,
                a[2] + (b[2] - a[2]) * t
        };
    }

    public static double[] project(double[] vector, double[] onto) {
        double ontoLengthSq = dot(onto, onto);
        if (ontoLengthSq < 1.0E-12D) {
            throw new IllegalArgumentException("projection axis cannot be zero");
        }
        double scale = dot(vector, onto) / ontoLengthSq;
        return mul(onto, scale);
    }

    public static double angle(double[] a, double[] b) {
        double aLength = length(a);
        double bLength = length(b);
        if (aLength < 1.0E-12D || bLength < 1.0E-12D) {
            throw new IllegalArgumentException("angle requires non-zero vectors");
        }
        double cosine = dot(a, b) / (aLength * bLength);
        return Math.acos(Math.max(-1.0D, Math.min(1.0D, cosine)));
    }

    public static double[] copy(double[] vector) {
        return new double[] {vector[0], vector[1], vector[2]};
    }
}
