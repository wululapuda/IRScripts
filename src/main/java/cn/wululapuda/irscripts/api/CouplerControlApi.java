package cn.wululapuda.irscripts.api;

import cam72cam.immersiverailroading.entity.EntityCoupleableRollingStock;
import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cam72cam.immersiverailroading.gui.overlay.Readouts;
import cn.wululapuda.irscripts.IRScripts;

public final class CouplerControlApi {
    private final EntityRollingStock stock;

    public CouplerControlApi(EntityRollingStock stock) {
        this.stock = stock;
    }

    public boolean isCoupleable() {
        return stock instanceof EntityCoupleableRollingStock;
    }

    public void setCouplerFront(double value) {
        setCoupler(EntityCoupleableRollingStock.CouplerType.FRONT, value, "setCouplerFront");
    }

    public void setCouplerRear(double value) {
        setCoupler(EntityCoupleableRollingStock.CouplerType.BACK, value, "setCouplerRear");
    }

    public double getCouplerFront() {
        return Readouts.COUPLER_FRONT.getValue(stock);
    }

    public double getCouplerRear() {
        return Readouts.COUPLER_REAR.getValue(stock);
    }

    public double getCoupledFront() {
        return Readouts.COUPLED_FRONT.getValue(stock);
    }

    public double getCoupledRear() {
        return Readouts.COUPLED_REAR.getValue(stock);
    }

    public double getSlackFront() {
        return Readouts.COUPLER_SLACK_FRONT.getValue(stock);
    }

    public double getSlackRear() {
        return Readouts.COUPLER_SLACK_REAR.getValue(stock);
    }

    private void setCoupler(EntityCoupleableRollingStock.CouplerType type, double value, String action) {
        if (!requireServer(action)) {
            return;
        }
        if (!(stock instanceof EntityCoupleableRollingStock)) {
            IRScripts.logger.warn("[IRScripts] {} ignored: stock {} is not coupleable", action, stock.getDefinitionID());
            return;
        }
        boolean engaged = value >= 0.5D;
        ((EntityCoupleableRollingStock) stock).setCouplerEngaged(type, engaged);
    }

    private boolean requireServer(String action) {
        if (stock.getWorld().isServer) {
            return true;
        }
        IRScripts.logger.warn("[IRScripts] {} ignored: coupler API is server-side only", action);
        return false;
    }
}
