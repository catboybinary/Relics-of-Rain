package meow.binary.relicsofrain.item.relic;

import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.*;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemColor;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemShape;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.style.BeamsData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.TooltipData;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import meow.binary.relicsofrain.client.model.ShroomModel;
import meow.binary.relicsofrain.item.AbstractRORItem;
import meow.binary.relicsofrain.network.BustlingFungusUpdatePacket;
import meow.binary.relicsofrain.registry.DataComponentRegistry;
import meow.binary.relicsofrain.registry.ItemRegistry;
import meow.binary.relicsofrain.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Vector3f;
import top.theillusivec4.curios.api.SlotContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BustlingFungusItem extends AbstractRORItem {
    public static final double RADIUS = 2.5d;
    public static final int COOLDOWN = 30;

    public BustlingFungusItem(Properties props) {
        super((new Properties()).rarity(Rarity.COMMON).stacksTo(1));
    }


    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("healing")
                                .maxLevel(20)
                                .stat(StatData.builder("heal_amount")
                                        .initialValue(0.01f, 0.015f)
                                        .formatValue(value -> MathUtils.round(value * 100, 1) * 4)
                                        .upgradeModifier(UpgradeOperation.ADD, 0.005)
                                        .build())
                                .build())
                        .build())
                .style(StyleData.builder()
                        .beams(BeamsData.builder()
                                .startColor(0xFF58f99f)
                                .endColor(0x00005346)
                                .build())
                        .tooltip(TooltipData.builder()
                                .borderBottom(0xff0a7a6c)
                                .borderTop(0xff88ee88)
                                .build())
                        .build())
                .leveling(LevelingData.builder()
                        .sources(LevelingSourcesData.builder()
                                .source(LevelingSourceData.abilityBuilder("healing")
                                        .gem(GemShape.SQUARE, GemColor.GREEN)
                                        .build())
                                .build())
                        .maxLevel(20)
                        .step(175)
                        .initialCost(230)
                        .build())
                .build();
    }


    public List<LivingEntity> findEligibleEntities(LivingEntity entity, double radius) {
        return entity.level().getEntitiesOfClass(LivingEntity.class, new AABB(entity.position(), entity.position()).inflate(radius), e ->
                EntityUtils.isAlliedTo(entity, e)
                        && e.isAlive()
                        && e.getHealth() < e.getMaxHealth()
                        && entity.position().distanceTo(e.position()) <= radius
        );
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        int ticksImmobile = stack.getOrDefault(DataComponentRegistry.TICKS_IMMOBILE, 0);

        if (slotContext.entity().level().isClientSide) return;
        if (!(stack.getItem() instanceof IRelicItem relic)) return;

        boolean isMoving = slotContext.entity().getKnownMovement().multiply(1, slotContext.entity().onGround() ? 0 : 1, 1).lengthSqr() > 0.0005;

        if (!isMoving) {
            if (ticksImmobile < COOLDOWN) stack.set(DataComponentRegistry.TICKS_IMMOBILE, Mth.clamp(ticksImmobile + 1, 0, COOLDOWN));
        } else if (ticksImmobile > 0) stack.set(DataComponentRegistry.TICKS_IMMOBILE, 0);

        if (slotContext.entity().tickCount % 5 != 0 || ticksImmobile < COOLDOWN) return;
        NetworkHandler.sendToClientsTrackingEntityAndSelf(new BustlingFungusUpdatePacket(slotContext.entity().getId()), slotContext.entity());

        List<LivingEntity> entities = findEligibleEntities(slotContext.entity(), RADIUS);
        if (entities.isEmpty()) return;

        for (LivingEntity entity : entities) {
            float toHeal = (float) (relic.getStatValue(stack, "healing", "heal_amount") * slotContext.entity().getMaxHealth());
            entity.heal(toHeal);
            relic.spreadRelicExperience(slotContext.entity(), stack, 1);
        }
    }

    public static Map<Integer, Float> lerpedRadius = new HashMap<>();

    public static float smoothWave(float time, float exponent, double period) {
        // Calculate normalized sine value (range: [0, 1])
        double normalizedSine = 0.5 * (1 + Math.sin((2 * Math.PI * time) / period));

        // Apply power to flatten top and bottom (adjust the exponent for smoothness)
        double smoothWave = Math.pow(normalizedSine, exponent); // Experiment with exponents like 2, 3, or 4

        return (float) smoothWave;
    }

    private static void renderFungi(PoseStack p, LivingEntity livingEntity, ItemStack stack, float partialTick) {
        int light = LightTexture.pack(15, 15);

        int id = livingEntity.getId();
        if (!(stack.getItem() instanceof BustlingFungusItem relic)) return;
        int ticksImmobile = stack.getOrDefault(DataComponentRegistry.TICKS_IMMOBILE, 0);

        // Calculate total time for animation
        float totalTime = (livingEntity.tickCount + partialTick) / 20.0f; // Convert to seconds

        float radius = ticksImmobile >= COOLDOWN ? 2.5f : 0f;
        float tickMultiple = Mth.clamp(livingEntity.level().tickRateManager().tickrate() / 20f, 0, 1);

        lerpedRadius.put(id, Mth.lerp(Mth.clamp(Minecraft.getInstance().getTimer().getRealtimeDeltaTicks() / 3f * tickMultiple, 0, 1), lerpedRadius.getOrDefault(id, 0f), radius));
        float r = lerpedRadius.getOrDefault(id, 0f);
        float ii = Mth.clamp(r - 0.15f, 0f, 1.35f) / 1.5f;
        float l = 0.57735f;

        if (r < 0.025f) return;
        p.pushPose();
        p.mulPose(Axis.YP.rotationDegrees(livingEntity.tickCount + partialTick));
        p.scale(r, 1f, r);

        RenderType type = RenderUtils.getIcosahedronType(RenderUtils.WHITE, VertexFormat.Mode.QUADS);

        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
        VertexConsumer buffer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderUtils.getShroomType(RenderUtils.WHITE));

        int color = FastColor.ARGB32.color(100, (int) (150 * ii), (int) (255 * ii), (int) (110 * ii));

        for (int i = 0; i < 6; i++) {
            builder.addVertex(p.last(), new Vector3f(l, 0.0f, 1)).setColor(color).setUv(0, 0).setLight(light);
            builder.addVertex(p.last(), new Vector3f(l, 0.1f, 1)).setColor(color).setUv(0, 0).setLight(light);
            builder.addVertex(p.last(), new Vector3f(-l, 0.1f, 1)).setColor(color).setUv(0, 0).setLight(light);
            builder.addVertex(p.last(), new Vector3f(-l, 0.0f, 1)).setColor(color).setUv(0, 0).setLight(light);
            p.mulPose(Axis.YP.rotationDegrees(60f));
        }
        MeshData mesh = builder.build();
        if (mesh != null) type.draw(mesh);

        p.popPose();
        for (int i = 0; i < 7; i++) {
            ShroomModel model = new ShroomModel();
            int seed = i * 157 + i;
            Random random = new Random(seed + livingEntity.hashCode());
            random.nextFloat();
            random.nextFloat();
            random.nextFloat();

            p.pushPose();
            p.translate(random.nextFloat(-1.75f, 1.75f), 1.49, random.nextFloat(-1.75f, 1.75f));
            p.scale(-1, -1, 1);

            p.mulPose(Axis.ZP.rotationDegrees(random.nextFloat(-10, 10)));
            p.mulPose(Axis.XP.rotationDegrees(random.nextFloat(-10, 10)));
            float scale = random.nextFloat(0.75f, 1.25f);
            p.scale(scale, 1, scale);

            float popHeight = smoothWave(totalTime + seed, random.nextFloat(8, 13), random.nextFloat(6, 9));
            float alpha = Mth.clamp(1f - popHeight * 2, 0, 1);

            int c = FastColor.ARGB32.color(100, (int) (150 * ii * alpha), (int) (255 * ii * alpha), (int) (110 * ii * alpha));

            p.scale(alpha, 1, alpha);
            p.translate(0, popHeight + 0.05, 0);

            model.renderToBuffer(p, buffer, light, OverlayTexture.NO_OVERLAY, c);

            p.popPose();
        }
    }

    @EventBusSubscriber(Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void renderLevel(RenderLevelStageEvent e) {
            if (e.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
                return;
            }

            for (int id : lerpedRadius.keySet()) {
                Entity entity = e.getCamera().getEntity().level().getEntity(id);
                ItemStack stack = EntityUtils.findEquippedCurio(entity, ItemRegistry.BUSTLING_FUNGUS.get());

                if (!(entity instanceof LivingEntity livingEntity)
                        || !(stack.getItem() instanceof IRelicItem)
                ) continue;

                PoseStack p = e.getPoseStack();
                Vec3 cp = e.getCamera().getPosition();
                float partialTick = e.getPartialTick().getGameTimeDeltaPartialTick(true);
                Vec3 pp = livingEntity.getPosition(partialTick);

                p.pushPose();
                p.translate(pp.x - cp.x, pp.y - cp.y, pp.z - cp.z);
                renderFungi(p, livingEntity, stack, partialTick);
                p.popPose();
            }
        }
    }
}
