package cn.wululapuda.irscripts.gui;

import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cam72cam.immersiverailroading.gui.container.BaseContainer;
import cam72cam.mod.gui.container.ClientContainerBuilder;
import cam72cam.mod.gui.container.IContainer;
import cam72cam.mod.gui.container.ServerContainerBuilder;
import cn.wululapuda.irscripts.mixin.IContainerAccess;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.function.Consumer;

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

    public static UUID getStockId(IContainer container) {
        EntityRollingStock stock = getStock(container);
        return stock == null ? null : stock.getUUID();
    }

    public static EntityRollingStock resolveStock(GuiScreen gui) {
        if (gui instanceof GuiInventory) {
            return ScriptButtonOverlay.getRidingStock();
        }
        if (gui instanceof ClientContainerBuilder) {
            return resolveStock((ClientContainerBuilder) gui);
        }
        if (gui instanceof GuiContainer) {
            return resolveStock(((GuiContainer) gui).inventorySlots);
        }
        return null;
    }

    public static EntityRollingStock resolveStock(ClientContainerBuilder gui) {
        try {
            Field serverField = ClientContainerBuilder.class.getDeclaredField("server");
            serverField.setAccessible(true);
            Object server = serverField.get(gui);
            if (server instanceof ServerContainerBuilder) {
                return resolveStock((ServerContainerBuilder) server);
            }
        } catch (ReflectiveOperationException ignored) {
            // fall through
        }
        return null;
    }

    public static EntityRollingStock resolveStock(Container container) {
        if (container instanceof ServerContainerBuilder) {
            return resolveStock((ServerContainerBuilder) container);
        }
        return null;
    }

    public static EntityRollingStock resolveStock(ServerContainerBuilder server) {
        if (server instanceof IContainerAccess) {
            return getStock(((IContainerAccess) server).irscripts$getContainer());
        }

        IContainer captured = extractContainerFromDrawConsumer(server);
        if (captured != null) {
            return getStock(captured);
        }

        try {
            Field mixinField = server.getClass().getDeclaredField("irscripts$container");
            mixinField.setAccessible(true);
            Object value = mixinField.get(server);
            if (value instanceof IContainer) {
                return getStock((IContainer) value);
            }
        } catch (ReflectiveOperationException ignored) {
            // fall through
        }
        return null;
    }

    private static IContainer extractContainerFromDrawConsumer(ServerContainerBuilder server) {
        try {
            Field drawField = ServerContainerBuilder.class.getDeclaredField("draw");
            drawField.setAccessible(true);
            Object drawConsumer = drawField.get(server);
            if (!(drawConsumer instanceof Consumer)) {
                return null;
            }

            for (Field field : drawConsumer.getClass().getDeclaredFields()) {
                if (IContainer.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    return (IContainer) field.get(drawConsumer);
                }
            }
        } catch (ReflectiveOperationException ignored) {
            // fall through
        }
        return null;
    }
}
