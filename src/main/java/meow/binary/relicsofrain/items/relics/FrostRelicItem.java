package meow.binary.relicsofrain.items.relics;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import it.hurts.sskirillss.relics.client.models.items.CurioModel;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.IRenderableCurio;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.*;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemColor;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemShape;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.style.BeamsData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import meow.binary.relicsofrain.RelicsOfRain;
import meow.binary.relicsofrain.api.IProcCoefficient;
import meow.binary.relicsofrain.api.ItemDamageSource;
import meow.binary.relicsofrain.effects.OnKillEffect;
import meow.binary.relicsofrain.items.AbstractRORItem;
import meow.binary.relicsofrain.network.S2CFrostRelicUpdate;
import meow.binary.relicsofrain.registries.DataComponentRegistry;
import meow.binary.relicsofrain.registries.ItemRegistry;
import meow.binary.relicsofrain.registries.RarityRegistry;
import meow.binary.relicsofrain.util.EntityUtils;
import meow.binary.relicsofrain.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EventBusSubscriber
public class FrostRelicItem extends AbstractRORItem implements IRenderableCurio, OnKillEffect, IProcCoefficient {
    private static double STARTING_RADIUS = 4d;

    public FrostRelicItem(Properties props) {
        super((new Properties()).rarity(RarityRegistry.LEGENDARY_RARITY.getValue()).stacksTo(1));
    }

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("ice_storm")
                                .stat(StatData.builder("damage_percentage")
                                        .initialValue(0.3d, 0.5d)
                                        .upgradeModifier(UpgradeOperation.ADD, 0.5)
                                        .formatValue(value -> (int) (Math.round(value * 100) * 4))
                                        .build())
                                .stat(StatData.builder("max_radius")
                                        .initialValue(8, 10)
                                        .upgradeModifier(UpgradeOperation.ADD, 3)
                                        .formatValue(Math::round)
                                        .build())
                                .maxLevel(5)
                                .build())
                        .build())
                .leveling(LevelingData.builder()
                        .maxLevel(5)
                        .initialCost(250)
                        .step(175)
                        .sources(LevelingSourcesData.builder()
                                .source(LevelingSourceData.abilityBuilder("ice_storm")
                                        .gem(GemShape.SQUARE, GemColor.BLUE)
                                        .build())
                                .build())
                        .build())
                .style(StyleData.builder()
                        .beams(BeamsData.builder()
                                .startColor(0xFFFF2538)
                                .endColor(0x00FF2538)
                                .build())
                        .build())
                .build();
    }

    @Override
    public float getProcCoefficient() {
        return 0.2f;
    }

    public double getCurrentRadius(List<Integer> timers, double maxRadius) {
        return Math.min(!timers.isEmpty() ? STARTING_RADIUS + (timers.size() - 1) * 2 : 0, maxRadius);
    }

    public List<LivingEntity> findEligibleEntities(LivingEntity entity, double radius) {
        return entity.level().getEntitiesOfClass(LivingEntity.class, new AABB(entity.position(), entity.position()).inflate(radius), e ->
                !EntityUtils.isAlliedTo(entity, e)
                        && e.isAlive()
                        && entity.position().distanceTo(e.position()) <= radius
        );
    }

    @Override
    public int onKill(LivingDeathEvent e) {
        int exitValue = OnKillEffect.super.onKill(e);
        if (exitValue != 1) return exitValue;

        Entity causingEntity = e.getSource().getEntity();
        Entity source = e.getSource().getDirectEntity();
        LivingEntity target = e.getEntity();

        ItemStack stack = EntityUtils.findEquippedCurio(causingEntity, ItemRegistry.FROST_RELIC.get());
        if (!(stack.getItem() instanceof IRelicItem relic)) return -1;
        List<Integer> timers = Lists.newArrayList(stack.getOrDefault(DataComponentRegistry.TIMER_LIST, new ArrayList<>()));
        double maxRadius = relic.getStatValue(stack, "ice_storm", "max_radius");

        if (getCurrentRadius(timers, maxRadius) < maxRadius) {
            timers.add(120);
        } else {
            timers.set(0, 120);
        }

        relic.spreadRelicExperience(causingEntity instanceof LivingEntity living ? living : null, stack, 1);
        stack.set(DataComponentRegistry.TIMER_LIST, timers);
        if (causingEntity != null) NetworkHandler.sendToClientsTrackingEntityAndSelf(new S2CFrostRelicUpdate(causingEntity.getId()), causingEntity);

        return 1;
    }


    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        super.curioTick(slotContext, stack);
        List<Integer> timers = Lists.newArrayList(stack.getOrDefault(DataComponentRegistry.TIMER_LIST, new ArrayList<>()));
        if (slotContext.entity().level().isClientSide) {
            //if (slotContext.entity() == Minecraft.getInstance().player) Minecraft.getInstance().player.displayClientMessage(Component.literal(timers.toString()), true);
            return;
        }
        if (!(stack.getItem() instanceof IRelicItem relic)) return;

        double maxRadius = relic.getStatValue(stack, "ice_storm", "max_radius");
        double radius = getCurrentRadius(timers, maxRadius);
        if (radius > 0) {
            Vec3 pos = slotContext.entity().getEyePosition().add(radius * Mth.sin(slotContext.entity().tickCount / 16f), 0, radius * Mth.cos(slotContext.entity().tickCount / 16f));
            Vec3 pos2 = slotContext.entity().getEyePosition().subtract(radius * Mth.sin(slotContext.entity().tickCount / 16f), 0, radius * Mth.cos(slotContext.entity().tickCount / 16f));
            ((ServerLevel) slotContext.entity().level()).sendParticles(ParticleTypes.SNOWFLAKE, pos.x, pos.y, pos.z, 1, 0, 0, 0, 0);
            ((ServerLevel) slotContext.entity().level()).sendParticles(ParticleTypes.SNOWFLAKE, pos2.x, pos2.y, pos2.z, 1, 0, 0, 0, 0);

        }

        timers.replaceAll(i -> i - 1);
        timers.removeIf(i -> i == 0);
        stack.set(DataComponentRegistry.TIMER_LIST, timers);

        if (slotContext.entity().tickCount % 5 == 0) {
            double baseDamage = slotContext.entity().getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
            if (baseDamage == 0) return;

            List<LivingEntity> toHurt = findEligibleEntities(slotContext.entity(), radius);
            for (LivingEntity entity : toHurt) {
                entity.invulnerableTime = 0;
                if (entity.hurt(
                        ItemDamageSource.get(DamageTypes.FREEZE, slotContext.entity(), slotContext.entity(), stack),
                        (float) (baseDamage * relic.getStatValue(stack, "ice_storm", "damage_percentage"))
                )) {
                    entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30, 1, false, true), slotContext.entity());
                    entity.invulnerableTime = 0;
                }
            }
        }
    }

    @Override
    public List<String> headParts() {
        return Lists.newArrayList("left_leg");
    }

    @Override
    public LayerDefinition constructLayerDefinition() {

        MeshDefinition meshdefinition = HumanoidModel.createMesh(new CubeDeformation(0.4f), 0);
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 1).addBox(2.61F, 0.0F, -2.5F, 0.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 16, 16);
    }

    public static Map<Integer, Float> lerpedRadius = new HashMap<>();

    float ticker = 0f;

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        CurioModel model = this.getModel(stack);
        matrixStack.pushPose();
        LivingEntity entity = slotContext.entity();
        ICurioRenderer.followBodyRotations(entity, model);
        model.prepareMobModel(entity, limbSwing*0.76f, limbSwingAmount/4f, partialTicks);
        model.setupAnim(entity, limbSwing*0.76f, limbSwingAmount/4f, ageInTicks, netHeadYaw, headPitch);
        VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(renderTypeBuffer, RenderType.armorCutoutNoCull(this.getTexture(stack)), stack.hasFoil());
        matrixStack.translate(0,-0.125,0);
        model.renderToBuffer(matrixStack, vertexconsumer, light, OverlayTexture.NO_OVERLAY);
        matrixStack.popPose();
    }

    public static void renderIceStorm(PoseStack poseStack, LivingEntity livingEntity, ItemStack stack, float partialTicks) {
        int id = livingEntity.getId();
        if (!(stack.getItem() instanceof FrostRelicItem relic)) return;
        List<Integer> timers = Lists.newArrayList(stack.getOrDefault(DataComponentRegistry.TIMER_LIST, new ArrayList<>()));
        float maxRadius = (float) relic.getStatValue(stack, "ice_storm", "max_radius");
        float radius = (float) relic.getCurrentRadius(timers, maxRadius);
        lerpedRadius.put(id, Mth.lerp(Mth.clamp(Minecraft.getInstance().getTimer().getRealtimeDeltaTicks()/4f,0,1), lerpedRadius.getOrDefault(id, 0f), radius));
        float r = lerpedRadius.getOrDefault(id, 0f);

        float ii = Mth.clamp(r-0.15f, 0f, 1.35f)/1.5f;
//        matrixStack.pushPose();
//        matrixStack.scale(-0.05f, 0.05f, 0.05f);
//        Minecraft.getInstance().font.drawInBatch("TEST "+(ageInTicks + Minecraft.getInstance().getTimer().getGameTimeDeltaTicks())/20f, 0, -30, !timers.isEmpty() ? 0x00FFFF : 0x707070, true, matrixStack.last().pose(), renderTypeBuffer, Font.DisplayMode.NORMAL, 0x0, light);
//        matrixStack.popPose();

        poseStack.pushPose();
        //Vec3 pos = livingEntity.getPosition(partialTicks);
        //poseStack.translate(pos.x, pos.y, pos.z);
        poseStack.scale(r+ii*0.5f,r+ii*0.5f,r+ii*0.5f);
        poseStack.mulPose(Axis.YP.rotation(r*0.5f));
        poseStack.mulPose(Axis.YP.rotation((livingEntity.tickCount + partialTicks)/30f));
        if (r > 0.025) {
            RenderSystem.setShader(GameRenderer::getPositionColorTexLightmapShader);

            VertexConsumer builder =
                    Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(
                            RenderUtils.getRenderType(ResourceLocation.fromNamespaceAndPath(RelicsOfRain.MODID, "textures/white.png")));

            for (int i = 0; i < RenderUtils.icosahedronTriangleIndicies.length; i += 3) {
                builder.addVertex(poseStack.last(), RenderUtils.icosahedronVertices.get(RenderUtils.icosahedronTriangleIndicies[i]))    .setColor(0, (int) (30*ii), (int) (40*ii), 30).setUv(0,0).setLight(LightTexture.pack(15,15));
                builder.addVertex(poseStack.last(), RenderUtils.icosahedronVertices.get(RenderUtils.icosahedronTriangleIndicies[i + 1])).setColor(0, (int) (10*ii), (int) (40*ii), 30).setUv(0,1).setLight(LightTexture.pack(15,15));
                builder.addVertex(poseStack.last(), RenderUtils.icosahedronVertices.get(RenderUtils.icosahedronTriangleIndicies[i + 2])).setColor(0, (int) (20*ii), (int) (40*ii), 30).setUv(1,1).setLight(LightTexture.pack(15,15));
            }

        }
        poseStack.popPose();
    }

    @SubscribeEvent
    public static void renderLevel(RenderLevelStageEvent e) {
        if (e.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            for (int id : lerpedRadius.keySet()) {
                if (!(e.getCamera().getEntity().level().getEntity(id) instanceof LivingEntity livingEntity)) continue;
                ItemStack stack = EntityUtils.findEquippedCurio(livingEntity, ItemRegistry.FROST_RELIC.get());
                if (!(stack.getItem() instanceof IRelicItem relic)) continue;

                PoseStack p = e.getPoseStack();
                Vec3 cp = e.getCamera().getPosition();
                float partialTick = e.getPartialTick().getGameTimeDeltaPartialTick(!Minecraft.getInstance().isPaused());
                Vec3 pp = livingEntity.getPosition(partialTick);

                p.pushPose();
                p.translate(pp.x-cp.x, pp.y-cp.y+livingEntity.getBbHeight()/2f, pp.z-cp.z);
                renderIceStorm(p, livingEntity, stack, partialTick);
                p.popPose();
            }

            Minecraft.getInstance().renderBuffers().bufferSource().endLastBatch();
        }
    }
}
