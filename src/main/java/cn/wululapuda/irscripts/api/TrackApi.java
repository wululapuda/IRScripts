package cn.wululapuda.irscripts.api;

import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cn.wululapuda.irscripts.math.MathArrayUtil;
import cn.wululapuda.irscripts.script.TrackScriptBootstrap;
import cn.wululapuda.irscripts.script.TrackScriptRegistry;
import cn.wululapuda.irscripts.script.TrackScriptType;
import cn.wululapuda.irscripts.track.TrackDataSnapshot;
import cn.wululapuda.irscripts.track.TrackEndpointSnapshot;
import cn.wululapuda.irscripts.track.TrackGeometryExtractor;
import cn.wululapuda.irscripts.track.TrackScriptContext;
import cn.wululapuda.irscripts.track.TrackSource;
import cn.wululapuda.irscripts.util.ScriptLog;
import cam72cam.mod.math.Vec3d;
import cam72cam.mod.math.Vec3i;
import cam72cam.mod.world.World;

/**
 * Root-level script API exposed as {@code track}.
 * In track scripts, all geometry is relative to the track placement point (not rolling stock).
 */
public final class TrackApi {
    private final EntityRollingStock stock;
    private final TrackScriptContext context;

    /** Rolling-stock script scope (train-relative {@link #here()}). */
    public TrackApi(EntityRollingStock stock) {
        this.stock = stock;
        this.context = null;
    }

    /** Track script scope (placement-relative {@link #here()} and {@link #pos()}). */
    public TrackApi(TrackScriptContext context) {
        this.context = context;
        this.stock = null;
    }

    /**
     * Track placement block position {@code [x, y, z]}.
     * Only available in track scripts.
     */
    public int[] pos() {
        if (context == null) {
            ScriptLog.apiIgnored("track", "pos", "track scripts only");
            return null;
        }
        return context.getPlacementBlock();
    }

    /**
     * Geometry at the track placement point (track scripts)
     * or at the rolling stock block position (stock scripts).
     */
    public Object here() {
        if (context != null) {
            return queryPlacement();
        }
        if (stock == null) {
            return null;
        }
        Vec3d position = stock.getPosition();
        return queryWorld(stock.getWorld(), position.x, position.y, position.z);
    }

    public Object at(Object position) {
        return query(position);
    }

    public Object at(double x, double y, double z) {
        return query(new double[] {x, y, z});
    }

    public String script(String trackId, String typeKey) {
        TrackScriptBootstrap.ensureScanned();
        String path = TrackScriptRegistry.getScriptPath(trackId, typeKey);
        return path == null ? "" : path;
    }

    public boolean hasScript(String trackId, String typeKey) {
        TrackScriptBootstrap.ensureScanned();
        return TrackScriptRegistry.getScriptPath(trackId, typeKey) != null;
    }

    public boolean usesNative(String trackId, String typeKey) {
        TrackScriptBootstrap.ensureScanned();
        TrackScriptType type = TrackScriptType.fromJsonKey(typeKey);
        if (type == null) {
            return true;
        }
        return TrackScriptRegistry.usesNative(trackId, type.getTrackItem());
    }

    public String[] scriptTypes() {
        TrackScriptType[] types = TrackScriptType.values();
        String[] keys = new String[types.length];
        for (int index = 0; index < types.length; index++) {
            keys[index] = types[index].getJsonKey();
        }
        return keys;
    }

    /**
     * World position {@code [x, y, z]} of switch branch 2 (turn / 方向二) endpoint.
     * Returns {@code null} when the track at the query point is not a switch or has no turn branch.
     */
    public double[] getBranch2EndPosition() {
        TrackEndpointSnapshot branch2 = resolveBranch2Endpoint();
        return branch2 == null ? null : branch2.getPosition();
    }

    /**
     * World normal {@code [x, y, z]} (unit tangent) at switch branch 2 (turn / 方向二) endpoint.
     */
    public double[] getBranch2EndNormal() {
        TrackEndpointSnapshot branch2 = resolveBranch2Endpoint();
        return branch2 == null ? null : branch2.getNormal();
    }

    /**
     * Same as {@link #getBranch2EndPosition()} but at an explicit world block position.
     */
    public double[] getBranch2EndPosition(Object position) {
        TrackEndpointSnapshot branch2 = resolveBranch2Endpoint(position);
        return branch2 == null ? null : branch2.getPosition();
    }

    /**
     * Same as {@link #getBranch2EndNormal()} but at an explicit world block position.
     */
    public double[] getBranch2EndNormal(Object position) {
        TrackEndpointSnapshot branch2 = resolveBranch2Endpoint(position);
        return branch2 == null ? null : branch2.getNormal();
    }

    private TrackEndpointSnapshot resolveBranch2Endpoint() {
        if (context != null) {
            return branch2FromPlacement();
        }
        if (stock != null) {
            Vec3d position = stock.getPosition();
            return branch2FromWorld(stock.getWorld(), position.x, position.y, position.z);
        }
        return null;
    }

    private TrackEndpointSnapshot resolveBranch2Endpoint(Object position) {
        if (!requireServer("getBranch2End")) {
            return null;
        }
        try {
            double[] coords = MathArrayUtil.parseVec3(position, "position");
            World world = resolveWorld();
            if (world == null) {
                return null;
            }
            return branch2FromWorld(world, coords[0], coords[1], coords[2]);
        } catch (IllegalArgumentException error) {
            ScriptLog.apiWarn("track", "getBranch2End", error.getMessage());
            return null;
        }
    }

    private TrackEndpointSnapshot branch2FromPlacement() {
        if (!requireServer("getBranch2End")) {
            return null;
        }
        try {
            TrackSource source = TrackSource.resolve(context.getWorld(), context.getPlacementPos());
            if (source == null) {
                return null;
            }
            return branch2FromSnapshot(TrackGeometryExtractor.extract(source));
        } catch (Exception error) {
            ScriptLog.apiWarn("track", "getBranch2End", error.getMessage());
            return null;
        }
    }

    private TrackEndpointSnapshot branch2FromWorld(World world, double x, double y, double z) {
        if (!requireServer("getBranch2End")) {
            return null;
        }
        try {
            int[] blockPos = new int[] {
                    (int) Math.floor(x),
                    (int) Math.floor(y),
                    (int) Math.floor(z)
            };
            TrackSource source = TrackSource.resolve(world, new Vec3i(blockPos[0], blockPos[1], blockPos[2]));
            if (source == null) {
                return null;
            }
            return branch2FromSnapshot(TrackGeometryExtractor.extract(source));
        } catch (Exception error) {
            ScriptLog.apiWarn("track", "getBranch2End", error.getMessage());
            return null;
        }
    }

    private static TrackEndpointSnapshot branch2FromSnapshot(TrackDataSnapshot snapshot) {
        if (snapshot == null || !"SWITCH".equals(snapshot.getType())) {
            return null;
        }
        return snapshot.getBranch2();
    }

    private Object queryPlacement() {
        if (!requireServer("here")) {
            return null;
        }
        try {
            Vec3i placement = context.getPlacementPos();
            TrackSource source = TrackSource.resolve(context.getWorld(), placement);
            if (source == null) {
                return null;
            }
            return TrackGeometryExtractor.extract(source);
        } catch (Exception error) {
            ScriptLog.apiWarn("track", "here", "failed to read track data: " + error.getMessage());
            return null;
        }
    }

    private Object query(Object position) {
        if (!requireServer("at")) {
            return null;
        }
        try {
            double[] coords = MathArrayUtil.parseVec3(position, "position");
            World world = resolveWorld();
            if (world == null) {
                return null;
            }
            return queryWorld(world, coords[0], coords[1], coords[2]);
        } catch (IllegalArgumentException error) {
            ScriptLog.apiWarn("track", "at", error.getMessage());
            return null;
        } catch (Exception error) {
            ScriptLog.apiWarn("track", "at", "failed to read track data: " + error.getMessage());
            return null;
        }
    }

    private Object queryWorld(World world, double x, double y, double z) {
        int[] blockPos = new int[] {
                (int) Math.floor(x),
                (int) Math.floor(y),
                (int) Math.floor(z)
        };
        TrackSource source = TrackSource.resolve(world, new Vec3i(blockPos[0], blockPos[1], blockPos[2]));
        if (source == null) {
            return null;
        }
        return TrackGeometryExtractor.extract(source);
    }

    private World resolveWorld() {
        if (context != null) {
            return context.getWorld();
        }
        if (stock != null) {
            return stock.getWorld();
        }
        return null;
    }

    private boolean requireServer(String action) {
        World world = resolveWorld();
        if (world != null && world.isServer) {
            return true;
        }
        ScriptLog.apiIgnored("track", action, "server-side only");
        return false;
    }
}
