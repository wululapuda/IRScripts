package cn.wululapuda.irscripts.gui;

import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cam72cam.immersiverailroading.gui.container.BaseContainer;
import cam72cam.mod.gui.container.IContainer;

import java.lang.reflect.Field;

public final class ContainerStockAccess {
    private ContainerStockAccess() {
    }

    public static EntityRollingStock getStock(IContainer container) {
        if (container == null) {
            return null;
        }
        if (container instanceof BaseContainer) {
            try {
                Field field = container.getClass().getField("stock");
                Object value = field.get(container);
                if (value instanceof EntityRollingStock) {
                    return (EntityRollingStock) value;
                }
            } catch (ReflectiveOperationException ignored) {
                // fall through
            }
        }
        return null;
    }
}
