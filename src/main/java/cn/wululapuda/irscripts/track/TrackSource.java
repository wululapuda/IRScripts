package cn.wululapuda.irscripts.track;

import cam72cam.immersiverailroading.tile.TileRail;
import cam72cam.immersiverailroading.tile.TileRailBase;
import cam72cam.immersiverailroading.tile.TileRailPreview;
import cam72cam.immersiverailroading.util.RailInfo;
import cam72cam.mod.math.Vec3d;
import cam72cam.mod.math.Vec3i;
import cam72cam.mod.world.World;

public final class TrackSource {
    private final World world;
    private final Vec3i tilePos;
    private final Vec3i queryPos;
    private final RailInfo railInfo;
    private final boolean blueprint;
    private final TileRail switchTile;

    private TrackSource(
            World world,
            Vec3i tilePos,
            Vec3i queryPos,
            RailInfo railInfo,
            boolean blueprint,
            TileRail switchTile
    ) {
        this.world = world;
        this.tilePos = tilePos;
        this.queryPos = queryPos;
        this.railInfo = railInfo;
        this.blueprint = blueprint;
        this.switchTile = switchTile;
    }

    World getWorld() {
        return world;
    }

    Vec3i getTilePos() {
        return tilePos;
    }

    Vec3i getQueryPos() {
        return queryPos;
    }

    RailInfo getRailInfo() {
        return railInfo;
    }

    boolean isBlueprint() {
        return blueprint;
    }

    TileRail getSwitchTile() {
        return switchTile;
    }

    Vec3d getQueryCenter() {
        return new Vec3d(queryPos.x + 0.5D, queryPos.y + 0.5D, queryPos.z + 0.5D);
    }

    public static TrackSource resolve(World world, Vec3i blockPos) {
        TileRailPreview preview = world.getBlockEntity(blockPos, TileRailPreview.class);
        if (preview != null) {
            RailInfo info = preview.getRailRenderInfo();
            if (info != null) {
                return new TrackSource(world, preview.getPos(), blockPos, info, true, null);
            }
        }

        TileRail rail = world.getBlockEntity(blockPos, TileRail.class);
        if (rail != null && rail.info != null) {
            return new TrackSource(world, rail.getPos(), blockPos, rail.info, false, findSwitchTile(rail));
        }

        TileRailBase base = world.getBlockEntity(blockPos, TileRailBase.class);
        if (base != null) {
            TileRail parent = base.getParentTile();
            if (parent != null && parent.info != null) {
                return new TrackSource(world, parent.getPos(), blockPos, parent.info, false, findSwitchTile(base));
            }
        }

        return null;
    }

    private static TileRail findSwitchTile(TileRail rail) {
        if (rail == null) {
            return null;
        }
        if (rail.info != null && rail.info.settings.type == cam72cam.immersiverailroading.library.TrackItems.SWITCH) {
            return rail;
        }
        TileRail parent = rail.getParentTile();
        if (parent != null && parent.info != null
                && parent.info.settings.type == cam72cam.immersiverailroading.library.TrackItems.SWITCH) {
            return parent;
        }
        return rail.findSwitchParent();
    }

    private static TileRail findSwitchTile(TileRailBase base) {
        if (base == null) {
            return null;
        }
        if (base instanceof TileRail) {
            return findSwitchTile((TileRail) base);
        }
        return base.findSwitchParent();
    }
}
