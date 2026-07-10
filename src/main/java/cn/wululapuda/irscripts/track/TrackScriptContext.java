package cn.wululapuda.irscripts.track;

import cam72cam.immersiverailroading.tile.TileRail;
import cam72cam.immersiverailroading.tile.TileRailBase;
import cam72cam.mod.math.Vec3i;
import cam72cam.mod.world.World;

public final class TrackScriptContext {
    private final World world;
    private final Vec3i placementPos;
    private final TileRail parentRail;

    private TrackScriptContext(World world, Vec3i placementPos, TileRail parentRail) {
        this.world = world;
        this.placementPos = placementPos;
        this.parentRail = parentRail;
    }

    public World getWorld() {
        return world;
    }

    public Vec3i getPlacementPos() {
        return placementPos;
    }

    public TileRail getParentRail() {
        return parentRail;
    }

    public int[] getPlacementBlock() {
        return new int[] {placementPos.x, placementPos.y, placementPos.z};
    }

    public static TrackScriptContext from(TileRail parentRail) {
        return new TrackScriptContext(parentRail.getWorld(), parentRail.getPos(), parentRail);
    }

    public static TileRail resolveParentRail(TileRailBase tile) {
        if (tile instanceof TileRail) {
            TileRail rail = (TileRail) tile;
            if (rail.info != null) {
                return rail;
            }
        }
        return tile.getParentTile();
    }
}
