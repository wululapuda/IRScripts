package cn.wululapuda.irscripts.client;

import cam72cam.immersiverailroading.ConfigGraphics;
import cam72cam.immersiverailroading.entity.EntityMoveableRollingStock;
import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cam72cam.immersiverailroading.library.Particles;
import cam72cam.immersiverailroading.render.SmokeParticle;
import cam72cam.immersiverailroading.util.VecUtil;
import cam72cam.mod.math.Vec3d;
import cam72cam.mod.resource.Identifier;
import cam72cam.mod.world.World;
import cn.wululapuda.irscripts.api.ParticlePathUtil;
import cn.wululapuda.irscripts.net.ScriptParticlePacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@SideOnly(Side.CLIENT)
public final class ScriptClientParticleManager {
    private static final List<ActiveEffect> ACTIVE = new ArrayList<>();

    private ScriptClientParticleManager() {
    }

    public static void init() {
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new ScriptClientParticleManager());
    }

    public static void startEffect(
            UUID stockId,
            float startX,
            float startY,
            float startZ,
            float offsetX,
            float offsetY,
            float offsetZ,
            float speed,
            float time,
            float concentration,
            byte type,
            String texture
    ) {
        ACTIVE.add(new ActiveEffect(
                stockId,
                startX,
                startY,
                startZ,
                offsetX,
                offsetY,
                offsetZ,
                speed,
                Math.max(1, Math.round(time * 20.0F)),
                concentration,
                type,
                texture
        ));
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (ACTIVE.isEmpty()) {
            return;
        }

        Iterator<ActiveEffect> iterator = ACTIVE.iterator();
        while (iterator.hasNext()) {
            ActiveEffect effect = iterator.next();
            if (!effect.tick()) {
                iterator.remove();
            }
        }
    }

    private static final class ActiveEffect {
        private final UUID stockId;
        private final float startX;
        private final float startY;
        private final float startZ;
        private final float offsetX;
        private final float offsetY;
        private final float offsetZ;
        private final float speed;
        private final float concentration;
        private final byte type;
        private final String texture;
        private int remainingTicks;

        private ActiveEffect(
                UUID stockId,
                float startX,
                float startY,
                float startZ,
                float offsetX,
                float offsetY,
                float offsetZ,
                float speed,
                int durationTicks,
                float concentration,
                byte type,
                String texture
        ) {
            this.stockId = stockId;
            this.startX = startX;
            this.startY = startY;
            this.startZ = startZ;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.offsetZ = offsetZ;
            this.speed = speed;
            this.remainingTicks = durationTicks;
            this.concentration = concentration;
            this.type = type;
            this.texture = texture;
        }

        private boolean tick() {
            if (remainingTicks <= 0) {
                return false;
            }
            remainingTicks--;

            if (!ConfigGraphics.particlesEnabled || Particles.SMOKE == null) {
                return remainingTicks > 0;
            }

            Minecraft minecraft = Minecraft.getMinecraft();
            if (minecraft.world == null) {
                return false;
            }

            World world = World.get(minecraft.world);
            if (world == null) {
                return false;
            }

            EntityRollingStock stock = world.getEntity(stockId, EntityRollingStock.class);
            if (stock == null) {
                return false;
            }

            spawnParticle(stock);
            return remainingTicks > 0;
        }

        private void spawnParticle(EntityRollingStock stock) {
            double gauge = stock.gauge.scale();
            Vec3d localStart = new Vec3d(
                    startX + offsetX,
                    startY + offsetY,
                    startZ + offsetZ
            ).scale(gauge);
            Vec3d worldStart = VecUtil.rotateWrongYaw(localStart, stock.getRotationYaw() + 180.0F);

            Vec3d velocity = Vec3d.ZERO;
            if (stock instanceof EntityMoveableRollingStock) {
                velocity = ((EntityMoveableRollingStock) stock).getVelocity();
            }

            Vec3d particlePos = stock.getPosition().add(worldStart).subtract(velocity);
            double verticalSpeed = speed * gauge;
            Vec3d motion = new Vec3d(velocity.x, velocity.y + verticalSpeed, velocity.z);

            float darken;
            float thickness;
            int lifespan;
            double diameter;
            Identifier particleTexture;

            if (type == ScriptParticlePacket.TYPE_STEAM) {
                darken = 0.0F;
                thickness = 0.15F + Math.min(0.35F, speed * 0.1F);
                lifespan = Math.max(20, (int) (80.0F * (1.0F + speed * 0.25F)));
                diameter = 0.45D * gauge;
                particleTexture = resolveTexture(stock, stock.getDefinition().steamParticleTexture);
            } else {
                float concentrationValue = Math.max(0.05F, Math.min(1.0F, concentration));
                darken = concentrationValue * 0.75F;
                thickness = concentrationValue;
                lifespan = Math.max(20, (int) (40.0F * (1.0F + concentrationValue)));
                diameter = (0.35D + concentrationValue * 0.25D) * gauge;
                particleTexture = resolveTexture(stock, stock.getDefinition().smokeParticleTexture);
            }

            Particles.SMOKE.accept(new SmokeParticle.SmokeParticleData(
                    stock.getWorld(),
                    particlePos,
                    motion,
                    lifespan,
                    darken,
                    thickness,
                    diameter,
                    particleTexture
            ));
        }

        private Identifier resolveTexture(EntityRollingStock stock, Identifier fallback) {
            if (texture == null || texture.isEmpty()) {
                return fallback;
            }
            return ParticlePathUtil.resolve(stock, texture);
        }
    }
}
