/**
 * Common helpers (Python {@code math}/{@code operator} inspired).
 */
(function (global) {
    function toNumber(value, fallback) {
        var n = Number(value);
        return isNaN(n) ? fallback : n;
    }

    global.util = {
        clamp: function (value, min, max) {
            value = toNumber(value, 0);
            min = toNumber(min, 0);
            max = toNumber(max, 0);
            if (min > max) {
                var tmp = min;
                min = max;
                max = tmp;
            }
            return Math.min(max, Math.max(min, value));
        },

        lerp: function (a, b, t) {
            a = toNumber(a, 0);
            b = toNumber(b, 0);
            t = toNumber(t, 0);
            return a + (b - a) * t;
        },

        inverseLerp: function (a, b, value) {
            a = toNumber(a, 0);
            b = toNumber(b, 0);
            value = toNumber(value, 0);
            if (a === b) {
                return 0.0;
            }
            return (value - a) / (b - a);
        },

        mapRange: function (value, inMin, inMax, outMin, outMax) {
            var t = global.util.inverseLerp(inMin, inMax, value);
            return global.util.lerp(outMin, outMax, t);
        },

        sign: function (value) {
            value = toNumber(value, 0);
            if (value > 0) return 1;
            if (value < 0) return -1;
            return 0;
        },

        approximately: function (a, b, epsilon) {
            a = toNumber(a, 0);
            b = toNumber(b, 0);
            epsilon = epsilon === undefined ? 1e-6 : toNumber(epsilon, 1e-6);
            return Math.abs(a - b) <= epsilon;
        },

        mod: function (value, divisor) {
            value = toNumber(value, 0);
            divisor = toNumber(divisor, 1);
            if (divisor === 0) {
                return NaN;
            }
            var result = value % divisor;
            return result < 0 ? result + divisor : result;
        },

        degToRad: function (degrees) {
            return toNumber(degrees, 0) * Math.PI / 180.0;
        },

        radToDeg: function (radians) {
            return toNumber(radians, 0) * 180.0 / Math.PI;
        },

        isEmpty: function (value) {
            return value === undefined || value === null || value === "";
        }
    };
})(this);
