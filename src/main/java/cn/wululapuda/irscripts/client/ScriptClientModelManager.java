package cn.wululapuda.irscripts.client;

import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cam72cam.mod.math.Vec3d;
import cam72cam.mod.model.obj.OBJModel;
import cam72cam.mod.render.GlobalRender;
import cam72cam.mod.render.obj.OBJRender;
import cam72cam.mod.render.opengl.RenderState;
import cam72cam.mod.resource.Identifier;
import cam72cam.mod.world.World;
import cn.wululapuda.irscripts.model.ModelCoordinateSystem;
import cn.wululapuda.irscripts.model.ModelLoader;
import cn.wululapuda.irscripts.net.ScriptModelRenderPacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@SideOnly(Side.CLIENT)
public final class ScriptClientModelManager {
    private static final Map<UUID, ActiveRender> ACTIVE = new ConcurrentHashMap<>();
    private static boolean registered;

    private ScriptClientModelManager() {
    }

    public static void init() {
        if (!registered) {
            GlobalRender.registerRender(ScriptClientModelManager::renderAll);
            registered = true;
        }
    }

    public static void applyPacket(ScriptModelRenderPacket packet) {
        if (packet.isRemove()) {
            ACTIVE.remove(packet.getRenderId());
            return;
        }

        List<String> groups = splitGroups(packet.getGroups());
        if (packet.getModelPath() == null || packet.getModelPath().isEmpty() || groups.isEmpty()) {
            return;
        }

        ACTIVE.put(packet.getRenderId(), new ActiveRender(packet, groups));
    }

    public static void purgeMissingStocks(World world) {
        Iterator<Map.Entry<UUID, ActiveRender>> iterator = ACTIVE.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, ActiveRender> entry = iterator.next();
            if (world.getEntity(entry.getValue().stockId, EntityRollingStock.class) == null) {
                iterator.remove();
            }
        }
    }

    private static void renderAll(RenderState state, float partialTicks) {
        if (ACTIVE.isEmpty()) {
            return;
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.world == null) {
            return;
        }

        World world = World.get(minecraft.world);
        if (world == null) {
            return;
        }

        Iterator<Map.Entry<UUID, ActiveRender>> iterator = ACTIVE.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, ActiveRender> entry = iterator.next();
            ActiveRender render = entry.getValue();
            EntityRollingStock stock = world.getEntity(render.stockId, EntityRollingStock.class);
            if (stock == null) {
                iterator.remove();
                continue;
            }
            render.draw(state, stock, partialTicks);
        }
    }

    private static List<String> splitGroups(String groups) {
        if (groups == null || groups.isEmpty()) {
            return new ArrayList<>();
        }
        String[] parts = groups.split("\u001f", -1);
        return new ArrayList<>(Arrays.asList(parts));
    }

    private static final class ActiveRender {
        private final UUID stockId;
        private final String modelPath;
        private final List<String> groups;
        private final float posX;
        private final float posY;
        private final float posZ;
        private final float originX;
        private final float originY;
        private final float originZ;
        private final float scaleX;
        private final float scaleY;
        private final float scaleZ;
        private final float rotX;
        private final float rotY;
        private final float rotZ;
        private final float pivotX;
        private final float pivotY;
        private final float pivotZ;
        private final ModelCoordinateSystem coordinates = new ModelCoordinateSystem();
        private OBJModel cachedModel;

        private ActiveRender(ScriptModelRenderPacket packet, List<String> groups) {
            this.stockId = packet.getStockId();
            this.modelPath = packet.getModelPath();
            this.groups = groups;
            this.posX = packet.getPosX();
            this.posY = packet.getPosY();
            this.posZ = packet.getPosZ();
            this.originX = packet.getOriginX();
            this.originY = packet.getOriginY();
            this.originZ = packet.getOriginZ();
            this.scaleX = packet.getScaleX();
            this.scaleY = packet.getScaleY();
            this.scaleZ = packet.getScaleZ();
            this.rotX = packet.getRotX();
            this.rotY = packet.getRotY();
            this.rotZ = packet.getRotZ();
            this.pivotX = packet.getPivotX();
            this.pivotY = packet.getPivotY();
            this.pivotZ = packet.getPivotZ();
            coordinates.setCenter(new double[] {packet.getCenterX(), packet.getCenterY(), packet.getCenterZ()});
            coordinates.setNormal(new double[] {packet.getNormalX(), packet.getNormalY(), packet.getNormalZ()});
        }

        private void draw(RenderState state, EntityRollingStock stock, float partialTicks) {
            try {
                if (cachedModel == null) {
                    double scale = stock.gauge != null ? stock.gauge.scale() : 1.0D;
                    cachedModel = ModelLoader.load(new Identifier(modelPath), scale);
                }

                double gaugeScale = stock.gauge != null ? stock.gauge.scale() : 1.0D;
                Vec3d cameraPos = GlobalRender.getCameraPos(partialTicks);

                RenderState renderState = state.clone();
                coordinates.applyWorldOrigin(renderState, cameraPos);
                coordinates.applyLocalAxes(renderState);
                renderState.translate(posX * gaugeScale, posY * gaugeScale, posZ * gaugeScale);
                renderState.translate(originX * gaugeScale, originY * gaugeScale, originZ * gaugeScale);
                renderState.translate(pivotX * gaugeScale, pivotY * gaugeScale, pivotZ * gaugeScale);
                renderState.rotate(rotX, 1.0D, 0.0D, 0.0D);
                renderState.rotate(rotY, 0.0D, 1.0D, 0.0D);
                renderState.rotate(rotZ, 0.0D, 0.0D, 1.0D);
                renderState.scale(scaleX, scaleY, scaleZ);
                renderState.translate(-pivotX * gaugeScale, -pivotY * gaugeScale, -pivotZ * gaugeScale);

                try (OBJRender.Binding binding = cachedModel.binder().bind(renderState)) {
                    binding.draw(groups);
                }
            } catch (Exception ignored) {
                // Model may still be loading or missing on client.
            }
        }
    }
}
