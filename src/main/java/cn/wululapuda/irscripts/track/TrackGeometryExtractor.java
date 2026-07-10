package cn.wululapuda.irscripts.track;

import cam72cam.immersiverailroading.library.SwitchState;
import cam72cam.immersiverailroading.library.TrackDirection;
import cam72cam.immersiverailroading.library.TrackItems;
import cam72cam.immersiverailroading.tile.TileRail;
import cam72cam.immersiverailroading.track.BuilderBase;
import cam72cam.immersiverailroading.track.BuilderCubicCurve;
import cam72cam.immersiverailroading.track.BuilderIterator;
import cam72cam.immersiverailroading.track.BuilderStraight;
import cam72cam.immersiverailroading.track.BuilderSwitch;
import cam72cam.immersiverailroading.track.CubicCurve;
import cam72cam.immersiverailroading.track.IIterableTrack;
import cam72cam.immersiverailroading.track.PosStep;
import cam72cam.immersiverailroading.util.RailInfo;
import cam72cam.immersiverailroading.util.SwitchUtil;
import cam72cam.immersiverailroading.util.VecUtil;
import cn.wululapuda.irscripts.script.TrackScriptRegistry;
import cam72cam.mod.math.Vec3d;
import cam72cam.mod.math.Vec3i;

import java.lang.reflect.Field;
import java.util.List;

public final class TrackGeometryExtractor {
    private TrackGeometryExtractor() {
    }

    public static TrackDataSnapshot extract(TrackSource source) {
        RailInfo info = source.getRailInfo();
        Vec3i tilePos = source.getTilePos();
        double gauge = info.settings.gauge.value();
        double gaugeScale = info.settings.gauge.scale();
        TrackItems type = info.settings.type;

        Double curvosity = type.hasCurvosity() ? (double) info.settings.curvosity : null;
        String smoothing = type.hasSmoothing() ? info.settings.smoothing.name() : null;
        String direction = info.settings.direction == TrackDirection.NONE
                ? "NONE"
                : info.settings.direction.name();
        String trackId = info.settings.track;
        String scriptPath = TrackScriptRegistry.getScriptPath(trackId, type);
        boolean usesNative = scriptPath == null;
        String switchDirection = resolveSwitchDirection(source);
        String switchForced = resolveSwitchForced(source);

        TrackEndpointSnapshot start = extractStart(source);
        TrackEndpointSnapshot end = null;
        TrackEndpointSnapshot branch1 = null;
        TrackEndpointSnapshot branch2 = null;
        double[][] controls = null;

        if (type == TrackItems.SWITCH) {
            BuilderSwitch switchBuilder = (BuilderSwitch) info.getBuilder(source.getWorld(), tilePos);
            BuilderStraight straightBuilder = readSwitchStraightBuilder(switchBuilder);
            BuilderIterator turnBuilder = readSwitchTurnBuilder(switchBuilder);
            if (straightBuilder != null) {
                branch1 = extractPathEndpoint(straightBuilder, true);
            }
            if (turnBuilder != null) {
                branch2 = extractPathEndpoint(turnBuilder, true);
            }
            if (turnBuilder instanceof BuilderCubicCurve) {
                controls = extractControls((BuilderCubicCurve) turnBuilder, source);
            }
        } else if (type == TrackItems.CUSTOM || type == TrackItems.TURN || type == TrackItems.SLOPE || type == TrackItems.STRAIGHT) {
            BuilderBase builder = info.getBuilder(source.getWorld(), tilePos);
            end = extractPathEndpoint(builder, true);
            if (builder instanceof BuilderCubicCurve) {
                controls = extractControls((BuilderCubicCurve) builder, source);
            }
        }

        return new TrackDataSnapshot(
                type.name(),
                trackId,
                gauge,
                gaugeScale,
                source.isBlueprint(),
                usesNative,
                scriptPath,
                curvosity,
                smoothing,
                direction,
                switchDirection,
                switchForced,
                new int[] {tilePos.x, tilePos.y, tilePos.z},
                start,
                end,
                branch1,
                branch2,
                controls
        );
    }

    private static String resolveSwitchDirection(TrackSource source) {
        TileRail switchTile = source.getSwitchTile();
        if (switchTile == null) {
            return SwitchState.NONE.name();
        }
        return SwitchUtil.getSwitchState(switchTile, source.getQueryCenter()).name();
    }

    private static String resolveSwitchForced(TrackSource source) {
        TileRail switchTile = source.getSwitchTile();
        if (switchTile == null || switchTile.info == null) {
            return SwitchState.NONE.name();
        }
        return switchTile.info.switchForced.name();
    }

    private static TrackEndpointSnapshot extractStart(TrackSource source) {
        RailInfo info = source.getRailInfo();
        Vec3d anchor = anchorFor(source.getTilePos(), info);
        double heightOffset = heightOffset(info);
        Vec3d worldPos = anchor.add(0.0D, heightOffset, 0.0D);
        Vec3d tangent = VecUtil.fromWrongYawPitch(1.0F, info.placementInfo.yaw, 0.0F).normalize();
        return new TrackEndpointSnapshot(toArray(worldPos), toArray(tangent));
    }

    private static TrackEndpointSnapshot extractPathEndpoint(BuilderBase builder, boolean endPoint) {
        if (!(builder instanceof IIterableTrack)) {
            return null;
        }
        RailInfo info = builder.info;
        double step = 0.25D * info.settings.gauge.scale();
        List<PosStep> path = ((IIterableTrack) builder).getPath(step);
        if (path == null || path.isEmpty()) {
            return null;
        }
        PosStep stepData = endPoint ? path.get(path.size() - 1) : path.get(0);
        Vec3d anchor = anchorFor(builder.pos, info);
        double heightOffset = heightOffset(info);
        Vec3d worldPos = anchor.add(stepData.x, stepData.y + heightOffset, stepData.z);
        Vec3d tangent = VecUtil.fromWrongYawPitch(1.0F, stepData.yaw, stepData.pitch).normalize();
        return new TrackEndpointSnapshot(toArray(worldPos), toArray(tangent));
    }

    private static double[][] extractControls(BuilderCubicCurve builder, TrackSource source) {
        if (builder == null) {
            return null;
        }
        CubicCurve curve = builder.getCurve();
        return controlsToWorld(curve, source);
    }

    private static double[][] controlsToWorld(CubicCurve curve, TrackSource source) {
        RailInfo info = source.getRailInfo();
        Vec3d anchor = anchorFor(source.getTilePos(), info);
        double heightOffset = heightOffset(info);
        return new double[][] {
                worldPoint(anchor, heightOffset, curve.p1),
                worldPoint(anchor, heightOffset, curve.ctrl1),
                worldPoint(anchor, heightOffset, curve.ctrl2),
                worldPoint(anchor, heightOffset, curve.p2)
        };
    }

    private static BuilderStraight readSwitchStraightBuilder(BuilderSwitch switchBuilder) {
        try {
            Field field = BuilderSwitch.class.getDeclaredField("realStraightBuilder");
            field.setAccessible(true);
            return (BuilderStraight) field.get(switchBuilder);
        } catch (ReflectiveOperationException error) {
            return null;
        }
    }

    private static BuilderIterator readSwitchTurnBuilder(BuilderSwitch switchBuilder) {
        try {
            Field field = BuilderSwitch.class.getDeclaredField("turnBuilder");
            field.setAccessible(true);
            return (BuilderIterator) field.get(switchBuilder);
        } catch (ReflectiveOperationException error) {
            return null;
        }
    }

    private static Vec3d anchorFor(Vec3i tilePos, RailInfo info) {
        return info.placementInfo.placementPosition.add(tilePos);
    }

    private static double heightOffset(RailInfo info) {
        return info.getTrackHeight() * info.settings.gauge.scale();
    }

    private static double[] worldPoint(Vec3d anchor, double heightOffset, Vec3d local) {
        Vec3d world = anchor.add(local.x, local.y + heightOffset, local.z);
        return toArray(world);
    }

    private static double[] toArray(Vec3d vector) {
        return new double[] {vector.x, vector.y, vector.z};
    }
}
