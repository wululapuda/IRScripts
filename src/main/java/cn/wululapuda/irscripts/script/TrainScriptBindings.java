package cn.wululapuda.irscripts.script;

import cam72cam.immersiverailroading.entity.EntityMoveableRollingStock;
import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cam72cam.immersiverailroading.util.Speed;
import cn.wululapuda.irscripts.api.CgGroupControlApi;
import cn.wululapuda.irscripts.api.CouplerControlApi;
import cn.wululapuda.irscripts.api.LocomotiveControlApi;
import cn.wululapuda.irscripts.api.ScriptSoundTracker;
import cn.wululapuda.irscripts.api.StockAnimationApi;
import cn.wululapuda.irscripts.api.StockParticleApi;
import cn.wululapuda.irscripts.api.StockReadoutApi;
import cn.wululapuda.irscripts.api.StockSoundApi;

public final class TrainScriptBindings {
    private final EntityRollingStock stock;
    private final LocomotiveControlApi control;
    private final CouplerControlApi coupler;
    private final StockReadoutApi readout;
    private final StockSoundApi sound;
    private final CgGroupControlApi cgGroup;
    private final StockAnimationApi animation;
    private final StockParticleApi particle;

    public TrainScriptBindings(EntityRollingStock stock, ScriptSoundTracker soundTracker) {
        this.stock = stock;
        this.control = new LocomotiveControlApi(stock);
        this.coupler = new CouplerControlApi(stock);
        this.readout = new StockReadoutApi(stock);
        this.sound = new StockSoundApi(stock, soundTracker);
        this.cgGroup = new CgGroupControlApi(stock);
        this.animation = new StockAnimationApi(stock);
        this.particle = new StockParticleApi(stock);
    }

    public String getUuid() {
        return stock.getUUID().toString();
    }

    public String getDefinitionId() {
        return stock.getDefinitionID();
    }

    public String getTag() {
        return stock.tag;
    }

    public int getTickCount() {
        return stock.getTickCount();
    }

    /** 实际速度 (km/h)。 */
    public double getSpeedKmh() {
        if (stock instanceof EntityMoveableRollingStock) {
            return ((EntityMoveableRollingStock) stock).getCurrentSpeed().metric();
        }
        return 0.0D;
    }

    /** 实际速度 (m/s)。 */
    public double getSpeedMps() {
        if (stock instanceof EntityMoveableRollingStock) {
            return ((EntityMoveableRollingStock) stock).getCurrentSpeed().metersPerSecond();
        }
        return 0.0D;
    }

    public LocomotiveControlApi getControl() {
        return control;
    }

    public CouplerControlApi getCoupler() {
        return coupler;
    }

    public StockReadoutApi getReadout() {
        return readout;
    }

    public StockSoundApi getSound() {
        return sound;
    }

    /** Exposed to scripts as {@code stock.cg_group}. */
    public CgGroupControlApi getCg_group() {
        return cgGroup;
    }

    /** Exposed to scripts as {@code stock.animation}. */
    public StockAnimationApi getAnimation() {
        return animation;
    }

    /** Exposed to scripts as {@code stock.particle}. */
    public StockParticleApi getParticle() {
        return particle;
    }

    public EntityRollingStock getStock() {
        return stock;
    }
}
