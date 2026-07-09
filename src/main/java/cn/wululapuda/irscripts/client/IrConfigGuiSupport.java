package cn.wululapuda.irscripts.client;

import cam72cam.mod.config.ConfigFile;
import cam72cam.mod.config.ConfigGui;
import cam72cam.mod.entity.Player;
import cam72cam.mod.gui.screen.Button;
import cam72cam.mod.gui.screen.IScreenBuilder;
import cam72cam.mod.gui.screen.ScreenBuilder;
import cn.wululapuda.irscripts.config.IRScriptsModConfig;
import cn.wululapuda.irscripts.config.ScriptRuntimeSettings;
import cn.wululapuda.irscripts.net.ScriptConfigSyncPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

public final class IrConfigGuiSupport {
    private static final Set<ConfigGui> ROOTS_WITH_BUTTON = Collections.newSetFromMap(new WeakHashMap<>());

    private static Field parentField;
    private static Field pcField;
    private static Field widgetsField;

    private IrConfigGuiSupport() {
    }

    public static boolean shouldAddIrScriptsEntry(ConfigGui configGui) {
        if (getParent(configGui) != null || getPropertyClass(configGui) != null) {
            return false;
        }
        List<?> widgets = getWidgets(configGui);
        if (widgets == null || widgets.size() != 4) {
            return false;
        }
        return ROOTS_WITH_BUTTON.add(configGui);
    }

    public static void addIrScriptsEntry(ConfigGui rootGui, IScreenBuilder screen) {
        List<?> widgets = getWidgets(rootGui);
        int index = widgets != null ? widgets.size() : 0;
        Button button = new Button(
                screen,
                -100,
                index * 20,
                200,
                20,
                I18n.format("irscripts.config.button.open")
        ) {
            @Override
            public void onClick(Player.Hand hand) {
                openIrScriptsConfig(rootGui);
            }
        };
        screen.addButton(button);
    }

    public static void openIrScriptsConfig(ConfigGui rootGui) {
        try {
            Object configInstance = createConfigInstance(IRScriptsModConfig.class);
            readConfigInstance(configInstance);
            Object propertyClass = getPropertyClass(configInstance);
            Constructor<ConfigGui> constructor = ConfigGui.class.getDeclaredConstructor(
                    ConfigGui.class,
                    propertyClass.getClass(),
                    configInstance.getClass()
            );
            ConfigGui detailGui = constructor.newInstance(rootGui, propertyClass, configInstance);
            Minecraft.getMinecraft().displayGuiScreen(new ScreenBuilder(detailGui, () -> true));
        } catch (ReflectiveOperationException error) {
            throw new IllegalStateException("Failed to open IR Scripts config screen", error);
        }
    }

    public static void syncIrScriptsConfigIfClosing(ConfigGui configGui) {
        if (configGui == null || !isIrScriptsDetailScreen(configGui)) {
            return;
        }

        ConfigFile.write(IRScriptsModConfig.class);
        ScriptRuntimeSettings.applyFromConfig();
        if (Minecraft.getMinecraft().world != null) {
            new ScriptConfigSyncPacket(IRScriptsModConfig.scriptPrint, IRScriptsModConfig.debug).sendToServer();
        }
    }

    private static boolean isIrScriptsDetailScreen(ConfigGui configGui) {
        if (configGui == null) {
            return false;
        }
        Object propertyClass = getPropertyClass(configGui);
        if (propertyClass == null) {
            return false;
        }
        try {
            Field classField = propertyClass.getClass().getDeclaredField("cls");
            classField.setAccessible(true);
            Object value = classField.get(propertyClass);
            return IRScriptsModConfig.class.equals(value);
        } catch (ReflectiveOperationException ignored) {
            return false;
        }
    }

    private static ConfigGui getParent(ConfigGui configGui) {
        try {
            return (ConfigGui) getParentField().get(configGui);
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    private static Object getPropertyClass(ConfigGui configGui) {
        try {
            return getPcField().get(configGui);
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private static List<?> getWidgets(ConfigGui configGui) {
        try {
            return (List<?>) getWidgetsField().get(configGui);
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    private static Field getParentField() throws NoSuchFieldException {
        if (parentField == null) {
            parentField = ConfigGui.class.getDeclaredField("parent");
            parentField.setAccessible(true);
        }
        return parentField;
    }

    private static Field getPcField() throws NoSuchFieldException {
        if (pcField == null) {
            pcField = ConfigGui.class.getDeclaredField("pc");
            pcField.setAccessible(true);
        }
        return pcField;
    }

    private static Field getWidgetsField() throws NoSuchFieldException {
        if (widgetsField == null) {
            widgetsField = ConfigGui.class.getDeclaredField("widgets");
            widgetsField.setAccessible(true);
        }
        return widgetsField;
    }

    private static Object createConfigInstance(Class<?> configClass) throws ReflectiveOperationException {
        Class<?> configInstanceClass = Class.forName("cam72cam.mod.config.ConfigFile$ConfigInstance");
        Constructor<?> constructor = configInstanceClass.getDeclaredConstructor(Class.class);
        constructor.setAccessible(true);
        return constructor.newInstance(configClass);
    }

    private static void readConfigInstance(Object configInstance) throws ReflectiveOperationException {
        Method read = configInstance.getClass().getDeclaredMethod("read");
        read.setAccessible(true);
        read.invoke(configInstance);
    }

    private static Object getPropertyClass(Object configInstance) throws ReflectiveOperationException {
        Field propertyClassField = configInstance.getClass().getDeclaredField("pc");
        propertyClassField.setAccessible(true);
        return propertyClassField.get(configInstance);
    }
}
