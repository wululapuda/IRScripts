package cn.wululapuda.irscripts.api;

import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cn.wululapuda.irscripts.math.MathArrayUtil;
import cn.wululapuda.irscripts.track.TrackGeometryExtractor;
import cn.wululapuda.irscripts.track.TrackSource;
import cn.wululapuda.irscripts.util.ScriptLog;
import cam72cam.mod.math.Vec3d;
import cam72cam.mod.math.Vec3i;
import cam72cam.mod.world.World;

/**
 * Root-level script API exposed as {@code track} (same scope as {@code stock}, {@code model}, {@code math}).
 * Reads IR track geometry without modifying native track loading.
 */
public final class TrackApi {
    private final EntityRollingStock stock;

    public TrackApi(EntityRollingStock stock) {
        this.stock = stock;
    }

    /**
     * Queries track geometry at a world block position.
     * Accepts {@code [x, y, z]} or separate numeric arguments.
     */
    public Object at(Object position) {
        return query(position);
    }

    public Object at(double x, double y, double z) {
        return query(new double[] {x, y, z});
    }

    /** Queries track geometry at the rolling stock's current block position. */
    public Object here() {
        if (stock == null) {
            return null;
        }
        Vec3d pos = stock.getPosition();
        return query(new double[] {pos.x, pos.y, pos.z});
    }

    private Object query(Object position) {
        if (!requireServer("at")) {
            return null;
        }
        try {
            int[] blockPos = parseBlockPos(position);
            World world = stock.getWorld();
            TrackSource source = TrackSource.resolve(world, new Vec3i(blockPos[0], blockPos[1], blockPos[2]));
            if (source == null) {
                return null;
            }
            return TrackGeometryExtractor.extract(source);
        } catch (IllegalArgumentException error) {
            ScriptLog.apiWarn("track", "at", error.getMessage());
            return null;
        } catch (Exception error) {
            ScriptLog.apiWarn("track", "at", "failed to read track data: " + error.getMessage());
            return null;
        }
    }

    private static int[] parseBlockPos(Object value) {
        double[] coords = MathArrayUtil.parseVec3(value, "position");
        return new int[] {
                (int) Math.floor(coords[0]),
                (int) Math.floor(coords[1]),
                (int) Math.floor(coords[2])
        };
    }

    private boolean requireServer(String action) {
        if (stock.getWorld().isServer) {
            return true;
        }
        ScriptLog.apiIgnored("track", action, "server-side only");
        return false;
    }
}
