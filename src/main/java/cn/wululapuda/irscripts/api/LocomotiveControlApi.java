package cn.wululapuda.irscripts.api;

import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cam72cam.immersiverailroading.entity.Locomotive;
import cam72cam.immersiverailroading.entity.LocomotiveDiesel;
import cam72cam.immersiverailroading.entity.LocomotiveSteam;
import cn.wululapuda.irscripts.IRScripts;

public final class LocomotiveControlApi {
    private final EntityRollingStock stock;

    public LocomotiveControlApi(EntityRollingStock stock) {
        this.stock = stock;
    }

    public String getType() {
        if (stock instanceof LocomotiveDiesel) {
            return "diesel";
        }
        if (stock instanceof LocomotiveSteam) {
            return "steam";
        }
        if (stock instanceof Locomotive) {
            return "locomotive";
        }
        return "none";
    }

    public boolean isLocomotive() {
        return stock instanceof Locomotive;
    }

    public boolean isDiesel() {
        return stock instanceof LocomotiveDiesel;
    }

    public boolean isSteam() {
        return stock instanceof LocomotiveSteam;
    }

    public void setEngine(double value) {
        if (!requireServer("setEngine")) {
            return;
        }
        LocomotiveDiesel diesel = asDiesel("setEngine");
        if (diesel == null) {
            return;
        }
        diesel.setTurnedOn(value >= 0.5D);
    }

    public double getEngine() {
        LocomotiveDiesel diesel = asDiesel(null);
        if (diesel == null) {
            return 0.0D;
        }
        return diesel.isTurnedOn() ? 1.0D : 0.0D;
    }

    public void setTrainBrake(double value) {
        if (!requireServer("setTrainBrake")) {
            return;
        }
        Locomotive locomotive = asLocomotive("setTrainBrake");
        if (locomotive == null) {
            return;
        }
        locomotive.setTrainBrake(clamp01(value));
    }

    public double getTrainBrake() {
        Locomotive locomotive = asLocomotive(null);
        return locomotive == null ? 0.0D : locomotive.getTrainBrake();
    }

    public void setIndependentBrake(double value) {
        if (!requireServer("setIndependentBrake")) {
            return;
        }
        Locomotive locomotive = asLocomotive("setIndependentBrake");
        if (locomotive == null) {
            return;
        }
        locomotive.setIndependentBrake(clamp01(value));
    }

    public double getIndependentBrake() {
        Locomotive locomotive = asLocomotive(null);
        return locomotive == null ? 0.0D : locomotive.getIndependentBrake();
    }

    public void setThrottle(double value) {
        if (!requireServer("setThrottle")) {
            return;
        }
        Locomotive locomotive = asLocomotive("setThrottle");
        if (locomotive == null) {
            return;
        }
        locomotive.setThrottle(clamp01(value));
    }

    public double getThrottle() {
        Locomotive locomotive = asLocomotive(null);
        return locomotive == null ? 0.0D : locomotive.getThrottle();
    }

    public void setReverser(double value) {
        if (!requireServer("setReverser")) {
            return;
        }
        Locomotive locomotive = asLocomotive("setReverser");
        if (locomotive == null) {
            return;
        }
        if (stock instanceof LocomotiveDiesel) {
            locomotive.setReverser(mapDieselReverser(value));
        } else if (stock instanceof LocomotiveSteam) {
            locomotive.setReverser(clamp01(value));
        } else {
            locomotive.setReverser((float) clamp(value, -1.0D, 1.0D));
        }
    }

    public double getReverser() {
        Locomotive locomotive = asLocomotive(null);
        if (locomotive == null) {
            return 0.0D;
        }
        if (stock instanceof LocomotiveSteam) {
            return clamp01(locomotive.getReverser());
        }
        return locomotive.getReverser();
    }

    private Locomotive asLocomotive(String action) {
        if (stock instanceof Locomotive) {
            return (Locomotive) stock;
        }
        if (action != null) {
            IRScripts.logger.warn("[IRScripts] {} ignored: stock {} is not a locomotive", action, stock.getDefinitionID());
        }
        return null;
    }

    private LocomotiveDiesel asDiesel(String action) {
        if (stock instanceof LocomotiveDiesel) {
            return (LocomotiveDiesel) stock;
        }
        if (action != null) {
            IRScripts.logger.warn("[IRScripts] {} ignored: stock {} is not a diesel locomotive", action, stock.getDefinitionID());
        }
        return null;
    }

    private boolean requireServer(String action) {
        if (stock.getWorld().isServer) {
            return true;
        }
        IRScripts.logger.warn("[IRScripts] {} ignored: control API is server-side only", action);
        return false;
    }

    private static float mapDieselReverser(double value) {
        if (value <= -0.5D) {
            return -1.0F;
        }
        if (value >= 0.5D) {
            return 1.0F;
        }
        return 0.0F;
    }

    private static float clamp01(double value) {
        return (float) clamp(value, 0.0D, 1.0D);
    }

    private static double clamp(double value, double min, double max) {
        return Math.min(max, Math.max(min, value));
    }
}
