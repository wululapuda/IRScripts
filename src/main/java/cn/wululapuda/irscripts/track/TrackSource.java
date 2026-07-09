package cn.wululapuda.irscripts.track;

import cam72cam.immersiverailroading.tile.TileRail;
import cam72cam.immersiverailroading.tile.TileRailPreview;
import cam72cam.immersiverailroading.util.RailInfo;
import cam72cam.mod.math.Vec3i;
import cam72cam.mod.world.World;

public final class TrackSource {
    private final World world;
    private final Vec3i tilePos;
    private final RailInfo railInfo;
    private final boolean blueprint;

    TrackSource(World world, Vec3i tilePos, RailInfo railInfo, boolean blueprint) {
        this.world = world;
        this.tilePos = tilePos;
        this.railInfo = railInfo;
        this.blueprint = blueprint;
    }

    World getWorld() {
        return world;
    }

    Vec3i getTilePos() {
        return tilePos;
    }

    RailInfo getRailInfo() {
        return railInfo;
    }

    boolean isBlueprint() {
        return blueprint;
    }

    public static TrackSource resolve(World world, Vec3i blockPos) {
        TileRailPreview preview = world.getBlockEntity(blockPos, TileRailPreview.class);
        if (preview != null) {
            RailInfo info = preview.getRailRenderInfo();
            if (info != null) {
                return new TrackSource(world, preview.getPos(), info, true);
            }
        }

        TileRail rail = world.getBlockEntity(blockPos, TileRail.class);
        if (rail != null && rail.info != null) {
            return new TrackSource(world, rail.getPos(), rail.info, false);
        }

        cam72cam.immersiverailroading.tile.TileRailBase base =
                world.getBlockEntity(blockPos, cam72cam.immersiverailroading.tile.TileRailBase.class);
        if (base != null) {
            TileRail parent = base.getParentTile();
            if (parent != null && parent.info != null) {
                return new TrackSource(world, parent.getPos(), parent.info, false);
            }
        }

        return null;
    }
}
