package cn.wululapuda.irscripts.api;

import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cam72cam.immersiverailroading.gui.overlay.Readouts;
import cam72cam.immersiverailroading.library.ModelComponentType;

import java.util.OptionalDouble;

public final class StockReadoutApi {
    private final EntityRollingStock stock;

    public StockReadoutApi(EntityRollingStock stock) {
        this.stock = stock;
    }

    /** 液体装载百分比，不可装载液体时为 0。 */
    public double getLiquid() {
        return Readouts.LIQUID.getValue(stock);
    }

    /** 当前速度与最大速度之比。 */
    public double getSpeed() {
        return Readouts.SPEED.getValue(stock);
    }

    /** 蒸汽机车为当前温度/100，内燃机车为当前温度/150。 */
    public double getTemperature() {
        return Readouts.TEMPERATURE.getValue(stock);
    }

    /** 货物装载百分比，不可装货时为 0。 */
    public double getCargoFill() {
        return Readouts.CARGO_FILL.getValue(stock);
    }

    /** 蒸汽机车锅炉压力与 JSON maxPSI 之比。 */
    public double getBoilerPressure() {
        return Readouts.BOILER_PRESSURE.getValue(stock);
    }

    /** 内燃机车实际 RPM/节流（GUI 红线，相对设定值有延迟）。 */
    public double getEngineRpm() {
        return Readouts.ENGINE_RPM.getValue(stock);
    }

    /** 全列刹车杆位置/大小。 */
    public double getTrainBrakeLever() {
        if (stock.getDefinition().isLinearBrakeControl()) {
            return Readouts.TRAIN_BRAKE.getValue(stock);
        }
        OptionalDouble lever = stock.getDefinition().getModel().getControls().stream()
                .filter(control -> control.part.type == ModelComponentType.TRAIN_BRAKE_X)
                .mapToDouble(stock::getControlPosition)
                .max();
        return lever.isPresent() ? lever.getAsDouble() : Readouts.TRAIN_BRAKE.getValue(stock);
    }

    /** 整列制动压力。 */
    public double getBrakePressure() {
        return Readouts.BRAKE_PRESSURE.getValue(stock);
    }

    /** 前耦合器是否已连挂。 */
    public double getCoupledFront() {
        return Readouts.COUPLED_FRONT.getValue(stock);
    }

    /** 后耦合器是否已连挂。 */
    public double getCoupledRear() {
        return Readouts.COUPLED_REAR.getValue(stock);
    }

    /** 前耦合器松弛量与最大松弛量之比。 */
    public double getCouplerSlackFront() {
        return Readouts.COUPLER_SLACK_FRONT.getValue(stock);
    }

    /** 后耦合器松弛量与最大松弛量之比。 */
    public double getCouplerSlackRear() {
        return Readouts.COUPLER_SLACK_REAR.getValue(stock);
    }

    /** 铃是否在响。 */
    public double getBell() {
        return Readouts.BELL.getValue(stock);
    }

    /** 汽笛/喇叭是否在响。 */
    public double getHorn() {
        return Readouts.HORN.getValue(stock);
    }

    /** 与 getHorn 相同，兼容 WHISTLE 读数。 */
    public double getWhistle() {
        return Readouts.WHISTLE.getValue(stock);
    }

    /** 前转向架偏转角映射到 0~1。 */
    public double getFrontBogeyAngle() {
        return Readouts.FRONT_BOGEY_ANGLE.getValue(stock);
    }

    /** 后转向架偏转角映射到 0~1。 */
    public double getRearBogeyAngle() {
        return Readouts.REAR_BOGEY_ANGLE.getValue(stock);
    }

    /** 前机车架偏转角映射到 0~1。 */
    public double getFrontLocomotiveAngle() {
        return Readouts.FRONT_LOCOMOTIVE_ANGLE.getValue(stock);
    }

    /** 后机车架偏转角映射到 0~1。 */
    public double getRearLocomotiveAngle() {
        return Readouts.REAR_LOCOMOTIVE_ANGLE.getValue(stock);
    }

    /** 蒸汽机车汽缸排水阀是否开启。 */
    public double getCylinderDrain() {
        return Readouts.CYLINDER_DRAIN.getValue(stock);
    }
}
